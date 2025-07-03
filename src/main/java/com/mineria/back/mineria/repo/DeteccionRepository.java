package com.mineria.back.mineria.repo;

import com.mineria.back.mineria.repo.model.Deteccion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeteccionRepository extends MongoRepository<Deteccion, String> {
    
    /**
     * Busca detecciones por timestamp exacto
     */
    List<Deteccion> findByTimestamp(LocalDateTime timestamp);
    
    /**
     * Busca detecciones en un rango de tiempo
     */
    @Query("{'timestamp': {'$gte': ?0, '$lte': ?1}}")
    List<Deteccion> findByTimestampBetween(LocalDateTime desde, LocalDateTime hasta);
    
    /**
     * Busca detecciones ordenadas por timestamp descendente
     */
    @Query(value = "{}", sort = "{'timestamp': -1}")
    List<Deteccion> findAllOrderByTimestampDesc();
    
    /**
     * Busca las últimas N detecciones
     */
    @Query(value = "{}", sort = "{'timestamp': -1}")
    List<Deteccion> findTopNOrderByTimestampDesc(int limit);
}
