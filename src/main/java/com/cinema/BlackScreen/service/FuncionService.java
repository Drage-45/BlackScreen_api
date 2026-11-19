package com.cinema.BlackScreen.service;

import com.cinema.BlackScreen.entity.Funcion;
import com.cinema.BlackScreen.repository.ButacaRepository;
import com.cinema.BlackScreen.repository.ButacaRepository.Butaca;
import com.cinema.BlackScreen.repository.FuncionRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FuncionService {
    private final FuncionRepository funciones;
    private final ButacaRepository butacas;

    public FuncionService(FuncionRepository funciones, ButacaRepository butacas) {
        this.funciones = funciones;
        this.butacas = butacas;
    }

    public List<Funcion> listarTodas() {
        return funciones.findAll();
    }

    public Optional<Funcion> buscarPorId(Integer id) {
        return funciones.findById(id);
    }

    public List<Butaca> listarButacas(Integer id) {
        if (!funciones.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Función no encontrada");
        }
        return butacas.listar(id);
    }

    @Transactional
    public Funcion guardar(Funcion funcion) {
        Integer cupos = funcion.getCuposDisponibles();
        // Seis columnas por fila y etiquetas desde A hasta ZZ.
        if (cupos == null || cupos < 0 || cupos > 4212) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Los cupos deben estar entre 0 y 4212");
        }

        funcion.setId(null);
        Funcion creada = funciones.saveAndFlush(funcion);
        if (cupos > 0) {
            butacas.generarMapaDemostracion(creada.getId(), cupos);
        }
        return creada;
    }

    @Transactional
    public Funcion actualizar(Integer id, Funcion cambios) {
        Funcion actual = funciones.bloquearPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Función no encontrada"));
        if ((!Objects.equals(actual.getSala(), cambios.getSala())
                || !Objects.equals(actual.getCuposDisponibles(), cambios.getCuposDisponibles()))
                && !butacas.listar(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La sala y los cupos de una función con mapa de butacas no se pueden editar");
        }

        actual.setPelicula(cambios.getPelicula());
        actual.setFecha(cambios.getFecha());
        actual.setHora(cambios.getHora());
        actual.setSala(cambios.getSala());
        actual.setPrecio(cambios.getPrecio());
        actual.setCuposDisponibles(cambios.getCuposDisponibles());
        return actual;
    }

    public void eliminar(Integer id) {
        funciones.deleteById(id);
    }
}
