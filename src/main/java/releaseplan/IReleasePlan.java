package releaseplan;

import java.util.List;


/**
 * This interface specifies an abstract release plan
 */
public interface IReleasePlan {

    /**
     *
     * @return Ids of all releases.
     */
    List<Integer> getReleases();

    /**
     * Gets the requirements specified by a releaseId
     * @param releasePlanId
     * @return a list of all requirements that are assigned to this release. Returns null if the Id can not be found
     */
    List<Integer> getRequirements(Integer releasePlanId);

    /**
     * Gets the effort of a requirement. Returns null if the Id can not be found
     * @param requirementId
     * @return the effort
     */
    Integer getRequirementEffort(Integer requirementId);

    /**
     * Gets the effort of a release. Returns null if the Id can not be found
     * @param releaseId
     * @return the effort
     */
    Integer getReleaseCapacity(Integer releaseId);

    /**
     * Gets the requirements that are not assigned to a release yet.
     * @return the unassigned requirements
     */
    List<Integer> getUnassignedRequirements();

    /**
     * Gets the priority of a requirement. Returns null if the Id can not be found
     * A high priority means a low value and vice versa.
     * @param requirementId
     * @return the priority
     */
    Integer getRequirementPriority(Integer requirementId);
}
