package csp.releaseplan;

import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.Constraint;
import releaseplan.ConstraintHelper;
import releaseplan.IReleasePlan;
import releaseplan.IndexBasedReleasePlan;

import java.util.List;
import java.util.stream.Collectors;

public class AutoReleasePlanModelTest {

    @Test
    public void testConsistent1() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{6, 0, 0, 0};
        Integer[] releaseCapacity = new Integer[]{4, 3, 4};
        Integer[] requirementEfforts = new Integer[]{1, 1, 1, 2, 1, 4};

        IndexBasedReleasePlan dto = new IndexBasedReleasePlan(
                noRequirementsPerRelease, requirementEfforts, releaseCapacity
                , null);

        List<Constraint> constraints = ConstraintHelper.createCapacityConstraints(dto);

        AutoReleasePlanModel m = new AutoReleasePlanModel(dto, constraints);
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

        IndexBasedReleasePlan dto = new IndexBasedReleasePlan(
                noRequirementsPerRelease, requirementEfforts, releaseCapacity
                , requirementPriorities);

        List<Constraint> constraints = ConstraintHelper.createCapacityConstraints(dto);

        AutoReleasePlanModel m = new AutoReleasePlanModel(dto, constraints);
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
