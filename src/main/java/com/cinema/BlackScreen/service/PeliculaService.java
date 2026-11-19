package com.cinema.BlackScreen.service;

import com.cinema.BlackScreen.entity.Pelicula;
import com.cinema.BlackScreen.repository.PeliculaRepository;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PeliculaService {
    private final PeliculaRepository peliculaRepository;

    public PeliculaService(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    public List<Pelicula> consultarPeliculas() {
        return peliculaRepository.findAll();
    }

    public Optional<Pelicula> buscarPorId(Integer id) {
        return peliculaRepository.findById(id);
    }

    public Pelicula guardarPelicula(Pelicula pelicula) {
        pelicula.setSinopsis(textoOpcional(pelicula.getSinopsis()));
        String poster = textoOpcional(pelicula.getPosterUrl());
        if (poster != null) {
            enlaceWeb(poster, "El póster debe usar una dirección HTTP o HTTPS válida");
        }
        pelicula.setPosterUrl(poster);
        pelicula.setTrailerUrl(normalizarTrailer(pelicula.getTrailerUrl()));
        return peliculaRepository.save(pelicula);
    }

    public void eliminarPelicula(Integer id) {
        peliculaRepository.deleteById(id);
    }

    private String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.strip();
    }

    private URI enlaceWeb(String valor, String mensaje) {
        URI uri;
        try {
            uri = URI.create(valor);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
        }
        if (uri.getHost() == null || uri.getUserInfo() != null
                || !("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
        }
        return uri;
    }

    private String normalizarTrailer(String valor) {
        String enlace = textoOpcional(valor);
        if (enlace == null) {
            return null;
        }

        String mensaje = "Introduce un enlace válido a un video de YouTube";
        URI uri = enlaceWeb(enlace, "El tráiler debe usar una dirección HTTP o HTTPS válida");
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        boolean youtube = List.of("youtube.com", "www.youtube.com", "m.youtube.com").contains(host);
        if (!youtube && !"youtu.be".equals(host)) {
            return enlace;
        }
        String ruta = uri.getPath();
        String id = "";
        if ("youtu.be".equals(host) && ruta.startsWith("/")) {
            id = ruta.substring(1);
        } else if (youtube) {
            if ("/watch".equals(ruta) && uri.getRawQuery() != null) {
                id = Arrays.stream(uri.getRawQuery().split("&"))
                        .filter(parametro -> parametro.startsWith("v="))
                        .map(parametro -> parametro.substring(2))
                        .findFirst().orElse("");
            } else if (ruta.startsWith("/embed/") || ruta.startsWith("/shorts/")) {
                id = ruta.substring(ruta.indexOf('/', 1) + 1);
            }
        }
        if (!id.matches("[A-Za-z0-9_-]{11}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
        }
        return "https://www.youtube.com/watch?v=" + id;
    }
}
