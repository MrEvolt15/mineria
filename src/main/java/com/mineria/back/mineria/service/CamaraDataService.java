package com.mineria.back.mineria.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mineria.back.mineria.dto.CamaraDataDTO;
import com.mineria.back.mineria.repo.ICamaraRepo;
import com.mineria.back.mineria.repo.IPersonaCamaraRepo;
import com.mineria.back.mineria.repo.IPersonaRepo;
import com.mineria.back.mineria.repo.model.Camara;
import com.mineria.back.mineria.repo.model.Persona;
import com.mineria.back.mineria.repo.model.PersonaCamara;

@Service
public class CamaraDataService {
    
    @Autowired
    private ICamaraRepo camaraRepo;
    
    @Autowired
    private IPersonaRepo personaRepo;
    
    @Autowired
    private IPersonaCamaraRepo personaCamaraRepo;
    
    /**
     * Procesa datos de cámara en tiempo real
     * 1. Busca o crea la cámara
     * 2. Crea persona si se detecta una o si faltan datos de persona
     * 3. Registra la detección
     */
    public void procesarDatosCamara(CamaraDataDTO data) {
        // 1. Buscar o crear cámara
        Camara camara = buscarOCrearCamara(data);
        
        // 2. Crear persona siempre (con datos específicos o genéricos)
        Persona persona = crearPersona(data);
        
        // 3. Registrar la detección
        registrarDeteccion(persona.getIdPersona(), camara.getIdCamara(), data);
    }
    
    /**
     * Busca cámara por número, si no existe la crea
     */
    private Camara buscarOCrearCamara(CamaraDataDTO data) {
        // Buscar cámara existente por número
        Camara camaraExistente = camaraRepo.findByNcam(data.getNcam());
            
        if (camaraExistente != null) {
            // Actualizar posición y tiempo si cambió
            camaraExistente.setTime(data.getTime());
            camaraExistente.setPosX(data.getPosX());
            camaraExistente.setPosY(data.getPosY());
            camaraRepo.update(camaraExistente);
            return camaraExistente;
        } else {
            // Crear nueva cámara
            Camara nuevaCamara = new Camara();
            nuevaCamara.setNcam(data.getNcam());
            nuevaCamara.setTime(data.getTime());
            nuevaCamara.setPosX(data.getPosX());
            nuevaCamara.setPosY(data.getPosY());
            camaraRepo.save(nuevaCamara);
            return nuevaCamara;
        }
    }
    
    /**
     * Crea una nueva persona basada en los datos detectados
     * Si faltan datos de persona, crea una persona genérica
     */
    private Persona crearPersona(CamaraDataDTO data) {
        Persona persona = new Persona();
        
        // Si hay datos específicos de persona, usarlos
        if (data.getTipoPersona() != null && !data.getTipoPersona().isEmpty()) {
            try {
                Persona.TipoPersona tipo = Persona.TipoPersona.valueOf(data.getTipoPersona());
                persona.setTipo(tipo);
            } catch (IllegalArgumentException e) {
                // Valor por defecto si no es válido
                persona.setTipo(Persona.TipoPersona.E);
            }
            
            // Usar género proporcionado o valor por defecto
            persona.setGenero(data.getGenero() != null ? data.getGenero() : "No especificado");
        } else {
            // Si faltan datos de persona, crear persona genérica
            persona.setTipo(Persona.TipoPersona.E); // Tipo por defecto: Empleado
            persona.setGenero("No especificado"); // Género por defecto
        }
        
        personaRepo.save(persona);
        return persona;
    }
    
    /**
     * Registra la detección de persona en cámara
     */
    private void registrarDeteccion(String idPersona, String idCamara, CamaraDataDTO data) {
        PersonaCamara deteccion = new PersonaCamara();
        deteccion.setIdPersona(idPersona);
        deteccion.setIdCamara(idCamara);
        deteccion.setFechaDeteccion(data.getTime());
        
        // Usar ID proporcionado como valor de confianza (convertido a Double)
        // o valor por defecto si no se proporciona
        Double confianzaValue = data.getId() != null ? data.getId().doubleValue() / 100.0 : 0.5;
        deteccion.setConfianza(confianzaValue);
        
        personaCamaraRepo.save(deteccion);
    }
    
    /**
     * Obtiene datos para mapa de calor en un rango de tiempo
     */
    public List<PersonaCamara> obtenerDatosMapaCalor(LocalDateTime inicio, LocalDateTime fin) {
        // Implementar filtro por fecha si es necesario
        return personaCamaraRepo.findAll();
    }
}
