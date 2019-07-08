package csp.releaseplan;

import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleReleasePlanTest {

    @Test
    public void testSimpleReleasePlan() throws Exception {

        int effort = 2;
        int priority = 3;
        List<Requirement> requirements = new ArrayList<Requirement>(
                Arrays.asList(
                        new Requirement(1, effort, priority),
                        new Requirement(2, effort, priority),
                        new Requirement(3, effort, priority),
                        new Requirement(4, effort, priority)));

        int tooLessCapacity = 3;
        List<Release> releases = new ArrayList<Release>(
                Arrays.asList(
                        new Release(10, Arrays.asList(1, 4), tooLessCapacity),
                        new Release(11, Arrays.asList(2, 3), 10)));

        SimpleReleasePlan plan = new SimpleReleasePlan(requirements, releases);

        List<Constraint> constraints = ConstraintHelper.createCapacityConstraints(plan);

        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(plan, constraints);
        m.build();
        Assert.assertFalse(m.checkConsistency());

        List<Constraint> diagnosis = m.getDiagnosedConstraints();
        Assert.assertTrue(diagnosis.size() == 1 && diagnosis.get(0).getConstraintId() == 10);

        constraints.removeIf(x -> m.getDiagnosedConstraints().stream().anyMatch(x::equals));
        m.build();
        Assert.assertTrue(m.checkConsistency());

    }
}
