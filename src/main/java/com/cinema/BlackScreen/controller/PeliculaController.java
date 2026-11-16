package com.cinema.BlackScreen.controller;

import com.cinema.BlackScreen.entity.Pelicula;
import com.cinema.BlackScreen.exception.RecursoNoEncontradoException;
import com.cinema.BlackScreen.repository.PeliculaRepository;
import com.cinema.BlackScreen.service.PeliculaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService){
        this.peliculaService = peliculaService;
    }

    @GetMapping
    public List<Pelicula> consultarPeliculas(){
        return peliculaService.consultarPeliculas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> buscarPorId(@PathVariable Integer id){
        Pelicula pelicula = peliculaService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una película con el ID: " + id
                ));

        return ResponseEntity.ok(pelicula);

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
