package com.cinema.BlackScreen.service;

import com.cinema.BlackScreen.entity.Pelicula;
import com.cinema.BlackScreen.repository.PeliculaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeliculaService {
    private final PeliculaRepository peliculaRepository;
    public PeliculaService(PeliculaRepository peliculaRepository){
        this.peliculaRepository = peliculaRepository;
    }

    public List<Pelicula> consultarPeliculas(){
        return peliculaRepository.findAll();

    }

    public Optional<Pelicula> buscarPorId(Integer id){
        return peliculaRepository.findById(id);

    }
    public Pelicula guardarPelicula(Pelicula pelicula){
        return peliculaRepository.save(pelicula);

    }

    public void eliminarPelicula(Integer id){
        peliculaRepository.deleteById(id);
    }



}
