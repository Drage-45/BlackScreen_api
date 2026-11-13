package com.cinema.BlackScreen.controller;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final JdbcTemplate jdbcTemplate;

    public RestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/prueba")
    public Map<String,String> conectar(){

        String blackScreen = jdbcTemplate.queryForObject(
                "SELECT current_database()",
                String.class);

        return Map.of("mensaje", "Conexion exitosa con PostgreSQL",
                "baseDeDatos", blackScreen);

    }
}
