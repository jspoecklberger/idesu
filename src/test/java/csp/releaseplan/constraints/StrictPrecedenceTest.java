package csp.releaseplan.constraints;


import csp.releaseplan.ReleasePlanModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlanDto;


public class StrictPrecedenceTest {

    @Test
    public void testStrictconstraints0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 1, 2),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_THAN, 2, 1),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 2);
    }

    @Test
    public void testStrictconstraints1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 1, 2),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_THAN, 2, 1),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testStrictconstraints2() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 2, 1),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testStrictconstraints3() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 1, 2),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }

}