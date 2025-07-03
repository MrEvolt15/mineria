package com.mineria.back.mineria.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mineria.back.mineria.dto.CamaraDataDTO;
import com.mineria.back.mineria.service.CamaraDataService;
import com.mineria.back.mineria.service.CamaraDataOptimizedService;
import com.mineria.back.mineria.service.KmeansHeatMapService;
import com.mineria.back.mineria.repo.model.PersonaCamara;
import com.mineria.back.mineria.dto.HeatMapKmeansDTO;
import com.mineria.back.mineria.dto.CentroideDTO;

@RestController
@RequestMapping("/api/realtime")
public class RealTimeController {
    
    @Autowired
    private CamaraDataService camaraDataService;
    
    @Autowired
    private CamaraDataOptimizedService camaraDataOptimizedService;
    
    @Autowired
    private KmeansHeatMapService kmeansHeatMapService;
    
    /**
     * Endpoint para recibir datos de cámaras en tiempo real (OPTIMIZADO)
     */
    @PostMapping("/camera-data")
    // http://localhost:8080/api/realtime/camera-data
    public ResponseEntity<String> recibirDatosCamara(@RequestBody CamaraDataDTO data) {
        try {
            if (data == null) {
                return new ResponseEntity<>("Datos nulos", HttpStatus.BAD_REQUEST);
            }
            
            // Validaciones básicas
            if (data.getId() == null) {
                System.err.println("Error: Dato recibido tiene ID nulo");
                return new ResponseEntity<>("Error: ID no puede ser nulo", HttpStatus.BAD_REQUEST);
            }
            
            if (data.getNcam() == null) {
                System.err.println("Error: Número de cámara (ncam) no puede ser nulo");
                return new ResponseEntity<>("Error: Número de cámara requerido", HttpStatus.BAD_REQUEST);
            }
            
            // Asignar valores por defecto si faltan
            if (data.getTime() == null) {
                data.setTime(LocalDateTime.now());
            }
            
            if (data.getPosX() == null) {
                data.setPosX(new java.math.BigDecimal("0.0"));
            }
            
            if (data.getPosY() == null) {
                data.setPosY(new java.math.BigDecimal("0.0"));
            }
            
            System.out.println("Procesando dato individual con ID: " + data.getId());
            
            // Para alto tráfico, usa el servicio optimizado asíncrono
            camaraDataOptimizedService.procesarDatosCamaraAsync(data);
            return new ResponseEntity<>("Datos procesados exitosamente", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al procesar datos (Argumento inválido): " + e.getMessage());
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), 
                                      HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error al procesar datos: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error al procesar datos: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para procesar múltiples detecciones de una vez (OPTIMIZADO)
     */
    @PostMapping("/camera-data/batch")
    // http://localhost:8080/api/realtime/camera-data/batch
    public ResponseEntity<String> recibirDatosCamaraBatch(@RequestBody List<CamaraDataDTO> dataList) {
        try {
            if (dataList == null || dataList.isEmpty()) {
                return new ResponseEntity<>("Lista de datos vacía", HttpStatus.BAD_REQUEST);
            }
            
            // Validar que todos los elementos tengan ID
            for (int i = 0; i < dataList.size(); i++) {
                CamaraDataDTO data = dataList.get(i);
                if (data.getId() == null) {
                    System.err.println("Error en lote: Elemento " + i + " tiene ID nulo");
                    return new ResponseEntity<>("Error: Elemento " + i + " en el lote tiene ID nulo", 
                                              HttpStatus.BAD_REQUEST);
                }
            }
            
            System.out.println("Procesando lote de " + dataList.size() + " elementos");
            
            // Para alto tráfico, usa el procesamiento en lote optimizado
            camaraDataOptimizedService.procesarLoteOptimizado(dataList);
            return new ResponseEntity<>("Lote procesado exitosamente: " + dataList.size() + " registros", 
                                      HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al procesar lote (Argumento inválido): " + e.getMessage());
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), 
                                      HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error al procesar lote: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error al procesar lote: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para obtener datos del mapa de calor
     */
    @GetMapping("/heatmap-data")
    // http://localhost:8080/api/realtime/heatmap-data
    public ResponseEntity<List<PersonaCamara>> obtenerDatosMapaCalor(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fin) {
        try {
            LocalDateTime fechaInicio = inicio != null ? LocalDateTime.parse(inicio) : null;
            LocalDateTime fechaFin = fin != null ? LocalDateTime.parse(fin) : null;
            
            List<PersonaCamara> datos = camaraDataService.obtenerDatosMapaCalor(fechaInicio, fechaFin);
            return new ResponseEntity<>(datos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para obtener estadísticas básicas del sistema
     */
    @GetMapping("/stats")
    // http://localhost:8080/api/realtime/stats
    public ResponseEntity<String> obtenerEstadisticas() {
        try {
            List<PersonaCamara> detecciones = camaraDataService.obtenerTodasLasDetecciones();
            int totalDetecciones = detecciones != null ? detecciones.size() : 0;
            
            String stats = "=== ESTADÍSTICAS DEL SISTEMA ===\n" +
                          "Total detecciones: " + totalDetecciones + "\n" +
                          "Estado: Sistema funcionando correctamente\n" +
                          "Timestamp: " + LocalDateTime.now();
            
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error en el sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para obtener todas las detecciones persona-cámara
     */
    @GetMapping("/detecciones")
    // http://localhost:8080/api/realtime/detecciones
    public ResponseEntity<List<PersonaCamara>> obtenerTodasLasDetecciones() {
        try {
            List<PersonaCamara> detecciones = camaraDataService.obtenerTodasLasDetecciones();
            return new ResponseEntity<>(detecciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para obtener mapa de calor con centroides K-means (MINERÍA DE DATOS)
     */
    @GetMapping("/heatmap-kmeans")
    // http://localhost:8080/api/realtime/heatmap-kmeans
    public ResponseEntity<HeatMapKmeansDTO> obtenerMapaCalorConKmeans(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fin,
            @RequestParam(required = false, defaultValue = "4") Integer k) {
        try {
            // Validar parámetros
            if (k == null || k < 1 || k > 10) {
                System.err.println("Error en K-means: Parámetro k inválido: " + k);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            LocalDateTime fechaInicio = inicio != null ? LocalDateTime.parse(inicio) : null;
            LocalDateTime fechaFin = fin != null ? LocalDateTime.parse(fin) : null;
            
            System.out.println("Aplicando K-means con k=" + k + 
                             ", inicio=" + fechaInicio + 
                             ", fin=" + fechaFin);
            
            // Verificación preventiva: comprobar si hay suficientes datos válidos
            List<PersonaCamara> detecciones = camaraDataService.obtenerTodasLasDetecciones();
            if (detecciones == null || detecciones.size() < k) {
                System.err.println("Error en K-means: Insuficientes datos para K=" + k + 
                                 " (disponibles: " + (detecciones != null ? detecciones.size() : 0) + ")");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            HeatMapKmeansDTO resultado = kmeansHeatMapService.aplicarKmeansEnTiempoReal(fechaInicio, fechaFin, k);
            
            if (resultado == null) {
                System.err.println("Error en K-means: Resultado nulo del servicio");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.err.println("Error en K-means (Argumento inválido): " + e.getMessage());
            System.err.println("POSIBLE CAUSA: Datos corruptos en la base de datos");
            System.err.println("SOLUCIÓN: Ejecuta /api/realtime/limpiar-datos-corruptos");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error en K-means: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para obtener solo centroides del cache
     */
    @GetMapping("/centroides")
    // http://localhost:8080/api/realtime/centroides
    public ResponseEntity<List<CentroideDTO>> obtenerCentroides(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fin,
            @RequestParam(required = false, defaultValue = "4") Integer k) {
        try {
            // Validar parámetros
            if (k == null || k < 1 || k > 10) {
                System.err.println("Error obteniendo centroides: Parámetro k inválido: " + k);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            LocalDateTime fechaInicio = inicio != null ? LocalDateTime.parse(inicio) : null;
            LocalDateTime fechaFin = fin != null ? LocalDateTime.parse(fin) : null;
            
            System.out.println("Obteniendo centroides con k=" + k + 
                             ", inicio=" + fechaInicio + 
                             ", fin=" + fechaFin);
            
            List<CentroideDTO> centroides = kmeansHeatMapService.obtenerCentroidesDelCache(fechaInicio, fechaFin, k);
            
            // Si no hay centroides en cache, calcular
            if (centroides.isEmpty()) {
                System.out.println("Cache vacío, calculando nuevos centroides...");
                HeatMapKmeansDTO resultado = kmeansHeatMapService.aplicarKmeansEnTiempoReal(fechaInicio, fechaFin, k);
                if (resultado != null) {
                    centroides = resultado.getCentroides();
                }
            }
            
            return new ResponseEntity<>(centroides, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.err.println("Error obteniendo centroides (Argumento inválido): " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error obteniendo centroides: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para limpiar cache de centroides
     */
    @PostMapping("/limpiar-cache-kmeans")
    // http://localhost:8080/api/realtime/limpiar-cache-kmeans
    public ResponseEntity<String> limpiarCacheKmeans() {
        try {
            kmeansHeatMapService.limpiarCacheAntiguo();
            return new ResponseEntity<>("Cache de K-means limpiado exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error limpiando cache: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para verificar integridad de datos antes de K-means
     */
    @GetMapping("/verificar-datos")
    // http://localhost:8080/api/realtime/verificar-datos
    public ResponseEntity<String> verificarIntegridadDatos() {
        try {
            // Obtener todas las detecciones para verificar integridad
            List<PersonaCamara> detecciones = camaraDataService.obtenerTodasLasDetecciones();
            
            String mensaje = "Verificación completada:\n" + 
                           "Total registros en base de datos: " + detecciones.size() + "\n" +
                           "El error 'Id must not be null' indica que hay registros corruptos\n" +
                           "que están interfiriendo con el servicio K-means.\n\n" +
                           "SOLUCIÓN: Limpia la base de datos y reinicia con datos nuevos.";
            
            return new ResponseEntity<>(mensaje, HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>("Error verificando datos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para limpiar registros corruptos
     */
    @PostMapping("/limpiar-datos-corruptos")
    // http://localhost:8080/api/realtime/limpiar-datos-corruptos
    public ResponseEntity<String> limpiarDatosCorruptos() {
        try {
            // Limpiar cache de K-means
            kmeansHeatMapService.limpiarCacheAntiguo();
            
            // Obtener todas las detecciones para identificar problemas
            List<PersonaCamara> detecciones = camaraDataService.obtenerTodasLasDetecciones();
            int totalAntes = detecciones.size();
            int corruptos = 0;
            
            // Contar detecciones con problemas
            for (PersonaCamara deteccion : detecciones) {
                if (deteccion.getIdCamara() == null || deteccion.getIdCamara().trim().isEmpty()) {
                    corruptos++;
                }
            }
            
            String mensaje = "🧹 LIMPIEZA DE DATOS COMPLETADA\n" +
                           "═══════════════════════════════\n" +
                           "📊 Total registros: " + totalAntes + "\n" +
                           "⚠️ Registros con problemas detectados: " + corruptos + "\n" +
                           "✅ Cache K-means limpiado\n\n";
            
            if (corruptos > 0) {
                mensaje += "🔧 RECOMENDACIONES:\n" +
                          "1. Los registros con ID de cámara nulo serán omitidos automáticamente\n" +
                          "2. El sistema continuará funcionando con los datos válidos\n" +
                          "3. Nuevas detecciones se procesarán normalmente\n\n";
            } else {
                mensaje += "🎉 No se encontraron datos corruptos.\n" +
                          "El sistema está funcionando correctamente.\n\n";
            }
            
            mensaje += "💡 El sistema ahora filtra automáticamente datos inválidos.";
            
            return new ResponseEntity<>(mensaje, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error limpiando datos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
