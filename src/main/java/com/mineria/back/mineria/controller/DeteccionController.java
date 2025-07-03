package com.mineria.back.mineria.controller;

import com.mineria.back.mineria.repo.model.Deteccion;
import com.mineria.back.mineria.service.DeteccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DeteccionController {
    
    @Autowired
    private DeteccionService deteccionService;
    
    /**
     * GET /detecciones?segundo=2025-07-03T11:10:01
     * Obtiene todas las detecciones que ocurrieron en un segundo específico
     */
    @GetMapping("/detecciones")
    public ResponseEntity<Map<String, Object>> obtenerDetecciones(
            @RequestParam(required = false) String segundo,
            @RequestParam(required = false) String rango) {
        
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Validar que solo se proporcione uno de los parámetros
            if (segundo != null && rango != null) {
                response.put("error", "Solo se puede usar 'segundo' o 'rango', no ambos");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (segundo == null && rango == null) {
                response.put("error", "Debe proporcionar el parámetro 'segundo' o 'rango'");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Deteccion> detecciones;
            
            if (segundo != null) {
                // Buscar por segundo específico
                detecciones = deteccionService.obtenerDeteccionesPorSegundo(segundo);
                response.put("tipo_consulta", "segundo");
                response.put("parametro", segundo);
            } else {
                // Buscar por rango
                detecciones = deteccionService.obtenerDeteccionesPorRango(rango);
                response.put("tipo_consulta", "rango");
                response.put("parametro", rango);
            }
            
            response.put("detecciones", detecciones);
            response.put("total_encontradas", detecciones.size());
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * POST /detecciones
     * Crea una nueva detección
     */
    @PostMapping("/detecciones")
    public ResponseEntity<Map<String, Object>> crearDeteccion(@RequestBody Deteccion deteccion) {
        try {
            Deteccion deteccionGuardada = deteccionService.guardarDeteccion(deteccion);
            
            Map<String, Object> response = new HashMap<>();
            response.put("deteccion", deteccionGuardada);
            response.put("success", true);
            response.put("mensaje", "Detección guardada exitosamente");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /detecciones/todas
     * Obtiene todas las detecciones ordenadas por timestamp
     */
    @GetMapping("/detecciones/todas")
    public ResponseEntity<Map<String, Object>> obtenerTodasLasDetecciones() {
        try {
            List<Deteccion> detecciones = deteccionService.obtenerTodasLasDetecciones();
            
            Map<String, Object> response = new HashMap<>();
            response.put("detecciones", detecciones);
            response.put("total", detecciones.size());
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /detecciones/ultimas?limite=10
     * Obtiene las últimas N detecciones
     */
    @GetMapping("/detecciones/ultimas")
    public ResponseEntity<Map<String, Object>> obtenerUltimasDetecciones(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Deteccion> detecciones = deteccionService.obtenerUltimasDetecciones(limite);
            
            Map<String, Object> response = new HashMap<>();
            response.put("detecciones", detecciones);
            response.put("limite_solicitado", limite);
            response.put("total_encontradas", detecciones.size());
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /detecciones/estadisticas
     * Obtiene estadísticas básicas de las detecciones
     */
    @GetMapping("/detecciones/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        try {
            DeteccionService.DeteccionStats stats = deteccionService.obtenerEstadisticas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("total_detecciones", stats.getTotalDetecciones());
            response.put("ultimas_5_detecciones", stats.getUltimasDetecciones());
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * DELETE /detecciones/limpiar
     * Elimina todas las detecciones (para pruebas y limpieza)
     */
    @DeleteMapping("/detecciones/limpiar")
    public ResponseEntity<Map<String, Object>> limpiarDetecciones() {
        try {
            deteccionService.limpiarTodasLasDetecciones();
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Todas las detecciones han sido eliminadas");
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /detecciones/ejemplo
     * Retorna un ejemplo del formato esperado para las detecciones
     */
    @GetMapping("/detecciones/ejemplo")
    public ResponseEntity<Map<String, Object>> obtenerEjemplo() {
        Map<String, Object> ejemplo = new HashMap<>();
        ejemplo.put("timestamp", "2025-07-03T14:18:00");
        ejemplo.put("personas", 2);
        
        List<Map<String, Object>> coordenadas = new ArrayList<>();
        
        Map<String, Object> coord1 = new HashMap<>();
        coord1.put("x", 450.2);
        coord1.put("y", 800.1);
        coordenadas.add(coord1);
        
        Map<String, Object> coord2 = new HashMap<>();
        coord2.put("x", 470.0);
        coord2.put("y", 790.5);
        coordenadas.add(coord2);
        
        ejemplo.put("coordenadas", coordenadas);
        
        Map<String, Object> response = new HashMap<>();
        response.put("formato_deteccion", ejemplo);
        response.put("nota", "Use POST /api/detecciones para enviar detecciones en este formato");
        response.put("consultas", new String[]{
            "GET /api/detecciones?segundo=2025-07-03T11:10:01",
            "GET /api/detecciones?rango=2025-07-03T11:00:00,2025-07-03T11:10:00"
        });
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
}
