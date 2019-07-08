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


public class WeakPrecedenceTest {

    @Test
    public void testWeakPrecedenc0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.LOWER_EQUAL_THAN, 0, 1),
                ConstraintHelper.createBinaryDependency(2, ConstraintType.LOWER_EQUAL_THAN, 1, 0),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }

    @Test
    public void testWeakPrecedence1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.LOWER_EQUAL_THAN, 0, 1),
                ConstraintHelper.createBinaryDependency(2, ConstraintType.LOWER_EQUAL_THAN, 1, 0),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testWeakPrecedence2() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.GREATER_THAN, 0, 1),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testWeakPrecedence3() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0,1, 1};

        Constraint[] constraints = new Constraint[]{
                ConstraintHelper.createBinaryDependency(1, ConstraintType.GREATER_THAN, 0, 1),
                ConstraintHelper.createBinaryDependency(2, ConstraintType.GREATER_THAN, 1, 0),
        };

        IndexBasedReleasePlan test = new IndexBasedReleasePlan(noRequirementsPerRelease);

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }
}