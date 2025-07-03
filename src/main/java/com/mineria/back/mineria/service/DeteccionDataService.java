package com.mineria.back.mineria.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mineria.back.mineria.dto.DeteccionConPosicionDTO;
import com.mineria.back.mineria.repo.ICamaraRepo;
import com.mineria.back.mineria.repo.IPersonaCamaraRepo;
import com.mineria.back.mineria.repo.model.Camara;
import com.mineria.back.mineria.repo.model.PersonaCamara;

/**
 * Servicio para combinar datos de PersonaCamara con Camara para obtener posiciones
 */
@Service
public class DeteccionDataService {
    
    @Autowired
    private IPersonaCamaraRepo personaCamaraRepo;
    
    @Autowired
    private ICamaraRepo camaraRepo;
    
    /**
     * Obtiene detecciones con datos de posición para K-means
     */
    public List<DeteccionConPosicionDTO> obtenerDeteccionesConPosicion(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Obtener detecciones
        List<PersonaCamara> detecciones;
        if (fechaInicio != null || fechaFin != null) {
            detecciones = personaCamaraRepo.findByFechaDeteccionBetween(fechaInicio, fechaFin);
        } else {
            detecciones = personaCamaraRepo.findAll();
        }
        
        // Combinar con datos de cámara
        List<DeteccionConPosicionDTO> resultado = new ArrayList<>();
        
        for (PersonaCamara deteccion : detecciones) {
            // Validar que la detección tenga ID de cámara válido
            if (deteccion.getIdCamara() == null || deteccion.getIdCamara().trim().isEmpty()) {
                System.err.println("Advertencia: Detección con ID de cámara nulo o vacío: " + deteccion.getIdPersonaCamara());
                continue; // Omitir esta detección
            }
            
            try {
                // Buscar datos de la cámara
                Camara camara = camaraRepo.findById(deteccion.getIdCamara());
                
                if (camara != null && camara.getPosX() != null && camara.getPosY() != null) {
                    DeteccionConPosicionDTO dto = new DeteccionConPosicionDTO(
                        deteccion.getIdPersonaCamara(),
                        deteccion.getIdPersona(),
                        deteccion.getIdCamara(),
                        deteccion.getFechaDeteccion(),
                        deteccion.getConfianza(),
                        camara.getPosX(),
                        camara.getPosY(),
                        camara.getNcam()
                    );
                    resultado.add(dto);
                } else {
                    System.err.println("Advertencia: Cámara no encontrada o sin posición válida para ID: " + deteccion.getIdCamara());
                }
            } catch (Exception e) {
                System.err.println("Error procesando detección " + deteccion.getIdPersonaCamara() + ": " + e.getMessage());
            }
        }
        
        return resultado;
    }
    
    /**
     * Obtiene una detección específica con posición
     */
    public DeteccionConPosicionDTO obtenerDeteccionConPosicion(String idPersonaCamara) {
        if (idPersonaCamara == null || idPersonaCamara.trim().isEmpty()) {
            return null;
        }
        
        PersonaCamara deteccion = personaCamaraRepo.findById(idPersonaCamara);
        if (deteccion == null) {
            return null;
        }
        
        // Validar ID de cámara
        if (deteccion.getIdCamara() == null || deteccion.getIdCamara().trim().isEmpty()) {
            System.err.println("Advertencia: Detección con ID de cámara nulo: " + idPersonaCamara);
            return null;
        }
        
        try {
            Camara camara = camaraRepo.findById(deteccion.getIdCamara());
            if (camara == null) {
                return null;
            }
            
            return new DeteccionConPosicionDTO(
                deteccion.getIdPersonaCamara(),
                deteccion.getIdPersona(),
                deteccion.getIdCamara(),
                deteccion.getFechaDeteccion(),
                deteccion.getConfianza(),
                camara.getPosX(),
                camara.getPosY(),
                camara.getNcam()
            );
        } catch (Exception e) {
            System.err.println("Error obteniendo detección con posición: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene detecciones con posición para una cámara específica
     */
    public List<DeteccionConPosicionDTO> obtenerDeteccionesPorCamara(String idCamara, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (idCamara == null || idCamara.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            List<PersonaCamara> detecciones = personaCamaraRepo.findByIdCamara(idCamara);
            
            // Filtrar por fecha si se especifica
            if (fechaInicio != null || fechaFin != null) {
                detecciones = detecciones.stream()
                    .filter(d -> {
                        LocalDateTime fecha = d.getFechaDeteccion();
                        boolean despuesInicio = fechaInicio == null || fecha.isAfter(fechaInicio) || fecha.isEqual(fechaInicio);
                        boolean antesFin = fechaFin == null || fecha.isBefore(fechaFin) || fecha.isEqual(fechaFin);
                        return despuesInicio && antesFin;
                    })
                    .collect(Collectors.toList());
            }
            
            // Obtener datos de la cámara una sola vez
            Camara camara = camaraRepo.findById(idCamara);
            if (camara == null) {
                return new ArrayList<>();
            }
            
            // Crear DTOs
            return detecciones.stream()
                .map(deteccion -> new DeteccionConPosicionDTO(
                    deteccion.getIdPersonaCamara(),
                    deteccion.getIdPersona(),
                    deteccion.getIdCamara(),
                    deteccion.getFechaDeteccion(),
                    deteccion.getConfianza(),
                    camara.getPosX(),
                    camara.getPosY(),
                    camara.getNcam()
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error obteniendo detecciones por cámara: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Convierte PersonaCamara a DeteccionConPosicionDTO
     */
    public DeteccionConPosicionDTO convertirADeteccionConPosicion(PersonaCamara personaCamara) {
        if (personaCamara == null) {
            return null;
        }
        
        Camara camara = camaraRepo.findById(personaCamara.getIdCamara());
        if (camara == null) {
            return null;
        }
        
        return new DeteccionConPosicionDTO(
            personaCamara.getIdPersonaCamara(),
            personaCamara.getIdPersona(),
            personaCamara.getIdCamara(),
            personaCamara.getFechaDeteccion(),
            personaCamara.getConfianza(),
            camara.getPosX(),
            camara.getPosY(),
            camara.getNcam()
        );
    }
}
