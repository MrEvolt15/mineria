package com.mineria.back.mineria.repo;

import java.util.List;

import com.mineria.back.mineria.repo.model.Persona;

public interface IPersonaRepo {
    void save(Persona persona);
    Persona findById(String id);
    List<Persona> findAll();
    void update(Persona persona);
    void deleteById(String id);

}
