package com.mineria.back.mineria.repo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Document(collection = "persona_camara")
public class PersonaCamara {
    
    @Id
    @Field("ID_PERSONA_CAMARA")
    private String idPersonaCamara;
    
    @Field("ID_PERSONA")
    private String idPersona;
    
    @Field("ID_CAMARA")
    private String idCamara;
    
    @Field("FECHA_DETECCION")
    private LocalDateTime fechaDeteccion;
    
    @Field("CONFIANZA")
    private Double confianza; // Nivel de confianza de la detección (0.0 - 1.0)
    
    // Default constructor
    public PersonaCamara() {}
    
    // Constructor with parameters
    public PersonaCamara(String idPersona, String idCamara, LocalDateTime fechaDeteccion, Double confianza) {
        this.idPersona = idPersona;
        this.idCamara = idCamara;
        this.fechaDeteccion = fechaDeteccion;
        this.confianza = confianza;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "PersonaCamara{" +
                "idPersonaCamara='" + idPersonaCamara + '\'' +
                ", idPersona='" + idPersona + '\'' +
                ", idCamara='" + idCamara + '\'' +
                ", fechaDeteccion=" + fechaDeteccion +
                ", confianza=" + confianza +
                '}';
    }
}
