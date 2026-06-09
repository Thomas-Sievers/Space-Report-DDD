package br.com.spacereport.infrastructure.adapter.in;

import br.com.spacereport.domain.model.*;
import br.com.spacereport.domain.port.out.OrganizationRepository;
import br.com.spacereport.domain.port.out.SpaceAssetRepository;
import br.com.spacereport.domain.service.AlertService;
import br.com.spacereport.domain.service.SpaceEventBST;
import br.com.spacereport.domain.service.SpaceEventService;
import br.com.spacereport.infrastructure.adapter.out.NasaDonkiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SpaceReportRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpaceReportRunner.class);

    private final NasaDonkiClient donkiClient;
    private final SpaceEventService spaceEventService;
    private final AlertService alertService;
    private final OrganizationRepository organizationRepository;
    private final SpaceAssetRepository spaceAssetRepository;

    public SpaceReportRunner(NasaDonkiClient donkiClient,
                             SpaceEventService spaceEventService,
                             AlertService alertService,
                             OrganizationRepository organizationRepository,
                             SpaceAssetRepository spaceAssetRepository) {
        this.donkiClient = donkiClient;
        this.spaceEventService = spaceEventService;
        this.alertService = alertService;
        this.organizationRepository = organizationRepository;
        this.spaceAssetRepository = spaceAssetRepository;
    }

    @Override
    public void run(String... args) {
        log.info("=== Space Report — Starting Demo ===");

        seedDemoData();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        log.info("Fetching DONKI events from {} to {}", startDate, endDate);

        List<SpaceEvent> events = donkiClient.fetchEvents(startDate, endDate);
        log.info("Fetched {} space events from NASA DONKI", events.size());

        SpaceEventBST bst = new SpaceEventBST();
        for (SpaceEvent event : events) {
            spaceEventService.ingest(event);
            bst.insert(event);
        }

        log.info("--- Events sorted by intensity (BST in-order) ---");
        bst.inOrder().forEach(e -> log.info("  {}", e));

        log.info("--- Events above intensity threshold 500 (BST lookup) ---");
        bst.findAboveThreshold(500).forEach(e -> log.info("  HIGH INTENSITY: {}", e));

        log.info("--- All generated alerts ---");
        List<Alert> allAlerts = alertService.findAll();
        if (allAlerts.isEmpty()) {
            log.info("  No alerts generated.");
        } else {
            allAlerts.forEach(a -> log.info("  {}", a));
        }

        log.info("--- CRITICAL alerts ---");
        List<Alert> critical = alertService.findCritical();
        if (critical.isEmpty()) {
            log.info("  No CRITICAL alerts.");
        } else {
            critical.forEach(a -> log.warn("  *** CRITICAL *** {}", a));
        }

        demoCrud();

        log.info("=== Space Report — Demo Complete ===");
    }

    private void seedDemoData() {
        Organization org = organizationRepository.save(
                new Organization(null, "AetherLink", "Satellite Operator",
                        "ops@aetherlink.com", "USA"));

        spaceAssetRepository.save(
                new SpaceAsset(null, "AetherLink-LEO-01", AssetType.SATELLITE,
                        "LEO", "OPERATIONAL", org));
        spaceAssetRepository.save(
                new SpaceAsset(null, "AetherLink-GEO-01", AssetType.SATELLITE,
                        "GEO", "OPERATIONAL", org));

        Organization org2 = organizationRepository.save(
                new Organization(null, "Celestia Lunar", "Space Infrastructure",
                        "safety@celestialunar.com", "Brazil"));
        spaceAssetRepository.save(
                new SpaceAsset(null, "Lunar-Base-Alpha", AssetType.LUNAR_BASE,
                        "LUNAR", "OPERATIONAL", org2));

        log.info("Seeded demo organizations and space assets.");
    }

    private void demoCrud() {
        log.info("--- CRUD demo: update ---");
        organizationRepository.findAll().stream()
                .filter(o -> o.getName().equals("AetherLink"))
                .findFirst()
                .ifPresent(org -> {
                    Organization updated = new Organization(
                            org.getId(), org.getName(), org.getType(),
                            "alerts@aetherlink.com", org.getCountry());
                    organizationRepository.update(updated);
                    log.info("  Updated AetherLink contact email to alerts@aetherlink.com");
                });

        log.info("--- CRUD demo: delete ---");
        spaceAssetRepository.findAll().stream()
                .filter(a -> a.getName().equals("AetherLink-GEO-01"))
                .findFirst()
                .ifPresent(asset -> {
                    spaceAssetRepository.delete(asset.getId());
                    log.info("  Deleted asset: {}", asset.getName());
                });
    }
}
