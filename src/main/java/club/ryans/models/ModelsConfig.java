package club.ryans.models;

import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.models.managers.AssetManager;
import club.ryans.stfcspace.StfcSpaceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelsConfig {
    private static StfcSpaceClient stfcSpaceClient;
    private static AssetManager assetManager;
    private boolean loadFromFiles = true;

    public ModelsConfig() {
        stfcSpaceClient = StfcSpaceClient.newClient();
        assetManager = new AssetManager();
    }

    @Bean
    public OfficerManager officerManager() {
        return new OfficerManager(stfcSpaceClient, assetManager, loadFromFiles);
    }

    @Bean
    public ShipManager shipManager() {
        return new ShipManager(stfcSpaceClient, assetManager, loadFromFiles);
    }

    @Bean
    public AssetManager assetManager() {
        return assetManager;
    }
}