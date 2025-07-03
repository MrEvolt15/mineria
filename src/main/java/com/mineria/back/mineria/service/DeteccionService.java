package com.mineria.back.mineria.service;

import com.mineria.back.mineria.repo.DeteccionRepositoryImpl;
import com.mineria.back.mineria.repo.model.Deteccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class DeteccionService {
    
    @Autowired
    private DeteccionRepositoryImpl deteccionRepository;
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Busca detecciones por segundo específico
     */
    public List<Deteccion> obtenerDeteccionesPorSegundo(String timestampStr) {
        try {
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, TIMESTAMP_FORMATTER);
            return deteccionRepository.findBySecond(timestamp);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Formato de timestamp inválido. Use: yyyy-MM-ddTHH:mm:ss", e);
        }
    }
    
    /**
     * Busca detecciones en un rango de tiempo
     */
    public List<Deteccion> obtenerDeteccionesPorRango(String rangoStr) {
        try {
            String[] partes = rangoStr.split(",");
            if (partes.length != 2) {
                throw new IllegalArgumentException(
                    "El rango debe tener el formato: timestamp1,timestamp2");
            }
            
            LocalDateTime desde = LocalDateTime.parse(partes[0].trim(), TIMESTAMP_FORMATTER);
            LocalDateTime hasta = LocalDateTime.parse(partes[1].trim(), TIMESTAMP_FORMATTER);
            
            if (desde.isAfter(hasta)) {
                throw new IllegalArgumentException(
                    "La fecha de inicio debe ser anterior a la fecha de fin");
            }
            
            return deteccionRepository.findByRange(desde, hasta);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Formato de timestamp inválido en el rango. Use: yyyy-MM-ddTHH:mm:ss", e);
        }
    }
    
    /**
     * Guarda una nueva detección
     */
    public Deteccion guardarDeteccion(Deteccion deteccion) {
        // Validaciones adicionales de negocio
        validarDeteccion(deteccion);
        
        return deteccionRepository.save(deteccion);
    }
    
    /**
     * Obtiene todas las detecciones ordenadas por timestamp
     */
    public List<Deteccion> obtenerTodasLasDetecciones() {
        return deteccionRepository.findAllOrderByTimestamp();
    }
    
    /**
     * Obtiene las últimas N detecciones
     */
    public List<Deteccion> obtenerUltimasDetecciones(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor a 0");
        }
        if (limite > 1000) {
            throw new IllegalArgumentException("El límite máximo es 1000 detecciones");
        }
        
        return deteccionRepository.findLastN(limite);
    }
    
    /**
     * Obtiene estadísticas básicas
     */
    public DeteccionStats obtenerEstadisticas() {
        long total = deteccionRepository.count();
        List<Deteccion> ultimasCinco = deteccionRepository.findLastN(5);
        
        DeteccionStats stats = new DeteccionStats();
        stats.setTotalDetecciones(total);
        stats.setUltimasDetecciones(ultimasCinco);
        
        return stats;
    }
    
    /**
     * Limpia todas las detecciones
     */
    public void limpiarTodasLasDetecciones() {
        deteccionRepository.deleteAll();
    }
    
    /**
     * Valida una detección antes de guardarla
     */
    private void validarDeteccion(Deteccion deteccion) {
        if (deteccion == null) {
            throw new IllegalArgumentException("La detección no puede ser null");
        }
        
        if (deteccion.getTimestamp() == null) {
            throw new IllegalArgumentException("El timestamp es obligatorio");
        }
        
        if (deteccion.getPersonas() == null) {
            throw new IllegalArgumentException("El número de personas es obligatorio");
        }
        
        if (deteccion.getPersonas() < 0) {
            throw new IllegalArgumentException("El número de personas no puede ser negativo");
        }
        
        if (deteccion.getCoordenadas() == null) {
            throw new IllegalArgumentException("Las coordenadas son obligatorias");
        }
        
        // Validar que el número de coordenadas coincida con el número de personas
        if (deteccion.getCoordenadas().size() != deteccion.getPersonas()) {
            throw new IllegalArgumentException(String.format(
                "El número de coordenadas (%d) debe coincidir con el número de personas (%d)",
                deteccion.getCoordenadas().size(), deteccion.getPersonas()));
        }
        
        // Validar que las coordenadas sean válidas
        for (int i = 0; i < deteccion.getCoordenadas().size(); i++) {
            Deteccion.Coordenada coord = deteccion.getCoordenadas().get(i);
            if (coord == null) {
                throw new IllegalArgumentException("Las coordenadas no pueden ser null");
            }
            if (coord.getX() == null || coord.getY() == null) {
                throw new IllegalArgumentException(String.format(
                    "Las coordenadas en la posición %d deben tener valores X e Y válidos", i));
            }
            if (coord.getX() < 0 || coord.getY() < 0) {
                throw new IllegalArgumentException(String.format(
                    "Las coordenadas en la posición %d no pueden ser negativas", i));
            }
        }
    }
    
    // Clase para estadísticas
    public static class DeteccionStats {
        private long totalDetecciones;
        private List<Deteccion> ultimasDetecciones;
        
        public long getTotalDetecciones() {
            return totalDetecciones;
        }
        
        public void setTotalDetecciones(long totalDetecciones) {
            this.totalDetecciones = totalDetecciones;
        }
        
        public List<Deteccion> getUltimasDetecciones() {
            return ultimasDetecciones;
        }
        
        public void setUltimasDetecciones(List<Deteccion> ultimasDetecciones) {
            this.ultimasDetecciones = ultimasDetecciones;
        }
    }
}
