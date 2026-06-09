package br.com.spacereport.infrastructure.adapter.out;

import br.com.spacereport.domain.model.Alert;
import br.com.spacereport.domain.model.RiskLevel;
import br.com.spacereport.domain.port.out.AlertNotifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class WebhookNotifier implements AlertNotifier {

    private static final Logger log = LoggerFactory.getLogger(WebhookNotifier.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WebhookNotifier() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void notify(Alert alert) {
        if (alert.getRiskAnalysis().getRiskLevel() != RiskLevel.CRITICAL) {
            return;
        }
        String webhookUrl = alert.getRiskAnalysis().getSpaceAsset()
                .getOrganization().getContactEmail();

        if (webhookUrl == null || !webhookUrl.startsWith("http")) {
            log.warn("No valid webhook URL for org '{}' — skipping notification",
                    alert.getRiskAnalysis().getSpaceAsset().getOrganization().getName());
            return;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "alertId", alert.getId(),
                    "riskLevel", alert.getRiskAnalysis().getRiskLevel().name(),
                    "asset", alert.getRiskAnalysis().getSpaceAsset().getName(),
                    "message", alert.getMessage(),
                    "issueDate", alert.getIssueDate().toString()
            );
            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Webhook sent to '{}' — HTTP {}", webhookUrl, response.statusCode());
        } catch (Exception e) {
            log.error("Failed to send webhook for alert {}", alert.getId(), e);
        }
    }
}
