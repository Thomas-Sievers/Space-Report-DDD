package br.com.spacereport.infrastructure.adapter.out;

import br.com.spacereport.domain.exception.DonkiApiException;
import br.com.spacereport.domain.model.EventType;
import br.com.spacereport.domain.model.SpaceEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class NasaDonkiClient {

    private static final Logger log = LoggerFactory.getLogger(NasaDonkiClient.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;
    public NasaDonkiClient(
            @Value("${donki.api.base-url}") String baseUrl,
            @Value("${donki.api.key}") String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<SpaceEvent> fetchEvents(LocalDate startDate, LocalDate endDate) {
        List<SpaceEvent> events = new ArrayList<>();
        events.addAll(fetchCmeEvents(startDate, endDate));
        events.addAll(fetchSepEvents(startDate, endDate));
        return events;
    }

    private List<SpaceEvent> fetchCmeEvents(LocalDate startDate, LocalDate endDate) {
        String url = buildUrl("/CME", startDate, endDate);
        String json = get(url);
        return parseCmeResponse(json);
    }

    private List<SpaceEvent> fetchSepEvents(LocalDate startDate, LocalDate endDate) {
        String url = buildUrl("/SEP", startDate, endDate);
        String json = get(url);
        return parseSepResponse(json);
    }

    private String buildUrl(String endpoint, LocalDate start, LocalDate end) {
        return baseUrl + endpoint
                + "?startDate=" + start.format(DATE_FORMAT)
                + "&endDate=" + end.format(DATE_FORMAT)
                + "&api_key=" + apiKey;
    }

    private String get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new DonkiApiException("DONKI API returned HTTP " + response.statusCode() + " for " + url);
            }
            return response.body();
        } catch (DonkiApiException e) {
            throw e;
        } catch (Exception e) {
            throw new DonkiApiException("Failed to reach DONKI API: " + url, e);
        }
    }

    private List<SpaceEvent> parseCmeResponse(String json) {
        List<SpaceEvent> events = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.isArray()) return events;
            for (JsonNode node : root) {
                String activityId = node.path("activityID").asText("UNKNOWN");
                String startTime = node.path("startTime").asText(LocalDate.now().toString());
                LocalDate eventDate = parseDate(startTime);

                double speed = 0.0;
                JsonNode analyses = node.path("cmeAnalyses");
                if (analyses.isArray() && !analyses.isEmpty()) {
                    speed = analyses.get(0).path("speed").asDouble(0.0);
                }

                events.add(new SpaceEvent(
                        null,
                        EventType.CME,
                        node.path("sourceLocation").asText(null),
                        speed,
                        eventDate,
                        "CME event: " + activityId
                ));
            }
        } catch (Exception e) {
            log.error("Failed to parse CME response", e);
        }
        return events;
    }

    private List<SpaceEvent> parseSepResponse(String json) {
        List<SpaceEvent> events = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.isArray()) return events;
            for (JsonNode node : root) {
                String activityId = node.path("sepID").asText("UNKNOWN");
                String startTime = node.path("eventTime").asText(LocalDate.now().toString());
                LocalDate eventDate = parseDate(startTime);

                double flux = node.path("flux").asDouble(0.0);

                events.add(new SpaceEvent(
                        null,
                        EventType.SEP,
                        node.path("instruments").asText(null),
                        flux,
                        eventDate,
                        "SEP event: " + activityId
                ));
            }
        } catch (Exception e) {
            log.error("Failed to parse SEP response", e);
        }
        return events;
    }

    private LocalDate parseDate(String dateTimeStr) {
        try {
            return LocalDate.parse(dateTimeStr.substring(0, 10));
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
