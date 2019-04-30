package csp.releaseplan.constraints;


import csp.ConstraintMapping;
import csp.releaseplan.ReleasePlanModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.Set;


public class DifferentTest {

    @Test
    public void testDifferent0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.EXCLUDES, 1, 2)
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(constraints, noRequirementsPerRelease);
        ReleasePlanModel m = new ReleasePlanModel(test);
        m.build();
        Set<ConstraintMapping> diagnosis = m.getDiagnosis();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }
}