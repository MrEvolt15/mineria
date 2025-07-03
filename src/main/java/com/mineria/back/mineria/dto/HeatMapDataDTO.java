package com.mineria.back.mineria.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HeatMapDataDTO {
    private BigDecimal posX;
    private BigDecimal posY;
    private Integer count; // Número de detecciones en esta posición
    private LocalDateTime ultimaDeteccion;
    private String tipoPersona;
    private Double confianzaPromedio;
    
    // Constructors
    public HeatMapDataDTO() {}
    
    public HeatMapDataDTO(BigDecimal posX, BigDecimal posY, Integer count, 
                         LocalDateTime ultimaDeteccion, String tipoPersona, Double confianzaPromedio) {
        this.posX = posX;
        this.posY = posY;
        this.count = count;
        this.ultimaDeteccion = ultimaDeteccion;
        this.tipoPersona = tipoPersona;
        this.confianzaPromedio = confianzaPromedio;
    }
    
    // Getters and Setters
    public BigDecimal getPosX() {
        return posX;
    }
    
    public void setPosX(BigDecimal posX) {
        this.posX = posX;
    }
    
    public BigDecimal getPosY() {
        return posY;
    }
    
    public void setPosY(BigDecimal posY) {
        this.posY = posY;
    }
    
    public Integer getCount() {
        return count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
    
    public LocalDateTime getUltimaDeteccion() {
        return ultimaDeteccion;
    }
    
    public void setUltimaDeteccion(LocalDateTime ultimaDeteccion) {
        this.ultimaDeteccion = ultimaDeteccion;
    }
    
    public String getTipoPersona() {
        return tipoPersona;
    }
    
    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }
    
    public Double getConfianzaPromedio() {
        return confianzaPromedio;
    }
    
    public void setConfianzaPromedio(Double confianzaPromedio) {
        this.confianzaPromedio = confianzaPromedio;
    }
}
