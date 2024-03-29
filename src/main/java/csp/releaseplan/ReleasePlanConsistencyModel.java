package csp.releaseplan;

import csp.CommonConstraints;
import csp.ConstraintManager;
import csp.ConstraintMapping;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import releaseplan.Constraint;
import releaseplan.ConstraintType;
import releaseplan.IReleasePlan;

import java.util.*;
import java.util.stream.Collectors;

public class ReleasePlanConsistencyModel extends BaseModel {

    private List<IntVar[]> requirementEffortByRelease_;

    public ReleasePlanConsistencyModel(IReleasePlan releasePlan, List<Constraint> constraints) {
        super(releasePlan, constraints);
    }

    public void build() {
        m = new Model("Releaseplan");

        List<Integer> nonEmptyReleases = datasource_.getReleases()
                .stream()
                .filter(x -> datasource_.getRequirements(x) != null && datasource_.getRequirements(x).size() != 0)
                .collect(Collectors.toList());

        List<Integer>[] requirementsPerRelease = getRequirementsPerRelease(nonEmptyReleases);
        requirementEffortByRelease_ = modelRequirementEffortByRelease(nonEmptyReleases, requirementsPerRelease);

        int noRequirements = Arrays.stream(requirementsPerRelease)
                .mapToInt(x -> x.size())
                .sum();

        releasePlan_ = new IntVar[noRequirements];

        HashMap<Integer, Integer> requirementIdToIndex = new HashMap<>();
        HashMap<Integer, Integer> releaseIdToIndex = new HashMap<>();

        int releasePlanRequirementIndex = 0;
        for (int releasePlanIndex = 0; releasePlanIndex < nonEmptyReleases.size(); releasePlanIndex++) {
            Integer release = nonEmptyReleases.get(releasePlanIndex);
            releaseIdToIndex.put(release, releasePlanIndex);
            for (Integer requirement : datasource_.getRequirements(nonEmptyReleases.get(releasePlanIndex))) {
                requirementIdToIndex.put(requirement, releasePlanRequirementIndex);
                releasePlan_[releasePlanRequirementIndex++] = m.intVar("release", releasePlanIndex + 1);
            }
        }

        List<ConstraintMapping> constraintMappings_ = null;
        if (constraints_ != null) {
            constraintMappings_ = createConstraints(requirementIdToIndex, constraints_, releaseIdToIndex);
        }
        constraintManager = new ConstraintManager(m, constraintMappings_);
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

    private List<IntVar[]> modelRequirementEffortByRelease(List<Integer> releases, List<Integer>[] requirementsPerRelease) {

        List<IntVar[]> requirementEffortByRelease = new ArrayList<>();
        for (int i = 0; i < releases.size(); i++) {
            IntVar[] effort = new IntVar[requirementsPerRelease[i].size()];
            int k = 0;
            for (Integer requirement : datasource_.getRequirements(releases.get(i))) {
                Integer requirementEffort = datasource_.getRequirementEffort(requirement);
                if (requirementEffort == null) {
                    requirementEffort = 0;
                }

                effort[k] = m.intVar(requirementEffort);
                k++;
            }
            requirementEffortByRelease.add(effort);
        }

        return requirementEffortByRelease;
    }

    private ArrayList<ConstraintMapping> createConstraints(HashMap<Integer, Integer> requirementIdToIndex,
                                                           List<Constraint> dtos,
                                                           HashMap<Integer, Integer> releaseIdToIndex) {

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
                        Integer maxCapacity = constraint.getCapacity();
                        int indexRelease = releaseIdToIndex.getOrDefault(constraint.getXid(), -1);
                        c = CommonConstraints.createCapacity(m, maxCapacity, requirementEffortByRelease_.get(indexRelease));
                        break;
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

    public Set<ConstraintMapping> getDiagnosis() {

        Set<ConstraintMapping> Diagnosis = null;
        if (!checkConsistency()) {
            Diagnosis = constraintManager.getDiagnosis(false);
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

    public void printCurrentSolution() {

        System.out.println("release plan:");
        for (int i = 0; i < releasePlan_.length; i++) {
            System.out.println(releasePlan_[i].getValue());
        }
        System.out.println("efforts plan:");
        for (int i = 0; i < requirementEffortByRelease_.size(); i++) {
            System.out.println("release index " + i);
            for (int j = 0; j < requirementEffortByRelease_.get(i).length; j++) {
                System.out.println(requirementEffortByRelease_.get(i)[j].getValue());
            }
        }
    }
}
