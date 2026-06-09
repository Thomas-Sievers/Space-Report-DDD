package br.com.spacereport.infrastructure.config;

import br.com.spacereport.domain.port.out.*;
import br.com.spacereport.domain.service.AlertService;
import br.com.spacereport.domain.service.RiskAnalysisService;
import br.com.spacereport.domain.service.SpaceEventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public RiskAnalysisService riskAnalysisService(RiskAnalysisRepository riskAnalysisRepository) {
        return new RiskAnalysisService(riskAnalysisRepository);
    }

    @Bean
    public AlertService alertService(AlertRepository alertRepository, AlertNotifier alertNotifier) {
        return new AlertService(alertRepository, alertNotifier);
    }

    @Bean
    public SpaceEventService spaceEventService(SpaceEventRepository spaceEventRepository,
                                               SpaceAssetRepository spaceAssetRepository,
                                               RiskAnalysisService riskAnalysisService,
                                               AlertService alertService) {
        return new SpaceEventService(spaceEventRepository, spaceAssetRepository,
                riskAnalysisService, alertService);
    }
}
