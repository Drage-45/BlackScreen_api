package com.cinema.BlackScreen.repository;

import com.cinema.BlackScreen.entity.Funcion;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FuncionRepository extends JpaRepository<Funcion, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Funcion f where f.id = :id")
    Optional<Funcion> bloquearPorId(@Param("id") Integer id);
}
