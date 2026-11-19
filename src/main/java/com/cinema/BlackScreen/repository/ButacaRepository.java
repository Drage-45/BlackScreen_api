package com.cinema.BlackScreen.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ButacaRepository {
    public record Butaca(String codigo, boolean ocupada) {}

    private final JdbcTemplate jdbc;

    public ButacaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void generarMapaDemostracion(Integer funcionId, int cupos) {
        jdbc.query("""
                SELECT public.configurar_butacas(?, ARRAY(
                    SELECT CASE WHEN fila < 26 THEN chr(65 + fila)
                                ELSE chr(64 + fila / 26) || chr(65 + fila % 26)
                           END || columna
                    FROM (
                        SELECT asiento / 6 AS fila, asiento % 6 + 1 AS columna
                        FROM generate_series(0, ? - 1) AS serie(asiento)
                    ) AS posiciones
                    ORDER BY fila, columna
                ))
                """, result -> {}, funcionId, cupos);
    }

    public List<Butaca> listar(Integer funcionId) {
        return jdbc.query("""
                SELECT codigo, id_compra IS NOT NULL OR reservada_historica AS ocupada
                FROM public.butacas_funcion WHERE id_funcion = ?
                ORDER BY length(regexp_replace(codigo, '[0-9]+$', '')),
                         regexp_replace(codigo, '[0-9]+$', ''),
                         regexp_replace(codigo, '^[A-Z]+', '')::integer
                """, (row, index) -> new Butaca(row.getString("codigo"), row.getBoolean("ocupada")),
                funcionId);
    }

    public int asignar(Integer funcionId, String codigo, Integer compraId) {
        return jdbc.update("""
                UPDATE public.butacas_funcion SET id_compra = ?
                WHERE id_funcion = ? AND codigo = ?
                  AND id_compra IS NULL AND NOT reservada_historica
                """, compraId, funcionId, codigo);
    }
}
