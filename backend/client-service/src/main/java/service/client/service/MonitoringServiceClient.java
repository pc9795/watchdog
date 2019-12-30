package service.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.entities.cockroachdb.BaseMonitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * Purpose: TODO:
 **/
@Service
public class MonitoringServiceClient {
    private static Logger LOGGER = LogManager.getLogger(MonitoringServiceClient.class);
    @Value("${monitoring_service_url}")
    private String monitoringServiceUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public MonitoringServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean editMonitor(long monitorId, BaseMonitor monitor) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(String.format("%s/%s", monitoringServiceUrl, monitorId),
                    HttpMethod.PUT, new HttpEntity<>(mapper.writeValueAsString(monitor), headers), String.class);

        } catch (JsonProcessingException e) {
            LOGGER.error(String.format("Error in converting to json:%s for monitor id:%s", e, monitorId));
            return false;
        }
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    public boolean deleteMonitor(long monitorId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity;
        responseEntity = restTemplate.exchange(String.format("%s/%s", monitoringServiceUrl, monitorId),
                HttpMethod.DELETE, new HttpEntity<>("", headers), String.class);

        return responseEntity.getStatusCode().is2xxSuccessful();
    }
}
