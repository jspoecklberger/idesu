package csp.releaseplan.constraints;


import csp.ConstraintMapping;
import csp.releaseplan.ReleasePlanConsistencyModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class DifferentTest {

    @Test
    public void testDifferent0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0,2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.EXCLUDES, 0, 1)
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Set<ConstraintMapping> diagnosis = m.getDiagnosis();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }
}