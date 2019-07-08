package csp.releaseplan.constraints;


import csp.releaseplan.ReleasePlanConsistencyModel;
import org.testng.Assert;
import org.testng.annotations.Test;
import releaseplan.Constraint;
import releaseplan.ConstraintHelper;
import releaseplan.IndexBasedReleasePlan;

import java.util.List;


public class CapacityTest {

    @Test
    public void testCapacityconstraint() throws Exception {

        Integer[] noRequirementsPerRelease = new Integer[]{0, 1, 2};
        Integer[] releaseCapacity = new Integer[]{4, 3};
        Integer[] requirementEfforts = new Integer[]{1, 2, 2};

        IndexBasedReleasePlan dto = new IndexBasedReleasePlan(noRequirementsPerRelease, requirementEfforts, releaseCapacity,
                null);
        List<Constraint> constraints = ConstraintHelper.createCapacityConstraints(dto);
        ReleasePlanConsistencyModel m = new ReleasePlanConsistencyModel(dto, constraints);
        m.build();

        Assert.assertTrue(m.getDiagnosis().size() == 1);

        dto.releaseCapacity[1] = 4;
        constraints = ConstraintHelper.createCapacityConstraints(dto);
        m = new ReleasePlanConsistencyModel(dto, constraints);
        m.build();
        Assert.assertNull(m.getDiagnosis());
    }
}