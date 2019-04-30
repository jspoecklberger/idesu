package csp;


import csp.algorithm.FastDiag;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.*;
import java.util.stream.Collectors;

public class ConstraintManager {


    public Set<Constraint> getWeightSortedDiagnoseableConstraints(List<ConstraintMapping> mappings) {

        Set<Constraint> c = mappings.stream().filter(x -> x.dto.isDiagnoseable())
                .sorted(Comparator.comparing(ConstraintMapping::getWeight).reversed())
                .map(x -> x.c)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return c;
    }

    public Set<ConstraintMapping> getDiagnosis(Model model, List<ConstraintMapping> mappings, boolean print) {
        FastDiag alg = new FastDiag();
        Set<Constraint> weightedC = getWeightSortedDiagnoseableConstraints(mappings);
        Set<Constraint> diagnosis = alg.fastDiagChoco(weightedC, model, print);
        if (diagnosis == null || diagnosis.size() == 0) {
            System.out.println("could not find diagnoses");
            return null;
        }
        if (print) {
            System.out.println("found diagnoses: " + diagnosis.size() + " of overall" + weightedC.size() + " constraints");
        }
        resetModel(model, mappings);

        Set<ConstraintMapping> diagnosedMappings = new HashSet<>();
        for (Constraint c : diagnosis) {
            ConstraintMapping mapping = mappings.stream().filter(x -> x.c.equals(c)).findFirst().get();
            diagnosedMappings.add(mapping);
        }
        return diagnosedMappings;
    }

    private void resetModel(Model model, List<ConstraintMapping> mappings) {
        model.getSolver().hardReset();
        for (Constraint d : mappings.stream().map(ConstraintMapping::getC).collect(Collectors.toList())) {
            if (d.getStatus() != Constraint.Status.POSTED) {
                d.post();
            }
        }
    }

}
