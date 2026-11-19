package com.cinema.BlackScreen.service;

import com.cinema.BlackScreen.entity.Compra;
import com.cinema.BlackScreen.entity.Funcion;
import com.cinema.BlackScreen.repository.ButacaRepository;
import com.cinema.BlackScreen.repository.CompraRepository;
import com.cinema.BlackScreen.repository.FuncionRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompraService {
    public record FuncionSolicitada(Integer id) {}
    public record SolicitudCompra(FuncionSolicitada funcion, String nombreCliente,
                                  Integer cantidadBoletos, List<String> butacas) {}
    public record CompraRegistrada(Integer id, Funcion funcion, String nombreCliente,
                                   Integer cantidadBoletos, Double valorTotal,
                                   LocalDateTime fechaCompra, List<String> butacas) {}

    private final CompraRepository compras;
    private final FuncionRepository funciones;
    private final ButacaRepository butacas;

    public CompraService(CompraRepository compras, FuncionRepository funciones,
                         ButacaRepository butacas) {
        this.compras = compras;
        this.funciones = funciones;
        this.butacas = butacas;
    }

    public List<Compra> listarTodas() {
        return compras.findAll();
    }

    @Transactional
    public CompraRegistrada registrarCompra(SolicitudCompra solicitud) {
        Integer cantidad = solicitud.cantidadBoletos();
        if (cantidad == null || cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cantidad de boletos debe ser mayor que cero");
        }
        if (solicitud.funcion() == null || solicitud.funcion().id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona una función");
        }
        String nombre = solicitud.nombreCliente() == null ? "" : solicitud.nombreCliente().strip();
        if (nombre.isEmpty() || nombre.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de cliente inválido");
        }
        List<String> codigos = solicitud.butacas();
        if (codigos == null || codigos.size() != cantidad
                || new HashSet<>(codigos).size() != cantidad
                || codigos.stream().anyMatch(codigo -> codigo == null
                    || !codigo.matches("[A-Z]{1,2}[1-9][0-9]{0,2}"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Selecciona una butaca válida y distinta por cada boleto");
        }

        Funcion funcion = funciones.bloquearPorId(solicitud.funcion().id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Función no encontrada"));
        if (cantidad > funcion.getCuposDisponibles()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cupos insuficientes para esta función");
        }

        Set<String> libres = butacas.listar(funcion.getId()).stream()
                .filter(butaca -> !butaca.ocupada())
                .map(ButacaRepository.Butaca::codigo)
                .collect(Collectors.toSet());
        for (String codigo : codigos) {
            if (!libres.contains(codigo)) {
                throw butacaNoDisponible(codigo);
            }
        }

        Compra compra = new Compra();
        compra.setCantidadBoletos(cantidad);
        compra.setNombreCliente(nombre);
        compra.setValorTotal(funcion.getPrecio() * cantidad);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setFuncion(funcion);
        compras.saveAndFlush(compra);

        for (String codigo : codigos) {
            if (butacas.asignar(funcion.getId(), codigo, compra.getId()) != 1) {
                throw butacaNoDisponible(codigo);
            }
        }
        funcion.setCuposDisponibles(funcion.getCuposDisponibles() - cantidad);
        return new CompraRegistrada(compra.getId(), funcion, nombre, cantidad,
                compra.getValorTotal(), compra.getFechaCompra(), List.copyOf(codigos));
    }

    private ResponseStatusException butacaNoDisponible(String codigo) {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                "La butaca " + codigo + " no está disponible para esta función");
    }
}
