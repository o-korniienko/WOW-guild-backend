package com.wowguild.sender;

import com.wowguild.tool.LogHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class HttpSender implements Sender {

    private final LogHandler logHandler;

    @Override
    public String sendRequest(String url, HttpMethod method, String clientId, String clientSecret) {
        String result = "";
        try {
            MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<String, String>();
            bodyParamMap.add("grant_type", "client_credentials");
            bodyParamMap.add("client_id", clientId);
            bodyParamMap.add("client_secret", clientSecret);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorisation", "Basic");
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyParamMap, headers);
            RestTemplate template = new RestTemplate(getClientHttpRequestFactory());
            ResponseEntity<String> response = template.exchange(url, method, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                result = response.getBody();
            }
        } catch (RestClientException e) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - sendRequest1 got error: " + e.getMessage());
            System.out.println("URL: " + url);
        }
        return result;
    }

    @Override
    public String sendRequest(String url, HttpMethod method, String token) {
        String result = "";
        try {
            MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.trim());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyParamMap, headers);


            RestTemplate template = new RestTemplate(getClientHttpRequestFactory());
            ResponseEntity<String> response = template.exchange(new URI(url.trim()), method, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                result = response.getBody();
            }
        } catch (RestClientException e) {
            String log = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - sendRequest2 got error: " + e.getMessage() +
                    "; URL: " + url + "; http method: " + method;
            logHandler.saveLog(log, "http requests");
        } catch (URISyntaxException e) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - sendRequest2 got error: " + e.getMessage());
        }
        return result;
    }

    @Override
    public String sendRequest(String url, Map<String, String> body, HttpMethod method, String token) {
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type", "application/json");
            headers.set("Authorisation", "Bearer");
            headers.setBearerAuth(token);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            RestTemplate template = new RestTemplate(getClientHttpRequestFactory());
            ResponseEntity<String> response = template.exchange(url, method, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                result = response.getBody();
            }
        } catch (RestClientException e) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - sendRequest3 got error: " + e.getMessage());
            System.out.println("URL: " + url);
            System.out.println("body: " + body);

            if (Objects.requireNonNull(e.getMessage()).contains("429 Too Many Requests")){
                result = "429 Too Many Requests";
            }
        }
        return result;
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactory.setConnectTimeout(30 * 1000);

        //Read timeout
        clientHttpRequestFactory.setReadTimeout(30 * 1000);
        return clientHttpRequestFactory;
    }


    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

}
