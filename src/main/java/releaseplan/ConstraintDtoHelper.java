package releaseplan;

import java.util.ArrayList;
import java.util.List;

public class ConstraintDtoHelper {
    public static ConstraintDto createBinaryDependency(int id, ConstraintType type, int xid, int yid) {
        return new ConstraintDto(type, xid, yid, 0, 1, id, true, null);
    }

    public static ConstraintDto createBinaryDependency(int id, ConstraintType type, int xid, int yid, int k) {
        return new ConstraintDto(type, xid, yid, k, 1, id, true, null);
    }

    public static ConstraintDto createUnaryConstraint(int id, ConstraintType type, int xid, int k) {
        return new ConstraintDto(type, xid, null, k, 1, id, true, null);
    }

    public static ConstraintDto createCapacitConstraint(int capacity, int releaseId) {
        return new ConstraintDto(ConstraintType.CAPACITY, releaseId, null, null, 1, null, true, capacity);
    }

    public static List<ConstraintDto> createCapacityConstraintDtos(IReleasePlan releasePlan) {

        List<ConstraintDto> list = new ArrayList<>();
        for (Integer release : releasePlan.getReleases()) {
            int releaseCapacity = releasePlan.getReleaseCapacity(release);
            if (releaseCapacity > 0) {
                list.add(ConstraintDtoHelper.createCapacitConstraint(releaseCapacity, release));
            }
        }
        return list;
    }
}
