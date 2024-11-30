package club.ryans.charts;

import club.ryans.charts.parser.BundleParser;
import club.ryans.charts.ex.borg.ExBorgEfficiencyCalculator;
import club.ryans.charts.parser.ParserFactory;
import club.ryans.models.managers.ResourceManager;
import club.ryans.models.managers.ShipManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChartsConfig {
    @Autowired
    private ResourceManager resourceManager;

    @Autowired
    private ShipManager shipManager;

    @Autowired
    private ParserFactory parserFactory;

    @Bean
    public BundleParser bundleParser() {
        return new BundleParser(shipManager, resourceManager);
    }

    @Bean
    public ExBorgEfficiencyCalculator exBorgEfficiencyCalculator(final BundleParser bundleParser) {
        return new ExBorgEfficiencyCalculator(bundleParser, parserFactory);
    }
}