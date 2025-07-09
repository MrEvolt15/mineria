package com.mineria.back.mineria.dto;

import com.mineria.back.mineria.repo.model.Deteccion;
import java.time.LocalDateTime;
import java.util.List;

public class DeteccionDTO {
    private String id;
    private LocalDateTime timestamp;
    private Integer personas;
    private List<Deteccion.Coordenada> coordenadas;
    
    // Información adicional de agrupación DBSCAN
    private Integer clusterId;           // ID del cluster (-1 para ruido)
    private String clusterTipo;          // "CLUSTER" o "RUIDO"
    private Double densidadCluster;      // Densidad del cluster al que pertenece
    private Integer puntosEnCluster;     // Número total de puntos en su cluster
    private Coordenada centroideCluster; // Centroide del cluster
    
    // Constructores
    public DeteccionDTO() {}
    
    public DeteccionDTO(Deteccion deteccion) {
        this.id = deteccion.getId();
        this.timestamp = deteccion.getTimestamp();
        this.personas = deteccion.getPersonas();
        this.coordenadas = deteccion.getCoordenadas();
        // Inicializar campos de clustering
        this.clusterId = null;
        this.clusterTipo = "SIN_PROCESAR";
        this.densidadCluster = 0.0;
        this.puntosEnCluster = 0;
        this.centroideCluster = null;
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getPersonas() {
        return personas;
    }
    
    public void setPersonas(Integer personas) {
        this.personas = personas;
    }
    
    public List<Deteccion.Coordenada> getCoordenadas() {
        return coordenadas;
    }
    
    public void setCoordenadas(List<Deteccion.Coordenada> coordenadas) {
        this.coordenadas = coordenadas;
    }
    
    public Integer getClusterId() {
        return clusterId;
    }
    
    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }
    
    public String getClusterTipo() {
        return clusterTipo;
    }
    
    public void setClusterTipo(String clusterTipo) {
        this.clusterTipo = clusterTipo;
    }
    
    public Double getDensidadCluster() {
        return densidadCluster;
    }
    
    public void setDensidadCluster(Double densidadCluster) {
        this.densidadCluster = densidadCluster;
    }
    
    public Integer getPuntosEnCluster() {
        return puntosEnCluster;
    }
    
    public void setPuntosEnCluster(Integer puntosEnCluster) {
        this.puntosEnCluster = puntosEnCluster;
    }
    
    public Coordenada getCentroideCluster() {
        return centroideCluster;
    }
    
    public void setCentroideCluster(Coordenada centroideCluster) {
        this.centroideCluster = centroideCluster;
    }
    
    // Clase interna para centroide
    public static class Coordenada {
        private Double x;
        private Double y;
        
        public Coordenada() {}
        
        public Coordenada(Double x, Double y) {
            this.x = x;
            this.y = y;
        }
        
        public Double getX() {
            return x;
        }
        
        public void setX(Double x) {
            this.x = x;
        }
        
        public Double getY() {
            return y;
        }
        
        public void setY(Double y) {
            this.y = y;
        }
        
        @Override
        public String toString() {
            return String.format("Coordenada{x=%.2f, y=%.2f}", x, y);
        }
    }
    
    @Override
    public String toString() {
        return String.format("DeteccionDTO{id='%s', timestamp=%s, personas=%d, clusterId=%d, tipo='%s'}", 
                           id, timestamp, personas, clusterId, clusterTipo);
    }
}
