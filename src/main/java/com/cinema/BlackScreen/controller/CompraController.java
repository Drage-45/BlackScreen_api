package com.cinema.BlackScreen.controller;

import com.cinema.BlackScreen.entity.Compra;
import com.cinema.BlackScreen.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @GetMapping
    public ResponseEntity<List<Compra>> listarVentas() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<Compra> registrarCompra(@RequestBody Compra compra) {
        if (compra.getFuncion() == null || compra.getFuncion().getId() == null || compra.getCantidadBoletos() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // El servicio maneja internamente el error 409 y 404 lanzando una ResponseStatusException
        Compra nuevaCompra = compraService.registrarCompra(compra);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCompra);
    }
}