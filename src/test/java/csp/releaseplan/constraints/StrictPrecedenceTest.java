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


public class StrictPrecedenceTest {

    @Test
    public void testStrictconstraints0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 0, 1),
                ConstraintHelper.createBinaryDependency(2, ConstraintType.LOWER_THAN, 1, 0),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 2);
    }

    @Test
    public void testStrictconstraints1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 0, 1),
                ConstraintHelper.createBinaryDependency(2, ConstraintType.LOWER_THAN, 1, 0),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testStrictconstraints2() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 1, 0),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testStrictconstraints3() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 0, 1),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }

}