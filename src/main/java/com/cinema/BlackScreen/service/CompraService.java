package com.cinema.BlackScreen.service;

import com.cinema.BlackScreen.entity.Compra;
import com.cinema.BlackScreen.entity.Funcion;
import com.cinema.BlackScreen.repository.CompraRepository;
import com.cinema.BlackScreen.repository.FuncionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private FuncionRepository funcionRepository;

    public List<Compra> listarTodas() {
        return compraRepository.findAll();
    }

    @Transactional
    public Compra registrarCompra(Compra compra) {

        if (compra.getCantidadBoletos() == null ||
                compra.getCantidadBoletos() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad de boletos debe ser mayor que cero"
            );
        }

        Funcion funcion = funcionRepository.findById(compra.getFuncion().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Función no encontrada"));

        if (compra.getCantidadBoletos() > funcion.getCuposDisponibles()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cupos insuficientes para esta función");
        }

        compra.setValorTotal(funcion.getPrecio() * compra.getCantidadBoletos());
        compra.setFechaCompra(LocalDateTime.now());
        compra.setFuncion(funcion);

        funcion.setCuposDisponibles(funcion.getCuposDisponibles() - compra.getCantidadBoletos());
        funcionRepository.save(funcion);

        return compraRepository.save(compra);
    }
}