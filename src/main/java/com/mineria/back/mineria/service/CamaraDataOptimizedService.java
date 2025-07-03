package com.mineria.back.mineria.service;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mineria.back.mineria.dto.CamaraDataDTO;
import com.mineria.back.mineria.repo.ICamaraRepo;
import com.mineria.back.mineria.repo.IPersonaCamaraRepo;
import com.mineria.back.mineria.repo.IPersonaRepo;
import com.mineria.back.mineria.repo.model.Camara;
import com.mineria.back.mineria.repo.model.Persona;
import com.mineria.back.mineria.repo.model.PersonaCamara;

@Service
public class CamaraDataOptimizedService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private ICamaraRepo camaraRepo;
    
    @Autowired
    private IPersonaRepo personaRepo;
    
    @Autowired
    private IPersonaCamaraRepo personaCamaraRepo;
    
    @Autowired
    private KmeansHeatMapService kmeansHeatMapService;
    
    // Pool de hilos para procesamiento asíncrono
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    /**
     * Procesamiento asíncrono de datos individuales
     */
    @Async
    public CompletableFuture<Void> procesarDatosCamaraAsync(CamaraDataDTO data) {
        return CompletableFuture.runAsync(() -> {
            try {
                procesarDatosCamaraInterno(data);
            } catch (Exception e) {
                // Log error pero no bloquear otros procesamientos
                System.err.println("Error procesando datos de cámara: " + e.getMessage());
            }
        }, executorService);
    }
    
    /**
     * Procesamiento en lote optimizado para alta velocidad
     */
    public void procesarLoteOptimizado(List<CamaraDataDTO> dataList) {
        if (dataList.isEmpty()) return;
        
        try {
            // Separar datos por tipo de operación
            List<Camara> camarasParaInsertar = new ArrayList<>();
            List<Persona> personasParaInsertar = new ArrayList<>();
            List<PersonaCamara> relacionesParaInsertar = new ArrayList<>();
            
            // Procesar datos en memoria sin consultas a BD
            for (CamaraDataDTO data : dataList) {
                // Crear cámara
                Camara camara = crearCamaraEnMemoria(data);
                camarasParaInsertar.add(camara);
                
                // Crear persona
                Persona persona = crearPersonaEnMemoria(data);
                personasParaInsertar.add(persona);
                
                // Crear relación (usando IDs temporales)
                PersonaCamara relacion = crearRelacionEnMemoria(data, persona, camara);
                relacionesParaInsertar.add(relacion);
            }
            
            // Inserción masiva en BD
            insertarEnLote(camarasParaInsertar, personasParaInsertar, relacionesParaInsertar);
            
        } catch (Exception e) {
            System.err.println("Error en procesamiento por lotes: " + e.getMessage());
        }
    }
    
    /**
     * Procesamiento interno individual
     */
    private void procesarDatosCamaraInterno(CamaraDataDTO data) {
        // Lógica optimizada individual
        Camara camara = buscarOCrearCamaraOptimizado(data);
        Persona persona = crearPersonaOptimizada(data);
        PersonaCamara deteccion = registrarDeteccionOptimizada(persona.getIdPersona(), camara.getIdCamara(), data);
        
        // Actualizar centroides K-means de forma incremental
        if (deteccion != null) {
            kmeansHeatMapService.actualizarCentroidesIncremental(deteccion);
        }
    }
    
    /**
     * Creación de cámara en memoria (sin consultar BD)
     */
    private Camara crearCamaraEnMemoria(CamaraDataDTO data) {
        Camara camara = new Camara();
        camara.setNcam(data.getNcam());
        camara.setTime(data.getTime());
        camara.setPosX(data.getPosX());
        camara.setPosY(data.getPosY());
        return camara;
    }
    
    /**
     * Creación de persona en memoria
     */
    private Persona crearPersonaEnMemoria(CamaraDataDTO data) {
        Persona persona = new Persona();
        
        if (data.getTipoPersona() != null && !data.getTipoPersona().isEmpty()) {
            try {
                Persona.TipoPersona tipo = Persona.TipoPersona.valueOf(data.getTipoPersona());
                persona.setTipo(tipo);
            } catch (IllegalArgumentException e) {
                persona.setTipo(Persona.TipoPersona.E);
            }
            persona.setGenero(data.getGenero() != null ? data.getGenero() : "No especificado");
        } else {
            persona.setTipo(Persona.TipoPersona.E);
            persona.setGenero("No especificado");
        }
        
        return persona;
    }
    
    /**
     * Creación de relación en memoria
     */
    private PersonaCamara crearRelacionEnMemoria(CamaraDataDTO data, Persona persona, Camara camara) {
        PersonaCamara relacion = new PersonaCamara();
        relacion.setFechaDeteccion(data.getTime());
        
        Double confianzaValue = data.getId() != null ? data.getId().doubleValue() / 100.0 : 0.5;
        relacion.setConfianza(confianzaValue);
        
        return relacion;
    }
    
    /**
     * Inserción masiva usando BulkOperations
     */
    private void insertarEnLote(List<Camara> camaras, List<Persona> personas, List<PersonaCamara> relaciones) {
        try {
            // Insertar cámaras en lote
            if (!camaras.isEmpty()) {
                BulkOperations bulkCamaras = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Camara.class);
                bulkCamaras.insert(camaras);
                bulkCamaras.execute();
            }
            
            // Insertar personas en lote
            if (!personas.isEmpty()) {
                BulkOperations bulkPersonas = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Persona.class);
                bulkPersonas.insert(personas);
                bulkPersonas.execute();
            }
            
            // Actualizar relaciones con IDs reales y insertar
            if (!relaciones.isEmpty()) {
                // Aquí necesitarías actualizar los IDs de persona y cámara con los IDs reales generados
                // Para simplificar, usamos los IDs generados automáticamente
                for (int i = 0; i < relaciones.size(); i++) {
                    relaciones.get(i).setIdPersona(personas.get(i).getIdPersona());
                    relaciones.get(i).setIdCamara(camaras.get(i).getIdCamara());
                }
                
                BulkOperations bulkRelaciones = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, PersonaCamara.class);
                bulkRelaciones.insert(relaciones);
                bulkRelaciones.execute();
            }
            
        } catch (Exception e) {
            System.err.println("Error en inserción masiva: " + e.getMessage());
        }
    }
    
    // Métodos optimizados individuales
    private Camara buscarOCrearCamaraOptimizado(CamaraDataDTO data) {
        Camara camaraExistente = camaraRepo.findByNcam(data.getNcam());
        
        if (camaraExistente != null) {
            camaraExistente.setTime(data.getTime());
            camaraExistente.setPosX(data.getPosX());
            camaraExistente.setPosY(data.getPosY());
            camaraRepo.update(camaraExistente);
            return camaraExistente;
        } else {
            Camara nuevaCamara = crearCamaraEnMemoria(data);
            camaraRepo.save(nuevaCamara);
            return nuevaCamara;
        }
    }
    
    private Persona crearPersonaOptimizada(CamaraDataDTO data) {
        Persona persona = crearPersonaEnMemoria(data);
        personaRepo.save(persona);
        return persona;
    }
    
    private PersonaCamara registrarDeteccionOptimizada(String idPersona, String idCamara, CamaraDataDTO data) {
        PersonaCamara deteccion = new PersonaCamara();
        deteccion.setIdPersona(idPersona);
        deteccion.setIdCamara(idCamara);
        deteccion.setFechaDeteccion(data.getTime());
        
        Double confianzaValue = data.getId() != null ? data.getId().doubleValue() / 100.0 : 0.5;
        deteccion.setConfianza(confianzaValue);
        
        personaCamaraRepo.save(deteccion);
        return deteccion; // Retornar la detección para K-means
    }
}
