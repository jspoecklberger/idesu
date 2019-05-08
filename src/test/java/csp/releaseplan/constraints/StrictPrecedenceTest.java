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


public class StrictPrecedenceTest {

    @Test
    public void testStrictconstraints0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 0, 1),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_THAN, 1, 0),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 2);
    }

    @Test
    public void testStrictconstraints1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 0, 1),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_THAN, 1, 0),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testStrictconstraints2() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 1, 0),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testStrictconstraints3() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 0, 1),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }

}