package csp.releaseplan.constraints;


import csp.releaseplan.ReleasePlanConsistencyModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.Constraint;
import releaseplan.ConstraintHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlan;

import java.util.Arrays;
import java.util.stream.Collectors;


public class NotEqualTest {

    @Test
    public void testNotEqual0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.NOT_EQUAL, 0, 1)
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertNotNull(m.getDiagnosis());
    }

    @Test
    public void testNotEqual1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.NOT_EQUAL, 0, 1)
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }
}