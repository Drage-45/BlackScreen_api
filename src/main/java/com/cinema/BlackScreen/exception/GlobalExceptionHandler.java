package com.cinema.BlackScreen.exception;

import com.cinema.BlackScreen.entity.Compra;
import com.cinema.BlackScreen.exception.RecursoNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

}
