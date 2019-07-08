package csp.releaseplan;

import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.Constraint;
import releaseplan.ConstraintHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlan;

import java.util.ArrayList;
import java.util.List;

public class ReleasePlanConsistencyModelTest {

    @Test
    public void testConsistent1() throws Exception {
        Integer[] noRequirementsPerRelease = new Integer[]{0, 3, 2, 1};
        Integer[] releaseCapacity = new Integer[]{4, 3, 4};
        Integer[] requirementEfforts = new Integer[]{1, 1, 1, 2, 1, 4};

        IndexBasedReleasePlan dto = new IndexBasedReleasePlan(noRequirementsPerRelease, requirementEfforts, releaseCapacity, null);
        List<Constraint> constraints = ConstraintHelper.createCapacityConstraints(dto);

        constraints.add(ConstraintHelper.createBinaryDependency(1, ConstraintType.LOWER_EQUAL_THAN, 0, 1));
        constraints.add(ConstraintHelper.createBinaryDependency(2, ConstraintType.LOWER_THAN, 0, 3));
        constraints.add(ConstraintHelper.createBinaryDependency(3, ConstraintType.GREATER_EQUAL_THAN, 3, 0));
        constraints.add(ConstraintHelper.createBinaryDependency(3, ConstraintType.GREATER_EQUAL_THAN, 4, 3));
        constraints.add(ConstraintHelper.createBinaryDependency(3, ConstraintType.NOT_EQUAL, 4, 5));

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(dto, constraints);
        m.build();
        Assert.assertNull(m.getDiagnosis());

        m.printCurrentSolution();
    }

    @Test
    public void testInconsistentCapacity() throws Exception {
        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 2};
        Integer[] releaseCapacity = new Integer[]{3, 4};
        Integer[] requirementEfforts = new Integer[]{3, 3, 3};

        IndexBasedReleasePlan dto = new IndexBasedReleasePlan(noRequirementsPerRelease, requirementEfforts, releaseCapacity, null);
        List<Constraint> constraints = ConstraintHelper.createCapacityConstraints(dto);

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(dto, constraints);
        m.build();
        Assert.assertTrue(m.getDiagnosedConstraints().get(0).getType() == ConstraintType.CAPACITY);
    }


    @Test
    public void testInconsistent1() throws Exception {
        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 2};
        Integer[] releaseCapacity = new Integer[]{3, 4};
        Integer[] requirementEfforts = new Integer[]{3, 3, 3};

        IndexBasedReleasePlan dto = new IndexBasedReleasePlan(noRequirementsPerRelease, requirementEfforts, releaseCapacity, null);
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(ConstraintHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 0, 1));
        constraints.add(ConstraintHelper.createBinaryDependency(3, ConstraintType.EQUAL, 0, 1));

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(dto, constraints);
        m.build();
        Assert.assertTrue(m.getDiagnosedConstraints().size() == 1);
    }
}
