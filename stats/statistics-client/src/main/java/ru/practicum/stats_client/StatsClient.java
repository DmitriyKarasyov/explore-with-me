package ru.practicum.stats_client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statistics_service.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatsClient {
    private final RestTemplate rest;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    public ResponseEntity<Object> postHit(EndpointHitDto endpointHitDto) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, endpointHitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris,
                                           @Nullable Boolean unique) {
        String startString = start.format(formatter);
        String endString = end.format(formatter);
        if (uris != null && !uris.isEmpty() && unique != null) {
            return getStatsInUrisAndUnique(startString, endString, uris, unique);
        } else if (uris != null && !uris.isEmpty()) {
            return getStatsInUris(startString, endString, uris);
        } else if (unique != null) {
            return getStatsUnique(startString, endString, unique);
        } else {
            return getStatsAllUrisNotUnique(startString, endString);
        }
    }

    private ResponseEntity<Object> getStatsInUrisAndUnique(String start, String end, List<String> uris,
                                                           Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return makeAndSendRequest(HttpMethod.GET, "stats/?start={start}&end={end}&uris={uris}&unique={unique}",
                parameters, null);
    }

    private ResponseEntity<Object> getStatsInUris(String start, String end, List<String> uris) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris
        );
        return makeAndSendRequest(HttpMethod.GET, "stats/?start={start}&end={end}&uris={uris}",
                parameters, null);
    }

    private ResponseEntity<Object> getStatsUnique(String start, String end, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "unique", unique
        );
        return makeAndSendRequest(HttpMethod.GET, "stats/?start={start}&end={end}&unique={unique}",
                parameters, null);
    }

    private ResponseEntity<Object> getStatsAllUrisNotUnique(String start, String end) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end
        );
        return makeAndSendRequest(HttpMethod.GET, "stats/?start={start}&end={end}",
                parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> ewmServerResponse;
        try {
            if (parameters != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(ewmServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
