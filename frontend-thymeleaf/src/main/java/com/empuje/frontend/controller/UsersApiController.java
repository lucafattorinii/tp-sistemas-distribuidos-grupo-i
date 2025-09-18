package com.empuje.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/api/users")
public class UsersApiController {

    @Value("${app.gateway-url:http://localhost:8000}")
    private String gatewayUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> list(@RequestHeader(name = "Authorization", required = false) String auth,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        String url = gatewayUrl + "/users?page=" + page + "&size=" + size;
        HttpHeaders headers = new HttpHeaders();
        if (auth != null) headers.set("Authorization", auth);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestHeader(name = "Authorization", required = false) String auth,
                                    @RequestBody Map<String, Object> body) {
        String url = gatewayUrl + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (auth != null) headers.set("Authorization", auth);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, req, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> get(@RequestHeader(name = "Authorization", required = false) String auth,
                                 @PathVariable("id") Long id) {
        String url = gatewayUrl + "/users/" + id;
        HttpHeaders headers = new HttpHeaders();
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> update(@RequestHeader(name = "Authorization", required = false) String auth,
                                    @PathVariable("id") Long id,
                                    @RequestBody Map<String, Object> body) {
        String url = gatewayUrl + "/users/" + id;
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
        String url = gatewayUrl + "/users/" + id;
        HttpHeaders headers = new HttpHeaders();
        if (auth != null) headers.set("Authorization", auth);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
