package com.example.appointmentservice.client;

import com.example.appointmentservice.exception.ResourceNotFoundException;
import com.example.appointmentservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class DoctorClient {

    private static final String DOCTOR_URL = "http://doctor-service/api/v1/doctors/{id}";

    private final RestTemplate restTemplate;

    public DoctorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "doctorServiceCB")
    public void ensureDoctorExists(Long doctorId) {
        try {
            restTemplate.getForObject(DOCTOR_URL, Object.class, doctorId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Doctor with id " + doctorId + " not found");
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Cannot reach doctor-service", e);
        }
    }
}
