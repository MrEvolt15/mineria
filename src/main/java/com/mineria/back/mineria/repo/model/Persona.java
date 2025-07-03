package com.mineria.back.mineria.repo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "personas")
public class Persona {
    
    @Id
    @Field("ID_PERSONA")
    private String idPersona;
    
    @Field("TIPO")
    private TipoPersona tipo;
    
    @Field("GENERO")
    private String genero;
    
    // Enum for TIPO field
    public enum TipoPersona {
        E("E"),
        PR("PR"),
        PA("PA");
        
        private final String valor;
        
        TipoPersona(String valor) {
            this.valor = valor;
        }
        
        public String getValor() {
            return valor;
        }
    }
    
    // Default constructor
    public Persona() {}
    
    // Constructor with parameters
    public Persona(String idPersona, TipoPersona tipo, String genero) {
        this.idPersona = idPersona;
        this.tipo = tipo;
        this.genero = genero;
    }
    
    // Getters and Setters
    public String getIdPersona() {
        return idPersona;
    }
    
    public void setIdPersona(String idPersona) {
        this.idPersona = idPersona;
    }
    
    public TipoPersona getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoPersona tipo) {
        this.tipo = tipo;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    @Override
    public String toString() {
        return "Persona{" +
                "idPersona='" + idPersona + '\'' +
                ", tipo=" + tipo +
                ", genero='" + genero + '\'' +
                '}';
    }
}
