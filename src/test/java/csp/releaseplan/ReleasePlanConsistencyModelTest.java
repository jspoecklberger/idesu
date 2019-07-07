package csp.releaseplan;

import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.ConstraintType;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.ArrayList;
import java.util.List;

public class ReleasePlanConsistencyModelTest {

    @Test
    public void testConsistent1() throws Exception {
        Integer[] noRequirementsPerRelease = new Integer[]{0, 3, 2, 1};
        Integer[] releaseCapacity = new Integer[]{4, 3, 4};
        Integer[] requirementEfforts = new Integer[]{1, 1, 1, 2, 1, 4};

        IndexBasedReleasePlanDto dto = new IndexBasedReleasePlanDto(noRequirementsPerRelease, requirementEfforts, releaseCapacity, null);
        List<ConstraintDto> constraintDtos = ConstraintDtoHelper.createCapacityConstraintDtos(dto);

        constraintDtos.add(ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_EQUAL_THAN, 0, 1));
        constraintDtos.add(ConstraintDtoHelper.createBinaryDependency(2, ConstraintType.LOWER_THAN, 0, 3));
        constraintDtos.add(ConstraintDtoHelper.createBinaryDependency(3, ConstraintType.GREATER_EQUAL_THAN, 3, 0));
        constraintDtos.add(ConstraintDtoHelper.createBinaryDependency(3, ConstraintType.GREATER_EQUAL_THAN, 4, 3));
        constraintDtos.add(ConstraintDtoHelper.createBinaryDependency(3, ConstraintType.NOT_EQUAL, 4, 5));

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(dto, constraintDtos);
        m.build();
        Assert.assertNull(m.getDiagnosis());

        m.printCurrentSolution();
    }

    @Test
    public void testInconsistentCapacity() throws Exception {
        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 2};
        Integer[] releaseCapacity = new Integer[]{3, 4};
        Integer[] requirementEfforts = new Integer[]{3, 3, 3};

        IndexBasedReleasePlanDto dto = new IndexBasedReleasePlanDto(noRequirementsPerRelease, requirementEfforts, releaseCapacity, null);
        List<ConstraintDto> constraintDtos = ConstraintDtoHelper.createCapacityConstraintDtos(dto);

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(dto, constraintDtos);
        m.build();
        Assert.assertTrue(m.getDiagnosisDtos().get(0).getType() == ConstraintType.CAPACITY);
    }


    @Test
    public void testInconsistent1() throws Exception {
        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 2};
        Integer[] releaseCapacity = new Integer[]{3, 4};
        Integer[] requirementEfforts = new Integer[]{3, 3, 3};

        IndexBasedReleasePlanDto dto = new IndexBasedReleasePlanDto(noRequirementsPerRelease, requirementEfforts, releaseCapacity, null);
        List<ConstraintDto> constraintDtos = new ArrayList<>();
        constraintDtos.add(ConstraintDtoHelper.createBinaryDependency(1, ConstraintType.LOWER_THAN, 0, 1));
        constraintDtos.add(ConstraintDtoHelper.createBinaryDependency(3, ConstraintType.EQUAL, 0, 1));

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(dto, constraintDtos);
        m.build();
        Assert.assertTrue(m.getDiagnosisDtos().size() == 1);
    }
}
