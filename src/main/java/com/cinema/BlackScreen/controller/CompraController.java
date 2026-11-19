package com.cinema.BlackScreen.controller;

import com.cinema.BlackScreen.entity.Compra;
import com.cinema.BlackScreen.service.CompraService;
import com.cinema.BlackScreen.service.CompraService.CompraRegistrada;
import com.cinema.BlackScreen.service.CompraService.SolicitudCompra;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {
    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public ResponseEntity<List<Compra>> listarVentas() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<CompraRegistrada> registrarCompra(@RequestBody SolicitudCompra compra) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.registrarCompra(compra));
    }
}
