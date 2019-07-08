package csp.releaseplan;

import csp.ConstraintManager;
import csp.ConstraintMapping;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import releaseplan.Constraint;
import releaseplan.IReleasePlan;

import java.util.List;
import java.util.Set;

public abstract class BaseModel {

    public BaseModel(IReleasePlan releasePlan, List<Constraint> constraints) {
        datasource_ = releasePlan;
        constraints_ = constraints;
    }

    protected IReleasePlan datasource_;

    //protected List<ConstraintMapping> constraintMappings_;
    protected ConstraintManager constraintManager;

    protected List<Constraint> constraints_;
    protected Model m;
    protected IntVar[] releasePlan_; //assignes releases to each requirement

    public abstract Set<ConstraintMapping> getDiagnosis();

    public abstract List<Constraint> getDiagnosedConstraints();

    public abstract void printCurrentSolution();

    /**
     * Checks the consistency of the release plan model.
     *
     * @return
     */
    public boolean checkConsistency() {
        return m.getSolver().solve();
    }

    public ConstraintManager getConstraintManager()
    {
        return constraintManager;
    }



}
