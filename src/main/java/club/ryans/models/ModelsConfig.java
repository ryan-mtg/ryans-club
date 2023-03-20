package club.ryans.models;

import club.ryans.models.generators.BuildingGenerator;
import club.ryans.models.generators.DataFileManager;
import club.ryans.models.generators.OfficerGenerator;
import club.ryans.models.generators.ResourceGenerator;
import club.ryans.models.generators.ShipGenerator;
import club.ryans.models.managers.BuildingManager;
import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.ResourceManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.models.managers.AssetManager;
import club.ryans.stfcspace.StfcSpaceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class ModelsConfig {
    private final StfcSpaceClient stfcSpaceClient;
    private final AssetManager assetManager;
    private final DataFileManager dataFileManager;

    private boolean shouldGenerate = false;
    private boolean generateOnly = false;

    public ModelsConfig() {
        stfcSpaceClient = StfcSpaceClient.newClient();
        assetManager = new AssetManager();
        dataFileManager = new DataFileManager(shouldGenerate, new File("data"));
        if (shouldGenerate) {
            new ShipGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new OfficerGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new BuildingGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();
            new ResourceGenerator(assetManager, stfcSpaceClient, dataFileManager).generate();

            if (generateOnly) {
                System.exit(0);
            }
        }
    }

    @Bean
    public BuildingManager buildingManager() {
        return new BuildingManager(dataFileManager);
    }

    @Bean
    public OfficerManager officerManager() {
        return new OfficerManager(dataFileManager);
    }

    @Bean
    public ResourceManager resourceManager() {
        return new ResourceManager(dataFileManager);
    }

    @Bean
    public ShipManager shipManager() {
        return new ShipManager(dataFileManager);
    }

    @Bean
    public AssetManager assetManager() {
        return assetManager;
    }
}