package releaseplan;

import org.testng.collections.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


//This class is used to simplify release plan creation.
//In this IReleaseplan implementation the elements indices act as id so that there is no need to explicitly define ids
//Note that the release index 0 is for unassigned releases.
//Also, note that release 0 doesnt have a capacity for unassinged releases.
public class IndexBasedReleasePlanDto implements IReleasePlan {

    public List<Integer> releases = new ArrayList<>();
    public HashMap<Integer, Integer[]> requirements = new HashMap<>();
    public Integer[] releaseRequirementEffort;
    public Integer[] releaseCapacity;
    Integer[] requirementPriorities;

    public IndexBasedReleasePlanDto(Integer[] noRequirementsPerRelease) {
        this(noRequirementsPerRelease, null, null, null);
    }

    public IndexBasedReleasePlanDto(Integer[] noRequirementsPerRelease,
                                    Integer[] requirementEffort, Integer[] releaseCapacity, Integer[] requirementPriorities) {
        int requirement = 0;
        for (int i = 0; i < noRequirementsPerRelease.length; i++) {
            releases.add(i);
            Integer[] requirementIds = new Integer[noRequirementsPerRelease[i]];
            for (int j = 0; j < noRequirementsPerRelease[i]; j++) {
                requirementIds[j] = requirement++;
            }
            requirements.put(i, requirementIds);
        }
        this.releaseRequirementEffort = requirementEffort;
        this.releaseCapacity = releaseCapacity;
        this.requirementPriorities = requirementPriorities;
    }

    @Override
    public List<Integer> getReleases() {
        return releases.stream().filter(x -> !x.equals(0)).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getRequirements(Integer releaseId) {
        return Lists.newArrayList(((Integer[]) requirements.get(releaseId)));
    }

    @Override
    public Integer getRequirementEffort(Integer requirementId) {

        if (releaseRequirementEffort == null) {
            return 0;
        }

        return releaseRequirementEffort[requirementId];
    }

    @Override
    public Integer getReleaseCapacity(Integer releaseId) {
        return releaseCapacity[releaseId - 1];
    }


    @Override
    public List<Integer> getUnassignedRequirements() {
        return Lists.newArrayList(((Integer[]) requirements.get(0)));
    }

    @Override
    public Integer getRequirementPriority(Integer requirementId) {
        return requirementPriorities == null ? 1 : requirementPriorities[requirementId];
    }
}
