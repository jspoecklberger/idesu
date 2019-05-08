package csp.releaseplan;

import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.List;

public class AutoReleasePlanModelTest {

    @Test
    public void testConsistent1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{6, 0, 0, 0};
        Integer[] releaseCapacity = new Integer[]{4, 3, 4};
        Integer[] requirementEfforts = new Integer[]{1, 1, 1, 2, 1, 4};

        IndexBasedReleasePlanDto dto = new IndexBasedReleasePlanDto(
                noRequirementsPerRelease, requirementEfforts, releaseCapacity
                , null);

        List<ConstraintDto> constraintDtos = ConstraintDtoHelper.createCapacityConstraintDtos(dto);

        AutoReleasePlanModel m = new AutoReleasePlanModel(dto, constraintDtos);
        m.build();
        Assert.assertNull(m.getDiagnosis());

        m.printCurrentSolution();
    }
}
