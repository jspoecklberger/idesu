package csp.algorithm;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class QuickXplainTest {

    @Test
    public void testQuickXplain() {
        Model m = new Model();

        IntVar a = m.intVar(0, 10);
        IntVar b = m.intVar(0, 10);
        IntVar c = m.intVar(0, 10);

        //Implicit knowledgebase
        m.arithm(a, ">", 1).post();
        m.arithm(b, ">", 2).post();
        m.arithm(c, ">", 3).post();

        //Conflictset
        Set<Constraint> CS = new HashSet<>();
        CS.add(m.arithm(a, ">", b));
        CS.add(m.arithm(b, ">", c));
        CS.add(m.arithm(c, ">", a));

        for (Constraint cstr : CS) {
            cstr.post();
        }

        QuickXplain qp = new QuickXplain();
        Set<Constraint> minimalConflictSet = qp.quickXplainChoco(CS, m, false);

        Assert.assertEqualsNoOrder(CS.toArray(), minimalConflictSet.toArray());
    }
}
