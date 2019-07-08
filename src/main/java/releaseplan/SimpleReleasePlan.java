package releaseplan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class SimpleReleasePlan implements IReleasePlan {

    private List<Requirement> requirements;
    private List<Release> releases;

    @Override
    public List<Integer> getReleases() {
        return releases.stream().map(x -> x.getId()).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getRequirements(Integer releasePlanId) {
        Optional<Release> rdto = releases.stream().filter(x -> x.getId().equals(releasePlanId)).findFirst();
        return rdto.isPresent() ? rdto.get().getRequirements() : null;
    }

    @Override
    public Integer getRequirementEffort(Integer requirementId) {
        Optional<Requirement> dt0 = requirements.stream().filter(x -> x.getId().equals(requirementId)).findFirst();
        return dt0.isPresent() ? dt0.get().getEffort() : 0;
    }

    @Override
    public Integer getReleaseCapacity(Integer releaseId) {
        Optional<Release> dt0 = releases.stream().filter(x -> x.getId().equals(releaseId)).findFirst();
        return dt0.isPresent() ? dt0.get().getCapacity() : 0;
    }

    @Override
    public List<Integer> getUnassignedRequirements() {
        return null;
    }

    @Override
    public Integer getRequirementPriority(Integer requirementId) {
        Optional<Requirement> dt0 = requirements.stream().filter(x -> x.getId().equals(requirementId)).findFirst();
        return dt0.isPresent() ? dt0.get().getPriority() : 0;
    }
}
