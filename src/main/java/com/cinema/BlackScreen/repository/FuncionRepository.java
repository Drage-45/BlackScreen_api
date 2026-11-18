package com.cinema.BlackScreen.repository;

import com.cinema.BlackScreen.entity.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Integer> {
}