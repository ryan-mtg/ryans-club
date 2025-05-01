package club.ryans.models.items;

import club.ryans.models.Requirement;
import club.ryans.models.managers.ItemManagerContainer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequirementDescriber {
    private final ItemManagerContainer itemManagerContainer;

    public String describe(final Requirement requirement) {
        switch (requirement.getType()) {
            case "BuildingLevel":
                Building building = itemManagerContainer.getBuilding(requirement.getRequirementId());
                return String.format("%s to be level %d", building.getName(),
                        requirement.getRequiredLevel());
            case "ResearchLevel":
                Research research = itemManagerContainer.getResearchFromStfcSpaceId(requirement.getRequirementId());
                return String.format("%s to be level %d", research.getName(),
                        requirement.getRequiredLevel());
        }
        return "Unknown requirement type: " + requirement.getType();
    }
}
