package releaseplan;

import java.util.ArrayList;
import java.util.List;

public class ConstraintHelper {
    public static Constraint createBinaryDependency(int id, ConstraintType type, int xid, int yid) {
        return new Constraint(type, xid, yid, 0, 1, id, true, null);
    }

    public static Constraint createBinaryDependency(int id, ConstraintType type, int xid, int yid, int k) {
        return new Constraint(type, xid, yid, k, 1, id, true, null);
    }

    public static Constraint createUnaryConstraint(int id, ConstraintType type, int xid, int k) {
        return new Constraint(type, xid, null, k, 1, id, true, null);
    }

    public static Constraint createCapacitConstraint(int capacity, int releaseId) {
        return new Constraint(ConstraintType.CAPACITY, releaseId, null, null, 1, null, true, capacity);
    }

    public static List<Constraint> createCapacityConstraints(IReleasePlan releasePlan) {

        List<Constraint> list = new ArrayList<>();
        for (Integer release : releasePlan.getReleases()) {
            int releaseCapacity = releasePlan.getReleaseCapacity(release);
            if (releaseCapacity > 0) {
                list.add(ConstraintHelper.createCapacitConstraint(releaseCapacity, release));
            }
        }
        return list;
    }
}
