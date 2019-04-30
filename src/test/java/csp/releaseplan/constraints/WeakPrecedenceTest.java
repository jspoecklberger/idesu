package csp.releaseplan.constraints;


import csp.releaseplan.ReleasePlanModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.ArrayList;


public class WeakPrecedenceTest {

    @Test
    public void testWeakPrecedenc0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_EQUAL_THAN, 1, 2),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_EQUAL_THAN, 2, 1),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }

    @Test
    public void testWeakPrecedence1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_EQUAL_THAN, 1, 2),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_EQUAL_THAN, 2, 1),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testWeakPrecedence2() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.GREATER_THAN, 1, 2),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testWeakPrecedence3() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.GREATER_THAN, 1, 2),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.GREATER_THAN, 2, 1),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        test.getConstraints().addAll(ConstraintDtoHelper.createCapacityConstraintDtos(test));

        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }
}