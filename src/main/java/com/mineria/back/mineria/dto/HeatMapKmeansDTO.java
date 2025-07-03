package com.mineria.back.mineria.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.mineria.back.mineria.repo.model.PersonaCamara;

/**
 * DTO para la respuesta del mapa de calor con centroides K-means
 */
public class HeatMapKmeansDTO {
    
    private List<PersonaCamara> detecciones; // Detecciones originales
    private List<CentroideDTO> centroides; // Centroides calculados por K-means
    private Integer totalDetecciones;
    private Integer numeroClusters;
    private LocalDateTime fechaCalculo;
    private Double convergencia; // Medida de qué tan estables están los centroides
    
    public HeatMapKmeansDTO() {}
    
    public HeatMapKmeansDTO(List<PersonaCamara> detecciones, List<CentroideDTO> centroides) {
        this.detecciones = detecciones;
        this.centroides = centroides;
        this.totalDetecciones = detecciones != null ? detecciones.size() : 0;
        this.numeroClusters = centroides != null ? centroides.size() : 0;
        this.fechaCalculo = LocalDateTime.now();
    }
    
    // Getters y Setters
    public List<PersonaCamara> getDetecciones() {
        return detecciones;
    }
    
    public void setDetecciones(List<PersonaCamara> detecciones) {
        this.detecciones = detecciones;
        this.totalDetecciones = detecciones != null ? detecciones.size() : 0;
    }
    
    public List<CentroideDTO> getCentroides() {
        return centroides;
    }
    
    public void setCentroides(List<CentroideDTO> centroides) {
        this.centroides = centroides;
        this.numeroClusters = centroides != null ? centroides.size() : 0;
    }
    
    public Integer getTotalDetecciones() {
        return totalDetecciones;
    }
    
    public void setTotalDetecciones(Integer totalDetecciones) {
        this.totalDetecciones = totalDetecciones;
    }
    
    public Integer getNumeroClusters() {
        return numeroClusters;
    }
    
    public void setNumeroClusters(Integer numeroClusters) {
        this.numeroClusters = numeroClusters;
    }
    
    public LocalDateTime getFechaCalculo() {
        return fechaCalculo;
    }
    
    public void setFechaCalculo(LocalDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }
    
    public Double getConvergencia() {
        return convergencia;
    }
    
    public void setConvergencia(Double convergencia) {
        this.convergencia = convergencia;
    }
    
    /**
     * Obtiene estadísticas resumidas para el frontend
     */
    public String getResumenEstadisticas() {
        return String.format("Detecciones: %d, Clusters: %d, Convergencia: %.3f", 
                           totalDetecciones, numeroClusters, convergencia != null ? convergencia : 0.0);
    }
}
