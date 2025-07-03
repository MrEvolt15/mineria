package com.mineria.back.mineria.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class CamaraDataDTO {
    private Integer ncam;
    private LocalDateTime time;
    private BigDecimal posX;
    private BigDecimal posY;
    
    // Datos adicionales para detección de personas
    private String tipoPersona; // E, PR, PA
    private String genero;
    private Integer id; // ID único de la detección (dato mínimo requerido)
    
    // Constructors
    public CamaraDataDTO() {}
    
    public CamaraDataDTO(Integer ncam, LocalDateTime time, BigDecimal posX, BigDecimal posY, 
                        String tipoPersona, String genero, Integer id) {
        this.ncam = ncam;
        this.time = time;
        this.posX = posX;
        this.posY = posY;
        this.tipoPersona = tipoPersona;
        this.genero = genero;
        this.id = id;
    }
    
    // Getters and Setters
    public Integer getNcam() {
        return ncam;
    }
    
    public void setNcam(Integer ncam) {
        this.ncam = ncam;
    }
    
    public LocalDateTime getTime() {
        return time;
    }
    
    public void setTime(LocalDateTime time) {
        this.time = time;
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
    
    public String getTipoPersona() {
        return tipoPersona;
    }
    
    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
}
