package csp;


import csp.algorithm.FastDiag;
import csp.algorithm.QuickXplain;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.*;
import java.util.stream.Collectors;

public class ConstraintManager {

    Model m;
    List<ConstraintMapping> mappings;

    public ConstraintManager(Model m, List<ConstraintMapping> mappings) {
        this.m = m;
        this.mappings = mappings;
    }

    public Set<Constraint> getWeightSortedDiagnoseableConstraints() {
        Set<Constraint> c = mappings.stream().filter(x -> x.dto.isDiagnoseable())
                .sorted(Comparator.comparing(ConstraintMapping::getWeight).reversed())
                .map(x -> x.c)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return c;
    }

    public Set<ConstraintMapping> getDiagnosis(boolean print) {
        FastDiag alg = new FastDiag();
        Set<Constraint> weightedC = getWeightSortedDiagnoseableConstraints();
        Set<Constraint> diagnosis = alg.fastDiagChoco(weightedC, m, print);
        if (diagnosis == null || diagnosis.size() == 0) {
            if (print)
                System.out.println("could not find diagnoses");
            return null;
        }
        if (print) {
            System.out.println("found diagnoses: " + diagnosis.size() + " of overall" + weightedC.size() + " constraints");
        }
        resetModel(m, mappings);

        Set<ConstraintMapping> diagnosedMappings = new HashSet<>();
        for (Constraint c : diagnosis) {
            ConstraintMapping mapping = mappings.stream().filter(x -> x.c.equals(c)).findFirst().get();
            diagnosedMappings.add(mapping);
        }
        return diagnosedMappings;
    }

    public Set<ConstraintMapping> getConflicts(boolean print) {
        QuickXplain alg = new QuickXplain();
        Set<Constraint> weightedC = getWeightSortedDiagnoseableConstraints();
        Set<Constraint> conflicts = alg.quickXplainChoco(weightedC, m, print);
        if (conflicts == null || conflicts.size() == 0) {
            if (print)
                System.out.println("could not find conflicts");
            return null;
        }
        if (print) {
            System.out.println("found conflicts: " + conflicts.size() + " of overall" + weightedC.size() + " constraints");
        }
        resetModel(m, mappings);

        Set<ConstraintMapping> conflictMapps = new HashSet<>();
        for (Constraint c : conflicts) {
            ConstraintMapping mapping = mappings.stream().filter(x -> x.c.equals(c)).findFirst().get();
            conflictMapps.add(mapping);
        }
        return conflictMapps;
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
