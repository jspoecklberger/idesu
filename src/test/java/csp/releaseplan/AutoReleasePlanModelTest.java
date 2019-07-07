package csp.releaseplan;

import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.ConstraintDto;
import releaseplan.ConstraintDtoHelper;
import releaseplan.IReleasePlan;
import releaseplan.IndexBasedReleasePlanDto;

import java.util.List;
import java.util.stream.Collectors;

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

    @Test
    public void testConsistent2() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{6, 0, 0, 0};
        Integer[] releaseCapacity = new Integer[]{4, 3, 4};
        Integer[] requirementEfforts = new Integer[]{1, 1, 1, 2, 2, 4};
        Integer[] requirementPriorities = new Integer[]{1, 1, 1, 2, 2, 3};

        IndexBasedReleasePlanDto dto = new IndexBasedReleasePlanDto(
                noRequirementsPerRelease, requirementEfforts, releaseCapacity
                , requirementPriorities);

        List<ConstraintDto> constraintDtos = ConstraintDtoHelper.createCapacityConstraintDtos(dto);

        AutoReleasePlanModel m = new AutoReleasePlanModel(dto, constraintDtos);
        m.build();
        Assert.assertNull(m.getDiagnosis());
        IReleasePlan rp = m.getSolution();
        List<Integer> test = rp.getRequirements(3);
        Assert.assertTrue(rp.getRequirements(3).get(0) == 5);
        Assert.assertTrue(rp.getUnassignedRequirements() == null || rp.getUnassignedRequirements().size() == 0);
        List<Integer> reqRelease0 = rp.getRequirements(1).stream()
                .map(rp::getRequirementPriority).collect(Collectors.toList());
        Assert.assertTrue(reqRelease0.contains(1) && reqRelease0.contains(2));

        m.printCurrentSolution();
    }
}
