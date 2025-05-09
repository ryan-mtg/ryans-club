package club.ryans.models;

import club.ryans.models.generators.MissionGenerator;
import club.ryans.models.generators.SystemGenerator;
import club.ryans.models.inventory.DataCollector;
import club.ryans.models.generators.BuildingGenerator;
import club.ryans.models.generators.DataFileManager;
import club.ryans.models.generators.OfficerGenerator;
import club.ryans.models.generators.ResearchGenerator;
import club.ryans.models.generators.ResourceGenerator;
import club.ryans.models.generators.ShipGenerator;
import club.ryans.models.items.RequirementDescriber;
import club.ryans.models.managers.AllianceManager;
import club.ryans.models.managers.BuildingManager;
import club.ryans.models.managers.ItemManagerContainer;
import club.ryans.models.managers.MissionManager;
import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.ResearchManager;
import club.ryans.models.managers.ResourceManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.SystemManager;
import club.ryans.stfcspace.CachingStfcSpaceClient;
import club.ryans.stfcspace.StfcSpaceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class ModelsConfig {
    private static final boolean shouldGenerate = false;
    private static final String THUMBS_VERSION = "DknUb-gZ";

    private final StfcSpaceClient stfcSpaceClient;
    private final AssetManager assetManager;

    private final DataFileManager dataFileManager;

    public ModelsConfig() {
        stfcSpaceClient = new CachingStfcSpaceClient();
        assetManager = new AssetManager(THUMBS_VERSION);
        dataFileManager = new DataFileManager(shouldGenerate, new File("data"));
        if (shouldGenerate) {
            new ShipGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new OfficerGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new BuildingGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new ResourceGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new ResearchGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new SystemGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new MissionGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();

            java.lang.System.exit(0);
        }
    }

    @Bean
    public BuildingManager buildingManager(final ResourceManager resourceManager) {
        return new BuildingManager(dataFileManager);
    }

    @Bean
    public OfficerManager officerManager() {
        return new OfficerManager(dataFileManager, assetManager);
    }

    @Bean
    public ResourceManager resourceManager(final AssetManager assetManager) {
        return new ResourceManager(dataFileManager, assetManager);
    }

    @Bean
    public ResearchManager researchManager() {
        return new ResearchManager(dataFileManager);
    }

    @Bean
    public ShipManager shipManager(final ResourceManager resourceManager) {
        return new ShipManager(dataFileManager, assetManager, resourceManager);
    }

    @Bean
    public MissionManager missionManager(final SystemManager systemManager) {
        return new MissionManager(dataFileManager, systemManager);
    }

    @Bean
    public SystemManager systemManager() {
        return new SystemManager(dataFileManager);
    }

    @Bean
    public AllianceManager allianceManager(final ResourceManager resourceManager) {
        return new AllianceManager(resourceManager);
    }

    @Bean
    public DataCollector collector(final ItemManagerContainer itemManagerContainer) {
        return new DataCollector(itemManagerContainer);
    }

    @Bean
    public RequirementDescriber requirementDescriber(final ItemManagerContainer itemManagerContainer) {
        return new RequirementDescriber(itemManagerContainer);
    }

    @Bean
    public AssetManager assetManager() {
        return assetManager;
    }
}