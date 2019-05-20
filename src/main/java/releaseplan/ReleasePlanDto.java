package releaseplan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ReleasePlanDto implements IReleasePlan {

    private List<RequirementDto> requirements;
    private List<ReleaseDto> releaseDtos;

    @Override
    public List<Integer> getReleases() {
        return releaseDtos.stream().map(x -> x.getId()).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getRequirements(Integer releasePlanId) {
        Optional<ReleaseDto> rdto = releaseDtos.stream().filter(x -> x.getId().equals(releasePlanId)).findFirst();
        return rdto.isPresent() ? rdto.get().getRequirements() : null;
    }

    @Override
    public Integer getRequirementEffort(Integer requirementId) {
        Optional<RequirementDto> dt0 = requirements.stream().filter(x -> x.getId().equals(requirementId)).findFirst();
        return dt0.isPresent() ? dt0.get().getEffort() : 0;
    }

    @Override
    public Integer getReleaseCapacity(Integer releaseId) {
        Optional<ReleaseDto> dt0 = releaseDtos.stream().filter(x -> x.getId().equals(releaseId)).findFirst();
        return dt0.isPresent() ? dt0.get().getCapacity() : 0;
    }

    @Override
    public List<Integer> getUnassignedRequirements() {
        return null;
    }

    @Override
    public Integer getRequirementPriority(Integer requirementId) {
        Optional<RequirementDto> dt0 = requirements.stream().filter(x -> x.getId().equals(requirementId)).findFirst();
        return dt0.isPresent() ? dt0.get().getPriority() : 0;
    }
}
