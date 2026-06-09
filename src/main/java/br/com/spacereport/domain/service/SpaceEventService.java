package br.com.spacereport.domain.service;

import br.com.spacereport.domain.model.Alert;
import br.com.spacereport.domain.model.SpaceAsset;
import br.com.spacereport.domain.model.SpaceEvent;
import br.com.spacereport.domain.port.in.AnalyzeRiskUseCase;
import br.com.spacereport.domain.port.in.GenerateAlertUseCase;
import br.com.spacereport.domain.port.in.IngestSpaceEventUseCase;
import br.com.spacereport.domain.port.out.SpaceAssetRepository;
import br.com.spacereport.domain.port.out.SpaceEventRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpaceEventService implements IngestSpaceEventUseCase {

    private final SpaceEventRepository spaceEventRepository;
    private final SpaceAssetRepository spaceAssetRepository;
    private final AnalyzeRiskUseCase analyzeRiskUseCase;
    private final GenerateAlertUseCase generateAlertUseCase;

    public SpaceEventService(SpaceEventRepository spaceEventRepository,
                             SpaceAssetRepository spaceAssetRepository,
                             AnalyzeRiskUseCase analyzeRiskUseCase,
                             GenerateAlertUseCase generateAlertUseCase) {
        this.spaceEventRepository = spaceEventRepository;
        this.spaceAssetRepository = spaceAssetRepository;
        this.analyzeRiskUseCase = analyzeRiskUseCase;
        this.generateAlertUseCase = generateAlertUseCase;
    }

    @Override
    public void ingest(SpaceEvent spaceEvent) {
        SpaceEvent saved = spaceEventRepository.save(spaceEvent);
        List<SpaceAsset> assets = spaceAssetRepository.findAll();
        for (SpaceAsset asset : assets) {
            var analysis = analyzeRiskUseCase.analyze(saved, asset);
            generateAlertUseCase.generate(analysis);
        }
    }

    @Override
    public List<SpaceEvent> ingestFromApi(LocalDate startDate, LocalDate endDate) {
        // Populated by the NASA DONKI adapter calling ingest() per event
        return spaceEventRepository.findAll();
    }
}
