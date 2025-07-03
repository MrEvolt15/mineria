package com.mineria.back.mineria.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mineria.back.mineria.dto.CentroideDTO;
import com.mineria.back.mineria.dto.DeteccionConPosicionDTO;
import com.mineria.back.mineria.dto.HeatMapKmeansDTO;
import com.mineria.back.mineria.repo.model.PersonaCamara;

/**
 * Servicio para implementar K-means en tiempo real para el mapa de calor
 */
@Service
public class KmeansHeatMapService {
    
    @Autowired
    private DeteccionDataService deteccionDataService;
    
    // Cache de centroides en memoria para actualización en tiempo real
    private Map<String, List<CentroideDTO>> centroidesCache = new ConcurrentHashMap<>();
    
    // Configuración por defecto
    private static final int K_DEFAULT = 3; // Número de clusters por defecto
    private static final int MAX_ITERACIONES = 10;
    private static final double CONVERGENCIA_THRESHOLD = 0.1;
    
    /**
     * Aplica K-means a los datos en tiempo real y devuelve centroides actualizados
     */
    public HeatMapKmeansDTO aplicarKmeansEnTiempoReal(LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer k) {
        // Obtener datos con posiciones
        List<DeteccionConPosicionDTO> deteccionesConPosicion = deteccionDataService.obtenerDeteccionesConPosicion(fechaInicio, fechaFin);
        
        if (deteccionesConPosicion.isEmpty()) {
            return new HeatMapKmeansDTO(new ArrayList<>(), new ArrayList<>());
        }
        
        // Aplicar K-means
        int numClusters = k != null ? k : Math.min(K_DEFAULT, deteccionesConPosicion.size());
        List<CentroideDTO> centroides = ejecutarKmeans(deteccionesConPosicion, numClusters);
        
        // Actualizar cache
        String cacheKey = generarCacheKey(fechaInicio, fechaFin, numClusters);
        centroidesCache.put(cacheKey, centroides);
        
        // Retornar resultado (detecciones originales se pueden cargar si es necesario)
        return new HeatMapKmeansDTO(new ArrayList<>(), centroides);
    }
    
    /**
     * Actualiza los centroides de forma incremental con una nueva detección
     */
    @Async
    public void actualizarCentroidesIncremental(PersonaCamara nuevaDeteccion) {
        // Convertir a detección con posición
        DeteccionConPosicionDTO deteccionConPosicion = deteccionDataService.convertirADeteccionConPosicion(nuevaDeteccion);
        
        if (deteccionConPosicion == null) {
            return;
        }
        
        // Para cada entrada en cache, actualizar incrementalmente
        for (Map.Entry<String, List<CentroideDTO>> entry : centroidesCache.entrySet()) {
            List<CentroideDTO> centroides = entry.getValue();
            
            // Encontrar el centroide más cercano
            CentroideDTO centroideMasCercano = encontrarCentroideMasCercano(deteccionConPosicion, centroides);
            
            if (centroideMasCercano != null) {
                // Incrementar densidad del centroide más cercano
                centroideMasCercano.incrementarDensidad();
                
                // Ajuste ligero de posición (learning rate bajo para estabilidad)
                ajustarPosicionCentroide(centroideMasCercano, deteccionConPosicion, 0.1);
            }
        }
    }
    
    /**
     * Implementación del algoritmo K-means
     */
    private List<CentroideDTO> ejecutarKmeans(List<DeteccionConPosicionDTO> detecciones, int k) {
        // Inicializar centroides aleatoriamente
        List<CentroideDTO> centroides = inicializarCentroides(detecciones, k);
        
        boolean convergio = false;
        int iteracion = 0;
        
        while (!convergio && iteracion < MAX_ITERACIONES) {
            // Asignar puntos a clusters
            Map<Integer, List<DeteccionConPosicionDTO>> clusters = asignarPuntosAClusters(detecciones, centroides);
            
            // Actualizar centroides
            List<CentroideDTO> nuevoCentroides = calcularNuevosCentroides(clusters, centroides);
            
            // Verificar convergencia
            convergio = verificarConvergencia(centroides, nuevoCentroides);
            centroides = nuevoCentroides;
            iteracion++;
        }
        
        // Calcular densidades finales
        calcularDensidadesCentroides(centroides, detecciones);
        
        return centroides;
    }
    
    /**
     * Inicializa centroides de forma aleatoria basada en los datos
     */
    private List<CentroideDTO> inicializarCentroides(List<DeteccionConPosicionDTO> detecciones, int k) {
        List<CentroideDTO> centroides = new ArrayList<>();
        Random random = new Random();
        
        // Calcular rangos de los datos
        double minX = detecciones.stream().mapToDouble(d -> d.getPosXAsDouble()).min().orElse(0.0);
        double maxX = detecciones.stream().mapToDouble(d -> d.getPosXAsDouble()).max().orElse(10.0);
        double minY = detecciones.stream().mapToDouble(d -> d.getPosYAsDouble()).min().orElse(0.0);
        double maxY = detecciones.stream().mapToDouble(d -> d.getPosYAsDouble()).max().orElse(10.0);
        
        // Crear k centroides aleatorios
        for (int i = 0; i < k; i++) {
            double x = minX + (maxX - minX) * random.nextDouble();
            double y = minY + (maxY - minY) * random.nextDouble();
            centroides.add(new CentroideDTO(x, y, i));
        }
        
        return centroides;
    }
    
