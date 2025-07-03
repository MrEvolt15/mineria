package com.mineria.back.mineria.repo;

import java.util.List;

import com.mineria.back.mineria.repo.model.PersonaCamara;

public interface IPersonaCamaraRepo {
    void save(PersonaCamara personaCamara);
    PersonaCamara findById(String id);
    List<PersonaCamara> findAll();
    List<PersonaCamara> findByIdPersona(String idPersona);
    List<PersonaCamara> findByIdCamara(String idCamara);
    void update(PersonaCamara personaCamara);
    void deleteById(String id);
    void deleteByIdPersona(String idPersona);
    void deleteByIdCamara(String idCamara);
}
