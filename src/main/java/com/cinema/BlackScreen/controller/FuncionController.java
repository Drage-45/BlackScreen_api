package com.cinema.BlackScreen.controller;

import com.cinema.BlackScreen.entity.Funcion;
import com.cinema.BlackScreen.service.FuncionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/funciones")
@CrossOrigin(origins = "*")
public class FuncionController {

    @Autowired
    private FuncionService funcionService;

    @GetMapping
    public ResponseEntity<List<Funcion>> listar() {
        return ResponseEntity.ok(funcionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcion> buscarPorId(@PathVariable Integer id) {
        Optional<Funcion> funcion = funcionService.buscarPorId(id);
        return funcion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Funcion> crear(@RequestBody Funcion funcion) {
        if (funcion.getPelicula() == null || funcion.getPelicula().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Funcion nuevaFuncion = funcionService.guardar(funcion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFuncion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcion> actualizar(@PathVariable Integer id, @RequestBody Funcion funcion) {
        Optional<Funcion> existente = funcionService.buscarPorId(id);
        if (existente.isPresent()) {
            funcion.setId(id);
            return ResponseEntity.ok(funcionService.guardar(funcion));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        Optional<Funcion> existente = funcionService.buscarPorId(id);
        if (existente.isPresent()) {
            funcionService.eliminar(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}