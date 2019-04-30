package util;

import org.chocosolver.solver.constraints.Constraint;

import java.util.LinkedHashSet;
import java.util.Set;

public class SetUtil {

    public static Set<Constraint> unionSets(Set<Constraint> a, Set<Constraint> b) {
        if (a == null && b == null)
            return null;

        Set<Constraint> r = new LinkedHashSet<>();
        if (a != null)
            r.addAll(a);
        if (b != null)
            r.addAll(b);

        return r;
    }

    public static Set<Constraint> removeSet(Set<Constraint> a, Set<Constraint> b) {

        if (a == null)
            return null;

        Set<Constraint> r = new LinkedHashSet<>(a);
        if (b != null)
            r.removeAll(b);

        return r;
    }
}
