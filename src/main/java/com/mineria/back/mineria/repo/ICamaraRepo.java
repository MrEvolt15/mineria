package com.mineria.back.mineria.repo;

import java.util.List;

import com.mineria.back.mineria.repo.model.Camara;

public interface ICamaraRepo {
    void save(Camara camara);
    Camara findById(String id);
    List<Camara> findAll();
    Camara findByNcam(Integer ncam);
    void update(Camara camara);
    void deleteById(String id);
}
