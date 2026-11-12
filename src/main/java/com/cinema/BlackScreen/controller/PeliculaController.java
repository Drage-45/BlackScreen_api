package com.cinema.BlackScreen.controller;

import com.cinema.BlackScreen.entity.Pelicula;
import com.cinema.BlackScreen.repository.PeliculaRepository;
import com.cinema.BlackScreen.service.PeliculaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {
    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService){
        this.peliculaService = peliculaService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> buscarPorId(@PathVariable Integer id){
        return  peliculaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }
    @PostMapping
    public Pelicula registrarPelicula(@RequestBody Pelicula pelicula){
        pelicula.setId(null);
        return peliculaService.guardarPelicula(pelicula);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> actualizarPelicula(
            @PathVariable Integer id,
            @RequestBody Pelicula pelicula
    ) {
        if (peliculaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pelicula.setId(id);
        return  ResponseEntity.ok(
                peliculaService.guardarPelicula(pelicula)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPelicula(@PathVariable Integer id){
        if (peliculaService.buscarPorId(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        peliculaService.eliminarPelicula(id);
        return ResponseEntity.noContent().build();
    }
}
