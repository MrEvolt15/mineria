package com.mineria.back.mineria.repo;

import com.mineria.back.mineria.repo.model.Deteccion;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DeteccionRepositoryImpl {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * Busca detecciones por segundo específico
     * Busca todas las detecciones que ocurrieron en ese segundo específico
     */
    public List<Deteccion> findBySecond(LocalDateTime segundo) {
        // Crear un rango del segundo especificado
        LocalDateTime inicioSegundo = segundo.withNano(0);
        LocalDateTime finSegundo = inicioSegundo.plusSeconds(1).minusNanos(1);
        
        Query query = new Query();
        query.addCriteria(Criteria.where("timestamp")
                .gte(inicioSegundo)
                .lte(finSegundo));
        
        return mongoTemplate.find(query, Deteccion.class);
    }
    
    /**
     * Busca detecciones en un rango de tiempo
     */
    public List<Deteccion> findByRange(LocalDateTime desde, LocalDateTime hasta) {
        Query query = new Query();
        query.addCriteria(Criteria.where("timestamp")
                .gte(desde)
                .lte(hasta));
        
        return mongoTemplate.find(query, Deteccion.class);
    }
    
    /**
     * Guarda una nueva detección
     */
    public Deteccion save(Deteccion deteccion) {
        // Validar que la detección tenga datos válidos
        if (deteccion.getTimestamp() == null) {
            throw new IllegalArgumentException("El timestamp no puede ser null");
        }
        if (deteccion.getPersonas() == null || deteccion.getPersonas() < 0) {
            throw new IllegalArgumentException("El número de personas debe ser mayor o igual a 0");
        }
        if (deteccion.getCoordenadas() == null) {
            throw new IllegalArgumentException("Las coordenadas no pueden ser null");
        }
        
        return mongoTemplate.save(deteccion);
    }
    
    /**
     * Obtiene todas las detecciones ordenadas por timestamp
     */
    public List<Deteccion> findAllOrderByTimestamp() {
        Query query = new Query();
        query.with(org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));
        
        return mongoTemplate.find(query, Deteccion.class);
    }
    
    /**
     * Obtiene las últimas N detecciones
     */
    public List<Deteccion> findLastN(int limit) {
        Query query = new Query();
        query.with(org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));
        query.limit(limit);
        
        return mongoTemplate.find(query, Deteccion.class);
    }
    
    /**
     * Cuenta el total de detecciones
     */
    public long count() {
        return mongoTemplate.count(new Query(), Deteccion.class);
    }
    
    /**
     * Elimina todas las detecciones (para limpieza)
     */
    public void deleteAll() {
        mongoTemplate.remove(new Query(), Deteccion.class);
    }
}
