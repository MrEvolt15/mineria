package com.mineria.back.mineria.repo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Document(collection = "camaras")
public class Camara {
    
    @Id
    @Field("ID_CAMARA")
    private String idCamara;
    
    @Field("NCAM")
    private Integer ncam;
    
    @Field("TIME")
    private LocalDateTime time;
    
    @Field("POS_X")
    private BigDecimal posX;
    
    @Field("POS_Y")
    private BigDecimal posY;
    
    // Default constructor
    public Camara() {}
    
    // Constructor with parameters
    public Camara(String idCamara, Integer ncam, LocalDateTime time, BigDecimal posX, BigDecimal posY) {
        this.idCamara = idCamara;
        this.ncam = ncam;
        this.time = time;
        this.posX = posX;
        this.posY = posY;
    }
    
    // Getters and Setters
    public String getIdCamara() {
        return idCamara;
    }
    
    public void setIdCamara(String idCamara) {
        this.idCamara = idCamara;
    }
    
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
    
    @Override
    public String toString() {
        return "Camara{" +
                "idCamara='" + idCamara + '\'' +
                ", ncam=" + ncam +
                ", time=" + time +
                ", posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
