package csp.releaseplan.constraints;


import csp.releaseplan.ReleasePlanModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlanDto;


public class FixedTest {

    @Test
    public void testFixed0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.FIXED, 1, 2),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.FIXED, 2, 1)
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 2);
    }
}