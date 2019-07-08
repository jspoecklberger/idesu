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


public class FixedTest {

    @Test
    public void testFixed0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.FIXED, 0, 1),
                ConstraintHelper.createBinaryDependency(2, ConstraintType.FIXED, 1, 0)
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 2);
    }
}