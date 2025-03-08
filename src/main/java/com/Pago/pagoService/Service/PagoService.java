package com.Pago.pagoService.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class PagoService {

    private final RestTemplate restTemplate;

    public PagoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Método principal que intenta enviar el POST a Aldeamo
    @CircuitBreaker(name = "pedidosCircuitBreaker", fallbackMethod = "enviarATwilio")
    public ResponseEntity<String> enviarPedido() {
        String urlAldeamo = "http://localhost:8081/notificacion"; // URL de Aldeamo

        Map<String, Object> requestBody = new HashMap<>();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(urlAldeamo, HttpMethod.POST, request, String.class);
    
        // Imprimir la respuesta completa para depurar
        System.out.println("Código de estado: " + response.getStatusCode());
        System.out.println("Cuerpo de la respuesta: " + response.getBody());
    
        guardarRespuestaEnArchivo("Respuesta Aldeamo: " + response.getBody());
        return response;
    
        //enviarATwilio(e);
        
    }

    // Método fallback que envía la solicitud a Twilio si Aldeamo falla
    public ResponseEntity<String> enviarATwilio(Throwable t) {
        String urlTwilio = "http://localhost:8082/notificacion"; // URL de Twilio

        Map<String, Object> requestBody = new HashMap<>();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(urlTwilio, HttpMethod.POST, request, String.class);
            guardarRespuestaEnArchivo("Respuesta Twilio: " + response.getBody());
            return response;
        } catch (Exception e) {
            guardarRespuestaEnArchivo("Error al enviar a Twilio: " + e.getMessage());
            return ResponseEntity.status(500).body("No se pudo enviar la notificación ni a Aldeamo ni a Twilio");
        }
    }

    private void guardarRespuestaEnArchivo(String respuesta) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("respuestas.txt", true))) {
            writer.write(respuesta);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }
}
