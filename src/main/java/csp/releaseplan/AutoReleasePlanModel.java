package csp.releaseplan;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import csp.CommonConstraints;
import csp.ConstraintManager;
import csp.ConstraintMapping;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import releaseplan.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoReleasePlanModel {

    //bidirectional mapping
    BiMap<Integer, Integer> requirementIdToIndex;
    BiMap<Integer, Integer> releaseIdToIndex;
    private List<ConstraintMapping> constraintMappings_;
    private ConstraintManager constraintManager = new ConstraintManager();
    private IReleasePlan datasource_;
    private List<ConstraintDto> constraints_;
    private Model m;
    private IntVar[] releasePlan_; //assignes releases to each requirement
    private IntVar[][] releaseEfforts_; //All Releases all requirements
    private IntVar objective_;
    private int[] priorities_;
    private IntVar[] fitnessReleasePlanAbs;
    private IntVar[] fitnessReleasePlanMinus;
    private IntVar[] fitnessReleasePlan_;
    private IntVar[] fitnessReleasePlus;
    private Integer requirementScalingFactor = 5;

    public AutoReleasePlanModel(IReleasePlan releasePlan, List<ConstraintDto> constraints) {
        datasource_ = releasePlan;
        constraints_ = constraints;
    }

    public void build() {

        m = new Model("Releaseplan");

        List<Integer> nonEmptyReleases = datasource_.getReleases()
                .stream()
                .collect(Collectors.toList());

        int noReleases = datasource_.getReleases().size();

        List<Integer>[] requirementsPerRelease = getRequirementsPerRelease(nonEmptyReleases);
        int noRequirements = Arrays.stream(requirementsPerRelease)
                .mapToInt(x -> x.size())
                .sum() + datasource_.getUnassignedRequirements().size();

        CreateIdToIndexMappings(nonEmptyReleases);

        releasePlan_ = m.intVarArray(noRequirements, 0, noReleases);

        modelCapacities(noReleases, noRequirements);

        modelSoftconstraintOptimization(noReleases, noRequirements);

        if (constraints_ != null) {
            constraintMappings_ = createConstraints(constraints_);
        }
    }

    private void modelCapacities(int noReleases, int noRequirements) {

        //we dont model release 0 since it is just a group for unassigned requirements
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

    private void modelSoftconstraintOptimization(int noReleases, int noRequirements) {

        fitnessReleasePlan_ = m.intVarArray(noRequirements, 1, noReleases + 1);
        fitnessReleasePlanMinus = new IntVar[noRequirements];
        fitnessReleasePlanAbs = new IntVar[noRequirements];
        fitnessReleasePlus = new IntVar[noRequirements];

        priorities_ = new int[noRequirements];
        for (int i = 0; i < fitnessReleasePlan_.length; i++) {

            fitnessReleasePlanMinus[i] = m.intOffsetView(releasePlan_[i], -(noReleases + 1));
            fitnessReleasePlanAbs[i] = m.intAbsView(fitnessReleasePlanMinus[i]);
            fitnessReleasePlus[i] = m.intOffsetView(fitnessReleasePlanAbs[i], 1);
            m.ifThenElse(m.arithm(fitnessReleasePlus[i], "=", noReleases + 2),
                    m.arithm(fitnessReleasePlan_[i], "=", 0),
                    m.arithm(fitnessReleasePlan_[i], "=", fitnessReleasePlus[i]));

            priorities_[i] = requirementScalingFactor * datasource_.getRequirementPriority(getRequirementId(i));
        }

        IntVar[] fitnessValues = m.intVarArray(noRequirements, 0,
                Arrays.stream(priorities_).max().getAsInt() * requirementScalingFactor * noReleases);
        objective_ = m.intVar("objective", 0, noRequirements * requirementScalingFactor * noReleases * noReleases);
        for (int requirement = 0; requirement < noRequirements; requirement++) {
            m.times(fitnessReleasePlan_[requirement], priorities_[requirement], fitnessValues[requirement]).post();
        }

        m.sum(fitnessValues, "=", objective_).post();

        setObjective();
    }

    public void setObjective() {
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
        for (Integer requirement : datasource_.getUnassignedRequirements()) {
            requirementIdToIndex.put(requirement, releasePlanRequirementIndex++);
        }
    }

    private List<Integer>[] getRequirementsPerRelease(List<Integer> releases) {
        List<Integer>[] requirementsPerRelease = new ArrayList[releases.size()];
        for (int i = 0; i < releases.size(); i++) {
            requirementsPerRelease[i] = datasource_.getRequirements(releases.get(i));
        }
        return requirementsPerRelease;
    }

    private ArrayList<ConstraintMapping> createConstraints(List<ConstraintDto> dtos) {

        ArrayList<ConstraintMapping> result = new ArrayList<>();
        if (dtos != null) {
            for (int i = 0; i < dtos.size(); i++) {
                ConstraintDto constraintDto = dtos.get(i);
                ConstraintType ctype = constraintDto.getType();

                ConstraintMapping mapping = new ConstraintMapping();
                Integer rx = constraintDto.getXid();
                Integer ry = constraintDto.getYid();

                int indexRx = (rx != null && rx > 0) ? requirementIdToIndex.get(rx) : 0;
                int indexRy = (rx != null && ry != null) ? requirementIdToIndex.get(ry) : 0;

                Constraint c = null;
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
                        c = CommonConstraints.createAtLeastOneHasValueK(m, x_, y_, constraintDto.getK());
                        break;
                    case NO_LATER_THAN:
                        c = CommonConstraints.createNoLaterThan(m, x_, y_);
                        break;
                    case NOT_EARLIER_THAN:
                        c = CommonConstraints.createNotEarlierThan(m, x_, y_);
                        break;
                  /*  case AT_MOST_ONE_A:
                        c = null;// CommonConstraints.createAt(m, x_, y_);
                        break;
                    case VALUE_DEPENDENCY:
                       c = CommonConstraints.createEqual(m, x_, y_);
                      break;
                  */
                    case FIXED:
                        c = CommonConstraints.createFixed(m, x_, constraintDto.getK());
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
                }
                if (c != null) {   // throw new Exception("Constrainttype not found in this model");
                    mapping.dto = constraintDto;
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
            Diagnosis = constraintManager.getDiagnosis(m, constraintMappings_, false);
        }
        return Diagnosis;
    }

    public List<ConstraintDto> getDiagnosisDtos() {

        Set<ConstraintMapping> diagnosis = getDiagnosis();
        if (diagnosis == null) {
            return null;
        }
        return diagnosis.stream().map(x -> x.dto).collect(Collectors.toList());
    }

    public void printCurrentSolution() {

        System.out.println("release plan:");
        for (int i = 0; i < releasePlan_.length; i++) {
            System.out.println(releasePlan_[i].getValue()
                    + "(" + fitnessReleasePlan_[i].getValue() + ")"
                    + "(" + fitnessReleasePlanMinus[i].getValue() + ")"
                    + "(" + fitnessReleasePlanAbs[i].getValue() + ")");
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

    public IReleasePlan getSolution() {
        List<Integer> releases = datasource_.getReleases();
        List<ReleaseDto> releaseDtos = new ArrayList<>();
        for (int i = 0; i < releases.size(); i++) {
            Integer releaseId = releases.get(i);
            Integer releaseIndex = getReleaseIndex(releaseId);
            List<Integer> assignedRequirements = new ArrayList<>();
            for (int j = 0; j < releasePlan_.length; j++) {
                if (releasePlan_[j].getValue() == releaseIndex+1) {
                    assignedRequirements.add(getRequirementId(j));
                }
            }
            ReleaseDto dto = new ReleaseDto(releaseId, assignedRequirements, datasource_.getReleaseCapacity(releaseId));
            releaseDtos.add(dto);
        }
        List<RequirementDto> requirementDtos = new ArrayList<>();
        for (int i = 0; i < releasePlan_.length; i++) {
            Integer id = getRequirementId(i);
            RequirementDto requirementDto = new RequirementDto(id,
                    datasource_.getRequirementEffort(id),
                    datasource_.getRequirementPriority(id));
            requirementDtos.add(requirementDto);
        }

        ReleasePlanDto rp = new ReleasePlanDto(requirementDtos, releaseDtos);

        return rp;
    }

    public boolean checkConsistency() {
        return m.getSolver().solve();
    }
}
