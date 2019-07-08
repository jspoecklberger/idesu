package csp.releaseplan;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import csp.CommonConstraints;
import csp.ConstraintMapping;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import releaseplan.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoReleasePlanModel extends BaseModel {

    /**
     * This factor is used for soft optimization. A low value means prioritizing a complete plan.
     * A high value means prioritizing high priority requirements in early releases.
     * The value is dependent on the priority scaling.
     */
    public int priorityCompletionTradeOffFactor;
    //bidirectional mapping
    private BiMap<Integer, Integer> requirementIdToIndex;
    private BiMap<Integer, Integer> releaseIdToIndex;
    private IntVar[][] releaseEfforts_; //All Releases all requirements
    private IntVar objective_;
    private IntVar[] fitnessReleasePlanAbs;

    public AutoReleasePlanModel(IReleasePlan releasePlan, List<Constraint> constraints) {
        super(releasePlan, constraints);
        priorityCompletionTradeOffFactor = 5;
    }

    /**
     * Builds the model defined by the datasource.
     */
    public void build() {
        m = new Model("Releaseplan");

        List<Integer> nonEmptyReleases = new ArrayList<>(datasource_.getReleases());
        CreateIdToIndexMappings(nonEmptyReleases);

        int noReleases = datasource_.getReleases().size();
        int noUnassignedRequirements = datasource_.getUnassignedRequirements() == null ?
                0 : datasource_.getUnassignedRequirements().size();

        List<Integer>[] requirementsPerRelease = getRequirementsPerRelease(nonEmptyReleases);
        int noRequirements = Arrays.stream(requirementsPerRelease)
                .mapToInt(List::size)
                .sum() + noUnassignedRequirements;

        releasePlan_ = m.intVarArray(noRequirements, 0, noReleases);
        modelCapacities(noReleases, noRequirements);
        modelSoftConstraintOptimization(noReleases, noRequirements);

        if (constraints_ != null) {
            constraintMappings_ = createConstraints(constraints_);
        }
    }

    private void modelCapacities(int noReleases, int noRequirements) {
        //we dont model release 0 since it is the group for unassigned requirements
        releaseEfforts_ = new IntVar[noReleases][noRequirements];

        for (int release = 0; release < noReleases; release++) {
            for (int requirement = 0; requirement < noRequirements; requirement++) {
                Integer effort = datasource_.getRequirementEffort(getRequirementId(requirement));
                releaseEfforts_[release][requirement] =
                        m.intVar(new int[]{0, effort});


                m.ifThenElse(m.arithm(releasePlan_[requirement], "=", release + 1),
                        m.arithm(releaseEfforts_[release][requirement], "=", effort),
                        m.arithm(releaseEfforts_[release][requirement], "=", 0));
            }
            Integer releaseCapacity = datasource_.getReleaseCapacity(getReleaseId(release));

            if (releaseCapacity != null && releaseCapacity > 0) {
                m.sum(releaseEfforts_[release], "<=", releaseCapacity).post();
            }
        }
    }

    private void modelSoftConstraintOptimization(int noReleases, int noRequirements) {
        //we start by assigning fitness values to each requirement such that requirements assigned to the highest release
        //yield the lowest base fitness value.
        IntVar[] fitnessReleasePlan_ = m.intVarArray(noRequirements, 1, noReleases + 1);
        IntVar[] fitnessReleasePlanMinus = new IntVar[noRequirements];
        fitnessReleasePlanAbs = new IntVar[noRequirements];
        IntVar[] fitnessReleasePlus = new IntVar[noRequirements];

        int[] priorities_ = new int[noRequirements];
        for (int i = 0; i < fitnessReleasePlan_.length; i++) {
            //Some transformation to get the right order.
            fitnessReleasePlanMinus[i] = m.intOffsetView(releasePlan_[i], -(noReleases + 1));
            fitnessReleasePlanAbs[i] = m.intAbsView(fitnessReleasePlanMinus[i]);
            fitnessReleasePlus[i] = m.intOffsetView(fitnessReleasePlanAbs[i], 1);
            //Unfortunately, there is no 'where'-clause in Choco as for example in MiniZinc.
            //Therefore we have to model the assignment of fitness values like this:
            m.ifThenElse(m.arithm(fitnessReleasePlus[i], "=", noReleases + 2),
                    m.arithm(fitnessReleasePlan_[i], "=", 0),
                    m.arithm(fitnessReleasePlan_[i], "=", fitnessReleasePlus[i]));

            priorities_[i] = priorityCompletionTradeOffFactor * datasource_.getRequirementPriority(getRequirementId(i));
        }

        int maxPriority = Arrays.stream(priorities_).max().getAsInt();
        IntVar[] fitnessValues = m.intVarArray(noRequirements, 0,
                Arrays.stream(priorities_).max().getAsInt() * priorityCompletionTradeOffFactor * noReleases);
        objective_ = m.intVar("objective", 0, noRequirements * priorityCompletionTradeOffFactor * noReleases * noReleases);
        for (int requirement = 0; requirement < noRequirements; requirement++) {
            m.times(fitnessReleasePlan_[requirement], priorities_[requirement], fitnessValues[requirement]).post();
        }
        m.sum(fitnessValues, "=", objective_).post();
        setObjective();
    }

    private void setObjective() {
        if (objective_ != null) {
            m.setObjective(Model.MAXIMIZE, objective_);
            m.getSolver().limitSolution(1);
        }
    }

    private Integer getRequirementIndex(Integer id) {
        return requirementIdToIndex.get(id);
    }

    private Integer getRequirementId(Integer index) {
        return requirementIdToIndex.inverse().get(index);
    }

    private Integer getReleaseId(Integer index) {
        return releaseIdToIndex.inverse().get(index);
    }

    private Integer getReleaseIndex(Integer id) {
        return releaseIdToIndex.get(id);
    }

    private void CreateIdToIndexMappings(List<Integer> nonEmptyReleases) {
        //create mapping between external ids and internal index
        requirementIdToIndex = HashBiMap.create();
        releaseIdToIndex = HashBiMap.create();

        int releasePlanRequirementIndex = 0;
        for (int releasePlanIndex = 0; releasePlanIndex < nonEmptyReleases.size(); releasePlanIndex++) {
            Integer release = nonEmptyReleases.get(releasePlanIndex);
            releaseIdToIndex.put(release, releasePlanIndex);
            for (Integer requirement : datasource_.getRequirements(nonEmptyReleases.get(releasePlanIndex))) {
                requirementIdToIndex.put(requirement, releasePlanRequirementIndex++);
            }
        }

        if (datasource_.getUnassignedRequirements() != null) {
            for (Integer requirement : datasource_.getUnassignedRequirements()) {
                requirementIdToIndex.put(requirement, releasePlanRequirementIndex++);
            }
        }
    }
    private List<Integer>[] getRequirementsPerRelease(List<Integer> releases) {
        List<Integer>[] requirementsPerRelease = new ArrayList[releases.size()];
        for (int i = 0; i < releases.size(); i++) {
            List<Integer> reqs = datasource_.getRequirements(releases.get(i));
            if (reqs != null) {
                requirementsPerRelease[i] = new ArrayList<>(reqs);
            }
        }
        return requirementsPerRelease;
    }

    private ArrayList<ConstraintMapping> createConstraints(List<Constraint> dtos) {

        ArrayList<ConstraintMapping> result = new ArrayList<>();
        if (dtos != null) {
            for (int i = 0; i < dtos.size(); i++) {
                Constraint constraint = dtos.get(i);
                ConstraintType ctype = constraint.getType();

                ConstraintMapping mapping = new ConstraintMapping();
                Integer rx = constraint.getXid();
                Integer ry = constraint.getYid();

                int indexRx = (rx != null && rx > 0) ? (constraint.getType() == ConstraintType.CAPACITY ?
                        releaseIdToIndex.get(rx) : requirementIdToIndex.get(rx)) : 0;
                int indexRy = (rx != null && ry != null) ? requirementIdToIndex.get(ry) : 0;

                org.chocosolver.solver.constraints.Constraint c = null;
                IntVar x_ = releasePlan_[indexRx];
                IntVar y_ = releasePlan_[indexRy];

                switch (ctype) {
                    case EQUAL:
                        c = CommonConstraints.createEqual(m, x_, y_);
                        break;
                    case NOT_EQUAL:
                        c = CommonConstraints.createDifferent(m, x_, y_);
                        break;
                    case OR:
                        c = CommonConstraints.createEitherOr(m, x_, y_);
                        break;
                    case AT_LEAST_ONE:
                        c = CommonConstraints.createAtLeastOneIsNotNull(m, x_, y_);
                        break;
                    case AT_LEASTONE_A:
                        c = CommonConstraints.createAtLeastOneHasValueK(m, x_, y_, constraint.getK());
                        break;
                    case NO_LATER_THAN:
                        c = CommonConstraints.createNoLaterThan(m, x_, y_);
                        break;
                    case NOT_EARLIER_THAN:
                        c = CommonConstraints.createNotEarlierThan(m, x_, y_);
                        break;
                    case FIXED:
                        c = CommonConstraints.createFixed(m, x_, constraint.getK());
                        break;
                    case EXCLUDES:
                        c = CommonConstraints.createDifferent(m, x_, y_);
                        break;
                    case LOWER_THAN:
                        c = CommonConstraints.createStrictPrecedence(m, x_, y_);
                        break;
                    case LOWER_EQUAL_THAN:
                        c = CommonConstraints.createWeakPrecedence(m, x_, y_);
                        break;
                    case GREATER_THAN:
                        c = CommonConstraints.createStrictPrecedence(m, y_, x_);
                        break;
                    case GREATER_EQUAL_THAN:
                        c = CommonConstraints.createWeakPrecedence(m, y_, x_);
                        break;
                    case CAPACITY:
                        break;
                      /*  Integer maxCapacity = constraint.getCapacity();
                        int indexRelease = releaseIdToIndex.getOrDefault(constraint.getXid(), -1);
                        c = CommonConstraints.createCapacity(m, maxCapacity, releaseEfforts_[indexRelease]);
*/
                }
                if (c != null) {   // throw new Exception("Constrainttype not found in this model");
                    mapping.dto = constraint;
                    mapping.c = c;
                    c.post();
                    result.add(mapping);
                }
            }
        }
        return result;
    }

    /**
     * Gets the Diagnosed Constraints
     *
     * @return the diagnosed ConstraintMappings.
     */
    public Set<ConstraintMapping> getDiagnosis() {

        Set<ConstraintMapping> Diagnosis = null;
        if (!checkConsistency()) {
            m.clearObjective();
            Diagnosis = constraintManager.getDiagnosis(m, constraintMappings_, false);
        }
        return Diagnosis;
    }

    public List<Constraint> getDiagnosedConstraints() {

        Set<ConstraintMapping> diagnosis = getDiagnosis();
        if (diagnosis == null) {
            return null;
        }
        return diagnosis.stream().map(x -> x.dto).collect(Collectors.toList());
    }

    /**
     * prints the current solution.
     */
    public void printCurrentSolution() {
        System.out.println("release plan:");
        for (int i = 0; i < releasePlan_.length; i++) {
            System.out.println(releasePlan_[i].getValue()
                    + "(fitness value" + fitnessReleasePlanAbs[i].getValue() + ")");
        }
        if (objective_ != null) {
            System.out.println("fitness value:" + objective_.getValue());
        }
        System.out.println("efforts plan:");
        for (int i = 0; i < releaseEfforts_.length; i++) {
            System.out.println("release index " + (i + 1));
            for (int j = 0; j < releaseEfforts_[0].length; j++) {
                System.out.println(releaseEfforts_[i][j].getValue());
            }
        }
    }

    /**
     * Gets the current solution as IReleasePlan.
     *
     * @return the solution.
     */
    public IReleasePlan getSolution() {
        List<Integer> releases = datasource_.getReleases();
        List<Release> releaseDtos = new ArrayList<>();
        for (int i = 0; i < releases.size(); i++) {
            Integer releaseId = releases.get(i);
            Integer releaseIndex = getReleaseIndex(releaseId);
            List<Integer> assignedRequirements = new ArrayList<>();
            for (int j = 0; j < releasePlan_.length; j++) {
                if (releasePlan_[j].getValue() == releaseIndex + 1) {
                    assignedRequirements.add(getRequirementId(j));
                }
            }
            Release dto = new Release(releaseId, assignedRequirements, datasource_.getReleaseCapacity(releaseId));
            releaseDtos.add(dto);
        }
        List<Requirement> requirements = new ArrayList<>();
        for (int i = 0; i < releasePlan_.length; i++) {
            Integer id = getRequirementId(i);
            Requirement requirement = new Requirement(id,
                    datasource_.getRequirementEffort(id),
                    datasource_.getRequirementPriority(id));
            requirements.add(requirement);
        }

        SimpleReleasePlan rp = new SimpleReleasePlan(requirements, releaseDtos);

        return rp;
    }
}
