package releaseplan;

import org.testng.collections.Lists;

import java.util.*;
import java.util.stream.Collectors;


//This class is used to simplify release plan creation.
//In this IReleaseplan implementation the elements indices act as id so that there is no need to explicitly define ids
public class IndexBasedReleasePlanDto implements IReleasePlan {

    public List<Integer> releases = new ArrayList<>();
    public HashMap<Integer, Integer[]> requirements = new HashMap<>();
    public ArrayList<Integer[]> requirementEffort = new ArrayList<>();
    public List<ConstraintDto> constraints = new ArrayList<>();
    public Integer[] releaseCapacity;

    public IndexBasedReleasePlanDto(ConstraintDto[] constraints, Integer[] noRequirementsPerRelease) {
        this(constraints, noRequirementsPerRelease, null, null);
    }

    public IndexBasedReleasePlanDto(ConstraintDto[] constraints, Integer[] noRequirementsPerRelease,
                                    ArrayList<Integer[]> requirementEffort, Integer[] releaseCapacity) {
        int k = 1;
        for (int i = 0; i < noRequirementsPerRelease.length; i++) {
            releases.add((int) i + 1);
            Integer[] requirementIds = new Integer[noRequirementsPerRelease[i]];
            for (int j = 0; j < noRequirementsPerRelease[i]; j++) {
                requirementIds[j] = k++;
            }
            requirements.put((int) i + 1, requirementIds);
        }
        this.requirementEffort = requirementEffort;

        this.releaseCapacity = releaseCapacity;

        if(constraints != null) {
            this.constraints = Arrays.asList(constraints);
        }
    }

    @Override
    public List<Integer> getReleases() {
        return releases;
    }

    public List<Integer> getRequirements() {
        return requirements.entrySet().stream().flatMap(listContainer -> Arrays.stream(listContainer.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getRequirements(Integer releaseId) {
        return Lists.newArrayList(((Integer[]) requirements.get(releaseId)));
    }

    @Override
    public Integer getRequirementEffort(Integer requirementId) {

        if (requirementEffort == null) {
            return 0;
        }

        int k = 1;
        for (int i = 0; i < requirementEffort.size(); i++) {
            for (int j = 0; j < requirementEffort.get(i).length; j++) {
                if (k == requirementId) {
                    return requirementEffort.get(i)[j];
                }
                k++;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public ArrayList<ConstraintDto> getConstraints() {
        if (constraints == null) {
            return null;
        }
        return (ArrayList<ConstraintDto>) constraints.stream().collect(Collectors.toList());
    }

    @Override
    public Integer getReleaseEffort(Integer releaseId) {
        return releaseCapacity[releaseId - 1];
    }
}
