package com.mineria.back.mineria.controller;

import com.mineria.back.mineria.repo.model.Deteccion;
import com.mineria.back.mineria.service.DeteccionService;
import com.mineria.back.mineria.dto.DeteccionDTO;
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
    /**
     * Ejemplo de respuesta para GET /api/detecciones?segundo=2025-07-03T11:10:01
     * {
     *   "tipo_consulta": "segundo",
     *   "parametro": "2025-07-03T11:10:01",
     *   "detecciones": [
     *     {
     *       "timestamp": "2025-07-03T11:10:01",
     *       "personas": 2,
     *       "coordenadas": [
     *         {"x": 450.2, "y": 800.1},
     *         {"x": 470.0, "y": 790.5}
     *       ]
     *     }
     *   ],
     *   "total_encontradas": 1,
     *   "success": true
     * }
     */
    @GetMapping("/detecciones")
    // http://localhost:8080/api/detecciones?segundo=2025-07-03T11:10:01
    // http://localhost:8080/api/detecciones?rango=2025-07-03T11:00:00,2025-07-03T11:10:00
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
    // http://localhost:8080/api/detecciones
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
     * 
     * Ejemplo de respuesta:
     * {
     *   "detecciones": [
     *     {
     *       "timestamp": "2025-07-03T11:10:01",
     *       "personas": 2,
     *       "coordenadas": [
     *         {"x": 450.2, "y": 800.1},
     *         {"x": 470.0, "y": 790.5}
     *       ]
     *     },
     *     ...
     *   ],
     *   "total": 15,
     *   "success": true
     * }
     */
    @GetMapping("/detecciones/todas")
    // http://localhost:8080/api/detecciones/todas
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
    
    /**
     * GET /detecciones/horas-disponibles
     * Obtiene todas las horas únicas disponibles en las detecciones
     */
    @GetMapping("/detecciones/horas-disponibles")
    // http://localhost:8080/api/detecciones/horas-disponibles
    public ResponseEntity<Map<String, Object>> obtenerHorasDisponibles() {
        try {
            List<String> horasDisponibles = deteccionService.obtenerHorasDisponibles();
            
            Map<String, Object> response = new HashMap<>();
            response.put("horas_disponibles", horasDisponibles);
            response.put("total_horas", horasDisponibles.size());
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
     * GET /detecciones/timestamps
     * Obtiene solo los timestamps de todas las detecciones
     * 
     * Ejemplo de respuesta:
     * {
     *   "timestamps": [
     *     "2025-07-03T14:18:00",
     *     "2025-07-03T14:19:15",
     *     "2025-07-03T14:20:30"
     *   ],
     *   "total": 3,
     *   "success": true
     * }
     */
    @GetMapping("/detecciones/timestamps")
    // http://localhost:8080/api/detecciones/timestamps
    public ResponseEntity<Map<String, Object>> obtenerTimestamps() {
        try {
            List<String> timestamps = deteccionService.obtenerTodosLosTimestamps();
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamps", timestamps);
            response.put("total", timestamps.size());
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
     * GET /detecciones/clustering
     * Aplica clustering DBSCAN a todas las detecciones y retorna los resultados enriquecidos
     * 
     * Parámetros opcionales:
     * - eps: Radio de vecindad para DBSCAN (default: 0.5)
     * - minSamples: Número mínimo de puntos para formar un cluster (default: 3)
     * 
     * Ejemplo de respuesta:
     * {
     *   "detecciones": [
     *     {
     *       "id": "66c8a7c123456789abcdef01",
     *       "timestamp": "2025-07-03T11:10:01",
     *       "personas": 2,
     *       "coordenadas": [
     *         {"x": 450.2, "y": 800.1},
     *         {"x": 470.0, "y": 790.5}
     *       ],
     *       "clusterId": 0,
     *       "clusterTipo": "CLUSTER",
     *       "densidadCluster": 0.85,
     *       "puntosEnCluster": 12,
     *       "centroideCluster": {"x": 460.5, "y": 795.3}
     *     }
     *   ],
     *   "total": 1,
     *   "success": true
     * }
     */
    @GetMapping("/detecciones/clustering")
    // http://localhost:8080/api/detecciones/clustering
    // http://localhost:8080/api/detecciones/clustering?eps=0.3&minSamples=5
    public ResponseEntity<Map<String, Object>> aplicarClustering(
            @RequestParam(required = false, defaultValue = "0.5") Double eps,
            @RequestParam(required = false, defaultValue = "3") Integer minSamples) {
        try {
            // Validar parámetros
            if (eps <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El parámetro 'eps' debe ser mayor que 0");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (minSamples <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El parámetro 'minSamples' debe ser mayor que 0");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Aplicar clustering
            List<DeteccionDTO> deteccionesConClustering = 
                deteccionService.aplicarClusteringDBSCAN(eps, minSamples);
            
            Map<String, Object> response = new HashMap<>();
            response.put("detecciones", deteccionesConClustering);
            response.put("total", deteccionesConClustering.size());
            response.put("parametros", Map.of("eps", eps, "minSamples", minSamples));
            response.put("success", true);
            
            // Agregar estadísticas de clustering
            long totalClusters = deteccionesConClustering.stream()
                .filter(d -> d.getClusterId() != null && d.getClusterId() >= 0)
                .mapToInt(DeteccionDTO::getClusterId)
                .distinct()
                .count();
            
            long puntosRuido = deteccionesConClustering.stream()
                .filter(d -> "RUIDO".equals(d.getClusterTipo()))
                .count();
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalClusters", totalClusters);
            estadisticas.put("puntosRuido", puntosRuido);
            estadisticas.put("puntosEnClusters", deteccionesConClustering.size() - puntosRuido);
            
            response.put("estadisticas", estadisticas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error aplicando clustering: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /detecciones/clustering/restringido
     * Aplica clustering DBSCAN a detecciones en un rango de tiempo específico
     * 
     * Parámetros:
     * - eps: Radio de vecindad para DBSCAN (default: 0.5)
     * - minSamples: Número mínimo de puntos para formar un cluster (default: 3)
     * - inicio: Timestamp de inicio (formato: yyyy-MM-ddTHH:mm:ss)
     * - fin: Timestamp de fin (formato: yyyy-MM-ddTHH:mm:ss)
     */
    @GetMapping("/detecciones/clustering/restringido")
    // http://localhost:8080/api/detecciones/clustering/restringido?eps=0.3&minSamples=5&inicio=2025-07-03T11:00:00&fin=2025-07-03T12:00:00
    // http://localhost:8080/api/detecciones/clustering/restringido?inicio=2025-07-03T11:00:00&fin=2025-07-03T12:00:00
    public ResponseEntity<Map<String, Object>> aplicarClusteringRestringido(
            @RequestParam(required = false, defaultValue = "0.5") Double eps,
            @RequestParam(required = false, defaultValue = "3") Integer minSamples,
            @RequestParam(required = true) String inicio,
            @RequestParam(required = true) String fin) {
        try {
            // Validar parámetros
            if (eps <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El parámetro 'eps' debe ser mayor que 0");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (minSamples <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El parámetro 'minSamples' debe ser mayor que 0");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (inicio == null || inicio.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El parámetro 'inicio' es obligatorio");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (fin == null || fin.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El parámetro 'fin' es obligatorio");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Aplicar clustering restringido
            List<DeteccionDTO> deteccionesConClustering = 
                deteccionService.aplicarClusteringDBSCANRestringido(eps, minSamples, inicio, fin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("detecciones", deteccionesConClustering);
            response.put("total", deteccionesConClustering.size());
            response.put("parametros", Map.of(
                "eps", eps, 
                "minSamples", minSamples,
                "inicio", inicio,
                "fin", fin));
            response.put("success", true);
            
            // Agregar estadísticas de clustering
            long totalClusters = deteccionesConClustering.stream()
                .filter(d -> d.getClusterId() != null && d.getClusterId() >= 0)
                .mapToInt(DeteccionDTO::getClusterId)
                .distinct()
                .count();
            
            long puntosRuido = deteccionesConClustering.stream()
                .filter(d -> "RUIDO".equals(d.getClusterTipo()))
                .count();
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalClusters", totalClusters);
            estadisticas.put("puntosRuido", puntosRuido);
            estadisticas.put("puntosEnClusters", deteccionesConClustering.size() - puntosRuido);
            
            response.put("estadisticas", estadisticas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error aplicando clustering restringido: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /detecciones/clustering/analisis
     * Obtiene análisis detallado de clustering con diagnósticos
     * 
     * Parámetros opcionales:
     * - eps: Radio de vecindad para DBSCAN (default: 0.5)
     * - minSamples: Número mínimo de puntos para formar un cluster (default: 3)
     * 
     * Incluye información de diagnóstico para entender por qué se obtienen ciertos resultados
     */
    @GetMapping("/detecciones/clustering/analisis")
    // http://localhost:8080/api/detecciones/clustering/analisis
    // http://localhost:8080/api/detecciones/clustering/analisis?eps=0.3&minSamples=5
    public ResponseEntity<Map<String, Object>> obtenerAnalisisClusteringDetallado(
            @RequestParam(required = false, defaultValue = "0.5") Double eps,
            @RequestParam(required = false, defaultValue = "3") Integer minSamples) {
        try {
            // Validar parámetros
            if (eps <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El parámetro 'eps' debe ser mayor que 0");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (minSamples <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El parámetro 'minSamples' debe ser mayor que 0");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Obtener análisis detallado
            Map<String, Object> analisis = deteccionService.obtenerAnalisisClusteringDetallado(eps, minSamples);
            analisis.put("success", true);
            
            return ResponseEntity.ok(analisis);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error en análisis de clustering: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
