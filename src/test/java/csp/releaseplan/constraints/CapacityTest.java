package csp.releaseplan.constraints;


import csp.releaseplan.ReleasePlanModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDtoHelper;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.ArrayList;


public class CapacityTest {

    @Test
    public void testCapacityconstraint() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{1, 2};
        Integer[] releaseCapacity = new Integer[]{4, 3};

        ArrayList<Integer[]> requirementEfforts = new ArrayList<>();
        requirementEfforts.add(new Integer[]{1});
        requirementEfforts.add(new Integer[]{2, 2});

        IndexBasedReleasePlanDto dto = new IndexBasedReleasePlanDto(null, noRequirementsPerRelease, requirementEfforts, releaseCapacity);
        dto.constraints.addAll(ConstraintDtoHelper.createCapacityConstraintDtos(dto));
        ReleasePlanModel m = new ReleasePlanModel(dto);
        m.build();

        Assert.assertTrue(m.getDiagnosis().size() == 1);

        dto.releaseCapacity[1] = 4;
        dto.constraints.clear();
        dto.constraints.addAll(ConstraintDtoHelper.createCapacityConstraintDtos(dto));
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }
}