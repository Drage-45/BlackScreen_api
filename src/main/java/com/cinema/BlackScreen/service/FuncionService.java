package com.cinema.BlackScreen.service;

import com.cinema.BlackScreen.entity.Funcion;
import com.cinema.BlackScreen.repository.FuncionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FuncionService {

    @Autowired
    private FuncionRepository funcionRepository;

    public List<Funcion> listarTodas() {
        return funcionRepository.findAll();
    }

    public Optional<Funcion> buscarPorId(Integer id) {
        return funcionRepository.findById(id);
    }

    public Funcion guardar(Funcion funcion) {
        return funcionRepository.save(funcion);
    }

    public void eliminar(Integer id) {
        funcionRepository.deleteById(id);
    }
}