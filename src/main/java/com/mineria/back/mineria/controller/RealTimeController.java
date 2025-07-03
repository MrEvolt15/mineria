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
import com.mineria.back.mineria.repo.model.PersonaCamara;

@RestController
@RequestMapping("/api/realtime")
public class RealTimeController {
    
    @Autowired
    private CamaraDataService camaraDataService;
    
    /**
     * Endpoint para recibir datos de cámaras en tiempo real
     */
    @PostMapping("/camera-data")
    // http://localhost:8080/api/realtime/camera-data
    public ResponseEntity<String> recibirDatosCamara(@RequestBody CamaraDataDTO data) {
        try {
            camaraDataService.procesarDatosCamara(data);
            return new ResponseEntity<>("Datos procesados exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al procesar datos: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint para procesar múltiples detecciones de una vez
     */
    @PostMapping("/camera-data/batch")
    // http://localhost:8080/api/realtime/camera-data/batch
    public ResponseEntity<String> recibirDatosCamaraBatch(@RequestBody List<CamaraDataDTO> dataList) {
        try {
            for (CamaraDataDTO data : dataList) {
                camaraDataService.procesarDatosCamara(data);
            }
            return new ResponseEntity<>("Lote procesado exitosamente: " + dataList.size() + " registros", 
                                      HttpStatus.OK);
        } catch (Exception e) {
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
            return new ResponseEntity<>("Sistema funcionando correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error en el sistema", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