    /**
     * Asigna cada punto al centroide más cercano
     */
    private Map<Integer, List<DeteccionConPosicionDTO>> asignarPuntosAClusters(List<DeteccionConPosicionDTO> detecciones, List<CentroideDTO> centroides) {
        Map<Integer, List<DeteccionConPosicionDTO>> clusters = new HashMap<>();
        
        // Inicializar clusters vacíos
        for (CentroideDTO centroide : centroides) {
            clusters.put(centroide.getClusterId(), new ArrayList<>());
        }
        
        // Asignar cada punto al cluster más cercano
        for (DeteccionConPosicionDTO deteccion : detecciones) {
            CentroideDTO centroideMasCercano = encontrarCentroideMasCercano(deteccion, centroides);
            if (centroideMasCercano != null) {
                clusters.get(centroideMasCercano.getClusterId()).add(deteccion);
            }
        }
        
        return clusters;
    }
    
    /**
     * Encuentra el centroide más cercano a una detección
     */
    private CentroideDTO encontrarCentroideMasCercano(DeteccionConPosicionDTO deteccion, List<CentroideDTO> centroides) {
        CentroideDTO masCercano = null;
        double distanciaMinima = Double.MAX_VALUE;
        
        Double x = deteccion.getPosXAsDouble();
        Double y = deteccion.getPosYAsDouble();
        
        for (CentroideDTO centroide : centroides) {
            double distancia = centroide.distanciaA(x, y);
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                masCercano = centroide;
            }
        }
        
        return masCercano;
    }
    
    /**
     * Calcula nuevos centroides basados en los clusters
     */
    private List<CentroideDTO> calcularNuevosCentroides(Map<Integer, List<DeteccionConPosicionDTO>> clusters, List<CentroideDTO> centroidesAnteriores) {
        List<CentroideDTO> nuevosCentroides = new ArrayList<>();
        
        for (Map.Entry<Integer, List<DeteccionConPosicionDTO>> entry : clusters.entrySet()) {
            Integer clusterId = entry.getKey();
            List<DeteccionConPosicionDTO> puntosCluster = entry.getValue();
            
            if (puntosCluster.isEmpty()) {
                // Mantener centroide anterior si no hay puntos asignados
                CentroideDTO centroideAnterior = centroidesAnteriores.stream()
                    .filter(c -> c.getClusterId().equals(clusterId))
                    .findFirst()
                    .orElse(new CentroideDTO(0.0, 0.0, clusterId));
                nuevosCentroides.add(centroideAnterior);
            } else {
                // Calcular promedio de posiciones
                double promX = puntosCluster.stream()
                    .mapToDouble(p -> p.getPosXAsDouble())
                    .average()
                    .orElse(0.0);
                double promY = puntosCluster.stream()
                    .mapToDouble(p -> p.getPosYAsDouble())
                    .average()
                    .orElse(0.0);
                
                CentroideDTO nuevoCentroide = new CentroideDTO(promX, promY, clusterId);
                nuevoCentroide.setDensidad(puntosCluster.size());
                nuevosCentroides.add(nuevoCentroide);
            }
        }
        
        return nuevosCentroides;
    }
    
    /**
     * Verifica si los centroides han convergido
     */
    private boolean verificarConvergencia(List<CentroideDTO> centroidesAnteriores, List<CentroideDTO> nuevosCentroides) {
        if (centroidesAnteriores.size() != nuevosCentroides.size()) {
            return false;
        }
        
        for (int i = 0; i < centroidesAnteriores.size(); i++) {
            CentroideDTO anterior = centroidesAnteriores.get(i);
            CentroideDTO nuevo = nuevosCentroides.get(i);
            
            double distancia = anterior.distanciaA(nuevo.getPosX(), nuevo.getPosY());
            if (distancia > CONVERGENCIA_THRESHOLD) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calcula las densidades finales de los centroides
     */
    private void calcularDensidadesCentroides(List<CentroideDTO> centroides, List<DeteccionConPosicionDTO> detecciones) {
        Map<Integer, List<DeteccionConPosicionDTO>> clusters = asignarPuntosAClusters(detecciones, centroides);
        
        for (CentroideDTO centroide : centroides) {
            List<DeteccionConPosicionDTO> puntosCluster = clusters.get(centroide.getClusterId());
            centroide.setDensidad(puntosCluster != null ? puntosCluster.size() : 0);
        }
    }
    
    /**
     * Ajusta la posición de un centroide de forma incremental
     */
    private void ajustarPosicionCentroide(CentroideDTO centroide, DeteccionConPosicionDTO nuevoPunto, double learningRate) {
        double x = centroide.getPosX();
        double y = centroide.getPosY();
        double nuevoX = nuevoPunto.getPosXAsDouble();
        double nuevoY = nuevoPunto.getPosYAsDouble();
        
        // Ajuste incremental con learning rate
        centroide.setPosX(x + learningRate * (nuevoX - x));
        centroide.setPosY(y + learningRate * (nuevoY - y));
    }
    
    /**
     * Genera clave para el cache
     */
    private String generarCacheKey(LocalDateTime fechaInicio, LocalDateTime fechaFin, int k) {
        return String.format("kmeans_%s_%s_%d", 
                           fechaInicio != null ? fechaInicio.toString() : "null",
                           fechaFin != null ? fechaFin.toString() : "null", 
                           k);
    }
    
    /**
     * Limpia el cache de centroides antiguos
     */
    public void limpiarCacheAntiguo() {
        centroidesCache.clear();
    }
    
    /**
     * Obtiene centroides del cache para una configuración específica
     */
    public List<CentroideDTO> obtenerCentroidesDelCache(LocalDateTime fechaInicio, LocalDateTime fechaFin, int k) {
        String cacheKey = generarCacheKey(fechaInicio, fechaFin, k);
        return centroidesCache.getOrDefault(cacheKey, new ArrayList<>());
    }
}
