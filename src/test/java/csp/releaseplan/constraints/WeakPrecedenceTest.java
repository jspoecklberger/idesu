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


public class WeakPrecedenceTest {

    @Test
    public void testWeakPrecedenc0() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_EQUAL_THAN, 0, 1),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_EQUAL_THAN, 1, 0),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }

    @Test
    public void testWeakPrecedence1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_EQUAL_THAN, 0, 1),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_EQUAL_THAN, 1, 0),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();

        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testWeakPrecedence2() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 2};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.GREATER_THAN, 0, 1),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }

    @Test
    public void testWeakPrecedence3() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0,1, 1};

        ConstraintDto[] constraints = new ConstraintDto[]{
                ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.GREATER_THAN, 0, 1),
                ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.GREATER_THAN, 1, 0),
        };

        IndexBasedReleasePlanDto test = new IndexBasedReleasePlanDto(noRequirementsPerRelease);

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(test, Arrays.stream(constraints).collect(Collectors.toList()));
        m.build();
        Assert.assertTrue(m.getDiagnosis() != null && m.getDiagnosis().size() == 1);
    }
}