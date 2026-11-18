package com.cinema.BlackScreen.exception;

import com.cinema.BlackScreen.entity.Compra;
import com.cinema.BlackScreen.exception.RecursoNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> respuestaError(
            HttpStatus estado,
            String mensaje,
            HttpServletRequest request){
        Map<String,Object> respuesta = Map.of(
                "fecha", LocalDateTime.now().toString(),
                "código", estado.value(),
                "error", estado.getReasonPhrase(),
                "mensaje", mensaje,
                "ruta", request.getRequestURL()
        );
        return ResponseEntity.status(estado).body(respuesta);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> errorDatos(
            HttpMessageNotReadableException ex,
            HttpServletRequest request){

        return respuestaError(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es invalido",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> errorIntegrida(
            DataIntegrityViolationException ex,
            HttpServletRequest request){

        return respuestaError(
                HttpStatus.CONFLICT,
                "La operación incumple una restricción de la base de datos",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> errorInterno(
            Exception ex,
            HttpServletRequest request){

        return respuestaError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor",
                request
        );
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> errorNoEncontrado(
            RecursoNoEncontradoException ex,
            HttpServletRequest request){

        return respuestaError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> errorValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Los datos enviados no son válidos");

        return respuestaError(
                HttpStatus.BAD_REQUEST,
                mensaje,
                request
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> errorEstado(
            ResponseStatusException ex,
            HttpServletRequest request){

        HttpStatusCode estado = ex.getStatusCode();

        String mensaje = ex.getMessage() != null
                ? ex.getReason()
                : "No se pudo completar la operación";

        return respuestaError(
                HttpStatus.valueOf(estado.value()),
                mensaje,
                request
        );
    }

}
