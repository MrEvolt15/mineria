package com.mineria.back.mineria.repo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "detecciones")
public class Deteccion {
    
    @Id
    private String id;
    
    @Field("timestamp")
    private LocalDateTime timestamp;
    
    @Field("personas")
    private Integer personas;
    
    @Field("coordenadas")
    private List<Coordenada> coordenadas;
    
    // Constructores
    public Deteccion() {}
    
    public Deteccion(LocalDateTime timestamp, Integer personas, List<Coordenada> coordenadas) {
        this.timestamp = timestamp;
        this.personas = personas;
        this.coordenadas = coordenadas;
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
    
    public List<Coordenada> getCoordenadas() {
        return coordenadas;
    }
    
    public void setCoordenadas(List<Coordenada> coordenadas) {
        this.coordenadas = coordenadas;
    }
    
    // Clase interna para las coordenadas
    public static class Coordenada {
        @Field("x")
        private Double x;
        
        @Field("y")
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
        return String.format("Deteccion{id='%s', timestamp=%s, personas=%d, coordenadas=%s}", 
                           id, timestamp, personas, coordenadas);
    }
}
