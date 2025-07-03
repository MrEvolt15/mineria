package com.mineria.back.mineria.dto;

import java.time.LocalDateTime;

/**
 * DTO para representar un centroide de K-means con información para el mapa de calor
 */
public class CentroideDTO {
    
    private Double posX;
    private Double posY;
    private Integer densidad; // Cantidad de puntos asignados a este centroide
    private Double intensidad; // Valor normalizado 0-1 para el color del mapa
    private Integer clusterId; // ID del cluster
    private LocalDateTime ultimaActualizacion;
    private Double radio; // Radio de influencia del centroide
    
    public CentroideDTO() {}
    
    public CentroideDTO(Double posX, Double posY, Integer clusterId) {
        this.posX = posX;
        this.posY = posY;
        this.clusterId = clusterId;
        this.densidad = 0;
        this.intensidad = 0.0;
        this.ultimaActualizacion = LocalDateTime.now();
        this.radio = 1.0; // Radio por defecto
    }
    
    // Getters y Setters
    public Double getPosX() {
        return posX;
    }
    
    public void setPosX(Double posX) {
        this.posX = posX;
    }
    
    public Double getPosY() {
        return posY;
    }
    
    public void setPosY(Double posY) {
        this.posY = posY;
    }
    
    public Integer getDensidad() {
        return densidad;
    }
    
    public void setDensidad(Integer densidad) {
        this.densidad = densidad;
        // Actualizar intensidad basada en densidad
        this.intensidad = Math.min(1.0, densidad / 10.0); // Normalizar a 0-1
    }
    
    public Double getIntensidad() {
        return intensidad;
    }
    
    public void setIntensidad(Double intensidad) {
        this.intensidad = intensidad;
    }
    
    public Integer getClusterId() {
        return clusterId;
    }
    
    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }
    
    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }
    
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
    
    public Double getRadio() {
        return radio;
    }
    
    public void setRadio(Double radio) {
        this.radio = radio;
    }
    
    /**
     * Incrementa la densidad y actualiza la intensidad
     */
    public void incrementarDensidad() {
        this.densidad++;
        this.setDensidad(this.densidad); // Recalcula intensidad
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Calcula la distancia euclidiana a un punto
     */
    public double distanciaA(Double x, Double y) {
        if (this.posX == null || this.posY == null || x == null || y == null) {
            return Double.MAX_VALUE;
        }
        return Math.sqrt(Math.pow(this.posX - x, 2) + Math.pow(this.posY - y, 2));
    }
    
    @Override
    public String toString() {
        return String.format("Centroide{id=%d, pos=(%.2f,%.2f), densidad=%d, intensidad=%.2f}", 
                           clusterId, posX, posY, densidad, intensidad);
    }
}
