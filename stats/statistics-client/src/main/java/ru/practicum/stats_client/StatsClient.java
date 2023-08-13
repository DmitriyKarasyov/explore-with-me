package ru.practicum.stats_client;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statistics_service.dto.EndpointHitDto;
import ru.practicum.statistics_service.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsClient {
    private final RestTemplate rest;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    public void postHit(EndpointHitDto endpointHitDto) {
        makeAndSendRequest(HttpMethod.POST, "/hit", null, endpointHitDto);
    }

    public ResponseEntity<ViewStatsDto[]> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris,
                                       @Nullable Boolean unique) {
        String startString = start == null ? null : start.format(formatter);
        String endString = end == null ? null : end.format(formatter);
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

    private ResponseEntity<ViewStatsDto[]> getStatsInUrisAndUnique(String start, String end, List<String> uris,
                                                       Boolean unique) {
        HashMap<String, Object> parameters = new HashMap<>(Map.of(
                "uris", uris,
                "unique", unique
        ));
        if (start != null) {
            parameters.put("start", start);
        }
        if (end != null) {
            parameters.put("end", end);
        }
        return getStatsRequest(makePath(parameters));
    }

    private ResponseEntity<ViewStatsDto[]> getStatsInUris(String start, String end, List<String> uris) {
        HashMap<String, Object> parameters = new HashMap<>(Map.of(
                "uris", uris
        ));
        if (start != null) {
            parameters.put("start", start);
        }
        if (end != null) {
            parameters.put("end", end);
        }
        return getStatsRequest(makePath(parameters));
    }

    private ResponseEntity<ViewStatsDto[]> getStatsUnique(String start, String end, Boolean unique) {
        HashMap<String, Object> parameters = new HashMap<>(Map.of(
                "unique", unique
        ));
        if (start != null) {
            parameters.put("start", start);
        }
        if (end != null) {
            parameters.put("end", end);
        }
        return getStatsRequest(makePath(parameters));
    }

    private ResponseEntity<ViewStatsDto[]> getStatsAllUrisNotUnique(String start, String end) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end
        );
        return getStatsRequest(makePath(parameters));
    }

    private ResponseEntity<ViewStatsDto[]> getStatsRequest(String path) {
        return rest.getForEntity(path, ViewStatsDto[].class);
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

    public String makePath(Map<String, Object> parameters) {
        StringBuilder pathBuilder = new StringBuilder("stats/?");
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            pathBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String path = pathBuilder.toString();
        return StringUtils.chop(path);
    }
}