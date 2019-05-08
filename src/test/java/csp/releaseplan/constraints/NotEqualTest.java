package csp.releaseplan.constraints;


import csp.releaseplan.ReleasePlanConsistencyModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.Arrays;
import java.util.stream.Collectors;


public class NotEqualTest {

    @Test
    public void testNotEqual0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.NOT_EQUAL, 0, 1)
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertNotNull(m.getDiagnosis());
    }

    @Test
    public void testNotEqual1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.NOT_EQUAL, 0, 1)
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }
}