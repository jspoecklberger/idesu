package csp.releaseplan.constraints;


import csp.releaseplan.ReleasePlanConsistencyModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.List;


public class CapacityTest {

    @Test
    public void testCapacityconstraint() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 2};
        Integer[] releaseCapacity = new Integer[]{4, 3};
        Integer[] requirementEfforts = new Integer[]{1, 2, 2};

        IndexBasedReleasePlanDto dto = new IndexBasedReleasePlanDto(noRequirementsPerRelease, requirementEfforts, releaseCapacity,
                null);
        List<ConstraintDto> constraintDtos = ConstraintDtoHelper.createCapacityConstraintDtos(dto);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(dto, constraintDtos);
        m.build();

        Assert.assertTrue(m.getDiagnosis().size() == 1);

        dto.releaseCapacity[1] = 4;
        constraintDtos = ConstraintDtoHelper.createCapacityConstraintDtos(dto);
        m = new ReleasePlanConsistencyModel(dto, constraintDtos);
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }
}