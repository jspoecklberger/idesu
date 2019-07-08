package csp.releaseplan;

import csp.ConstraintManager;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.Constraint;
import releaseplan.ConstraintHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlan;

import java.util.Arrays;
import java.util.stream.Collectors;

public class IndexbasedReleasePlanTest {

    @Test
    public void testQuickXplain() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{3, 0, 0, 0};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.GREATER_THAN, 0, 1),
                ConstraintHelper.createBinaryDependency(2, ConstraintType.GREATER_THAN, 1, 2),
                ConstraintHelper.createBinaryDependency(3, ConstraintType.GREATER_THAN, 2, 0),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        AutoReleasePlanModel m = new AutoReleasePlanModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        int size = m.getConflicts().size();
        Assert.assertTrue(size == 3);
    }
}
