package com.mineria.back.mineria.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para representar una detección con datos de posición para K-means
 */
public class DeteccionConPosicionDTO {
    
    private String idPersonaCamara;
    private String idPersona;
    private String idCamara;
    private LocalDateTime fechaDeteccion;
    private Double confianza;
    private BigDecimal posX;
    private BigDecimal posY;
    private Integer ncam;
    
    public DeteccionConPosicionDTO() {}
    
    public DeteccionConPosicionDTO(String idPersonaCamara, String idPersona, String idCamara, 
                                   LocalDateTime fechaDeteccion, Double confianza, 
                                   BigDecimal posX, BigDecimal posY, Integer ncam) {
        this.idPersonaCamara = idPersonaCamara;
        this.idPersona = idPersona;
        this.idCamara = idCamara;
        this.fechaDeteccion = fechaDeteccion;
        this.confianza = confianza;
        this.posX = posX;
        this.posY = posY;
        this.ncam = ncam;
    }
    
    // Getters y Setters
    public String getIdPersonaCamara() {
        return idPersonaCamara;
    }
    
    public void setIdPersonaCamara(String idPersonaCamara) {
        this.idPersonaCamara = idPersonaCamara;
    }
    
    public String getIdPersona() {
        return idPersona;
    }
    
    public void setIdPersona(String idPersona) {
        this.idPersona = idPersona;
    }
    
    public String getIdCamara() {
        return idCamara;
    }
    
    public void setIdCamara(String idCamara) {
        this.idCamara = idCamara;
    }
    
    public LocalDateTime getFechaDeteccion() {
        return fechaDeteccion;
    }
    
    public void setFechaDeteccion(LocalDateTime fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }
    
    public Double getConfianza() {
        return confianza;
    }
    
    public void setConfianza(Double confianza) {
        this.confianza = confianza;
    }
    
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
    
    public Integer getNcam() {
        return ncam;
    }
    
    public void setNcam(Integer ncam) {
        this.ncam = ncam;
    }
    
    /**
     * Convierte posX a Double para cálculos
     */
    public Double getPosXAsDouble() {
        return posX != null ? posX.doubleValue() : 0.0;
    }
    
    /**
     * Convierte posY a Double para cálculos
     */
    public Double getPosYAsDouble() {
        return posY != null ? posY.doubleValue() : 0.0;
    }
}
