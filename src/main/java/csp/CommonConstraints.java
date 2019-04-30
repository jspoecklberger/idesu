package csp;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class CommonConstraints {

    public static Constraint createAtLeastOneIsNotNull(Model m, IntVar x, IntVar y) {
        return m.not(m.and(m.arithm(x, "=", 0), (m.arithm(y, "=", 0))));
    }

    public static Constraint createAtLeastOneHasValueK(Model m, IntVar x, IntVar y, Integer k) {
        return m.not(m.and(m.arithm(x, "=", k), (m.arithm(y, "=", k))));
    }

    public static Constraint createEqual(Model m, IntVar x, IntVar y) {
        return m.arithm(x, "=", y);
    }

    public static Constraint createDifferent(Model m, IntVar x, IntVar y) {
        return m.or(m.arithm(x, "!=", y), m.arithm(x, "=", 0));
    }

    public static Constraint createEitherOr(Model m, IntVar x, IntVar y_) {
        return m.or(
                m.and(m.arithm(x, ">", 0), (m.arithm(y_, "=", 0))),
                m.and(m.arithm(x, "=", 0), (m.arithm(y_, ">", 0)))
        );
    }

    public static Constraint createFixed(Model m, IntVar x, Integer k) {
        return m.arithm(x, "=", k);
    }

    public static Constraint createNoLaterThan(Model m, IntVar x, IntVar y) {
        return m.arithm(x, "<=", y);
    }

    public static Constraint createNotEarlierThan(Model m, IntVar x, IntVar y) {
        return m.arithm(x, ">=", y);
    }

    public static Constraint createStrictPrecedence(Model m, IntVar x, IntVar y) {
        return m.and(m.arithm(x, "<", y), m.arithm(x, ">", 0));
    }

    public static Constraint createWeakPrecedence(Model m, IntVar x, IntVar y) {
        return m.and(m.arithm(x, "<=", y), m.arithm(x, ">", 0));
    }

    public static Constraint createCapacity(Model m, Integer maxCapacity, IntVar[] intVars) {
        return m.sum(intVars, "<=", maxCapacity);
    }
}
