package csp.algorithm;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class FastDiag {

    Set<Constraint> nonKnowledgeBaseConstraints;

    static boolean checkConstraintConsistent(Set<Constraint> AC, Model m, boolean print, Set<Constraint> nonKnowledgeBaseConstraints) {
        assert (!m.getSolver().hasObjective());
        m.getSolver().hardReset();
        for (Constraint c : nonKnowledgeBaseConstraints) {
            if (c.getStatus() == Constraint.Status.POSTED)
                m.unpost(c);
        }
        for (Constraint c : AC) {
            m.post(c);
        }

        return m.getSolver().solve();
    }

    static void split(Set<Constraint> C, Set<Constraint> c1, Set<Constraint> c2) {
        int i = 0;
        int k = C.size() / 2;
        for (Constraint c : C) {
            if (i < k)
                c1.add(c);
            else
                c2.add(c);
            i++;
        }
    }

    static boolean checkValidParameter(Set<Constraint> C, Model m, boolean print, Set<Constraint> nonKnowledgeBaseConstraints) {
        if (C == null || C.isEmpty() ||
                !checkConstraintConsistent(new LinkedHashSet<>(), m, print, nonKnowledgeBaseConstraints)) {

            if (print) {
                if (C == null || C.isEmpty())
                    System.out.println("no constraints specified");
                else
                    System.out.println("inconsistent knowledge base");
            }
            return false;
        }
        return true;
    }

    public Set<Constraint> fastDiagChoco(Set<Constraint> C, Model m, boolean print) {
        this.nonKnowledgeBaseConstraints = C;
        if (!checkValidParameter(C, m, print, nonKnowledgeBaseConstraints))
            return null;

        return FD(null, C, nonKnowledgeBaseConstraints, m, print);
    }

    private Set<Constraint> FD(Set<Constraint> D, Set<Constraint> C, Set<Constraint> AC, Model m, boolean print) {
        if (D != null && checkConstraintConsistent(AC, m, print, nonKnowledgeBaseConstraints))
            return null;

        if (C.size() == 1)
            return C;


        Set<Constraint> C1 = new LinkedHashSet<>();
        Set<Constraint> C2 = new LinkedHashSet<>();

        split(C, C1, C2);

        Set<Constraint> D1 = FD(C2, C1, util.SetUtil.removeSet(AC, C2), m, print);
        Set<Constraint> D2 = FD(D1, C2, util.SetUtil.removeSet(AC, D1), m, print);

        return util.SetUtil.unionSets(D1, D2);
    }
}
