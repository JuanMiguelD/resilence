package com.Pago.pagoService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Pago.pagoService.Service.PagoService;

@RestController
@RequestMapping("/pedido")
public class PagoController { // Clases en Java deben empezar con mayúscula

    private final PagoService pagoService;

    // Inyección de dependencia a través del constructor
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearPedido() {
        return pagoService.enviarPedido();
    }
}

