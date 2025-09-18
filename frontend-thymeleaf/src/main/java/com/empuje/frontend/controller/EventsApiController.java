package com.empuje.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/api/events")
public class EventsApiController {

    @Value("${app.gateway-url:http://localhost:8000}")
    private String gatewayUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> list(@RequestHeader(name = "Authorization", required = false) String auth) {
        String url = gatewayUrl + "/events";
        HttpHeaders headers = new HttpHeaders();
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestHeader(name = "Authorization", required = false) String auth,
                                    @RequestBody Map<String, Object> body) {
        String url = gatewayUrl + "/events";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> update(@RequestHeader(name = "Authorization", required = false) String auth,
                                    @PathVariable("id") Long id,
                                    @RequestBody Map<String, Object> body) {
        String url = gatewayUrl + "/events/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> delete(@RequestHeader(name = "Authorization", required = false) String auth,
                                    @PathVariable("id") Long id) {
        String url = gatewayUrl + "/events/" + id;
        HttpHeaders headers = new HttpHeaders();
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping(value = "/{id}/assign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> assign(@RequestHeader(name = "Authorization", required = false) String auth,
                                    @PathVariable("id") Long id,
                                    @RequestBody Map<String, Object> body) {
        String url = gatewayUrl + "/events/" + id + "/assign";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping(value = "/{id}/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> remove(@RequestHeader(name = "Authorization", required = false) String auth,
                                    @PathVariable("id") Long id,
                                    @RequestBody Map<String, Object> body) {
        String url = gatewayUrl + "/events/" + id + "/remove";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping(value = "/{id}/participate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> participate(@RequestHeader(name = "Authorization", required = false) String auth,
                                         @PathVariable("id") Long id) {
        String url = gatewayUrl + "/events/" + id + "/participate";
        HttpHeaders headers = new HttpHeaders();
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping(value = "/{id}/leave", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> leave(@RequestHeader(name = "Authorization", required = false) String auth,
                                   @PathVariable("id") Long id) {
        String url = gatewayUrl + "/events/" + id + "/leave";
        HttpHeaders headers = new HttpHeaders();
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
