package csp.algorithm;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.LinkedHashSet;
import java.util.Set;

import static csp.algorithm.FastDiag.checkConstraintConsistent;
import static csp.algorithm.FastDiag.checkValidParameter;
import static csp.algorithm.FastDiag.split;

public class QuickXplain {

    Set<Constraint> nonKnowledgeBaseConstraints;

    public Set<Constraint> quickXplainChoco(Set<Constraint> C, Model m, boolean print) {
        this.nonKnowledgeBaseConstraints = C;

        if (!checkValidParameter(C, m, print, nonKnowledgeBaseConstraints))
            return null;

        return QX(null, C, nonKnowledgeBaseConstraints, m, print);
    }

    private Set<Constraint> QX(Set<Constraint> D, Set<Constraint> C, Set<Constraint> AC, Model m, boolean print) {
        if (D != null && !checkConstraintConsistent(AC, m, print, nonKnowledgeBaseConstraints))
            return null;

        if (C.size() == 1)
            return C;

        Set<Constraint> C1 = new LinkedHashSet<>();
        Set<Constraint> C2 = new LinkedHashSet<>();

        split(C, C1, C2);

        Set<Constraint> D1 = QX(C2, C1, util.SetUtil.removeSet(AC, C2), m, print);
        Set<Constraint> D2 = QX(D1, C2, util.SetUtil.removeSet(AC, D1), m, print);

        return util.SetUtil.unionSets(D1, D2);
    }
}
