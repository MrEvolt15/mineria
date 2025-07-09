package com.mineria.back.mineria.service;

import com.mineria.back.mineria.repo.DeteccionRepositoryImpl;
import com.mineria.back.mineria.repo.model.Deteccion;
import com.mineria.back.mineria.dto.DeteccionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeteccionService {
    
    @Autowired
    private DeteccionRepositoryImpl deteccionRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Busca detecciones por segundo específico
     */
    public List<Deteccion> obtenerDeteccionesPorSegundo(String timestampStr) {
        try {
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, TIMESTAMP_FORMATTER);
            return deteccionRepository.findBySecond(timestamp);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Formato de timestamp inválido. Use: yyyy-MM-ddTHH:mm:ss", e);
        }
    }
    
    /**
     * Busca detecciones en un rango de tiempo
     */
    public List<Deteccion> obtenerDeteccionesPorRango(String rangoStr) {
        try {
            String[] partes = rangoStr.split(",");
            if (partes.length != 2) {
                throw new IllegalArgumentException(
                    "El rango debe tener el formato: timestamp1,timestamp2");
            }
            
            LocalDateTime desde = LocalDateTime.parse(partes[0].trim(), TIMESTAMP_FORMATTER);
            LocalDateTime hasta = LocalDateTime.parse(partes[1].trim(), TIMESTAMP_FORMATTER);
            
            if (desde.isAfter(hasta)) {
                throw new IllegalArgumentException(
                    "La fecha de inicio debe ser anterior a la fecha de fin");
            }
            
            return deteccionRepository.findByRange(desde, hasta);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Formato de timestamp inválido en el rango. Use: yyyy-MM-ddTHH:mm:ss", e);
        }
    }
    
    /**
     * Guarda una nueva detección
     */
    public Deteccion guardarDeteccion(Deteccion deteccion) {
        // Validaciones adicionales de negocio
        validarDeteccion(deteccion);
        
        return deteccionRepository.save(deteccion);
    }
    
    /**
     * Obtiene todas las detecciones ordenadas por timestamp
     */
    public List<Deteccion> obtenerTodasLasDetecciones() {
        return deteccionRepository.findAllOrderByTimestamp();
    }
    
    /**
     * Obtiene las últimas N detecciones
     */
    public List<Deteccion> obtenerUltimasDetecciones(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor a 0");
        }
        if (limite > 1000) {
            throw new IllegalArgumentException("El límite máximo es 1000 detecciones");
        }
        
        return deteccionRepository.findLastN(limite);
    }
    
    /**
     * Obtiene estadísticas básicas
     */
    public DeteccionStats obtenerEstadisticas() {
        long total = deteccionRepository.count();
        List<Deteccion> ultimasCinco = deteccionRepository.findLastN(5);
        
        DeteccionStats stats = new DeteccionStats();
        stats.setTotalDetecciones(total);
        stats.setUltimasDetecciones(ultimasCinco);
        
        return stats;
    }
    
    /**
     * Limpia todas las detecciones
     */
    public void limpiarTodasLasDetecciones() {
        deteccionRepository.deleteAll();
    }
    
    /**
     * Valida una detección antes de guardarla
     */
    private void validarDeteccion(Deteccion deteccion) {
        if (deteccion == null) {
            throw new IllegalArgumentException("La detección no puede ser null");
        }
        
        if (deteccion.getTimestamp() == null) {
            throw new IllegalArgumentException("El timestamp es obligatorio");
        }
        
        if (deteccion.getPersonas() == null) {
            throw new IllegalArgumentException("El número de personas es obligatorio");
        }
        
        if (deteccion.getPersonas() < 0) {
            throw new IllegalArgumentException("El número de personas no puede ser negativo");
        }
        
        if (deteccion.getCoordenadas() == null) {
            throw new IllegalArgumentException("Las coordenadas son obligatorias");
        }
        
        // Validar que el número de coordenadas coincida con el número de personas
        if (deteccion.getCoordenadas().size() != deteccion.getPersonas()) {
            throw new IllegalArgumentException(String.format(
                "El número de coordenadas (%d) debe coincidir con el número de personas (%d)",
                deteccion.getCoordenadas().size(), deteccion.getPersonas()));
        }
        
        // Validar que las coordenadas sean válidas
        for (int i = 0; i < deteccion.getCoordenadas().size(); i++) {
            Deteccion.Coordenada coord = deteccion.getCoordenadas().get(i);
            if (coord == null) {
                throw new IllegalArgumentException("Las coordenadas no pueden ser null");
            }
            if (coord.getX() == null || coord.getY() == null) {
                throw new IllegalArgumentException(String.format(
                    "Las coordenadas en la posición %d deben tener valores X e Y válidos", i));
            }
            if (coord.getX() < 0 || coord.getY() < 0) {
                throw new IllegalArgumentException(String.format(
                    "Las coordenadas en la posición %d no pueden ser negativas", i));
            }
        }
    }
    
    /**
     * Obtiene todas las horas únicas disponibles en las detecciones
     */
    public List<String> obtenerHorasDisponibles() {
        List<Deteccion> todasLasDetecciones = deteccionRepository.findAllOrderByTimestamp();
        
        // Extraer las horas únicas y ordenarlas
        Set<String> horasUnicas = todasLasDetecciones.stream()
            .map(deteccion -> deteccion.getTimestamp().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00")))
            .collect(Collectors.toSet());
        
        // Convertir a lista y ordenar
        List<String> horasOrdenadas = new ArrayList<>(horasUnicas);
        horasOrdenadas.sort(String::compareTo);
        
        return horasOrdenadas;
    }
    
    /**
     * Obtiene solo los timestamps de todas las detecciones
     */
    public List<String> obtenerTodosLosTimestamps() {
        List<Deteccion> todasLasDetecciones = deteccionRepository.findAllOrderByTimestamp();
        
        // Extraer solo los timestamps y formatearlos
        return todasLasDetecciones.stream()
            .map(deteccion -> deteccion.getTimestamp().format(TIMESTAMP_FORMATTER))
            .collect(Collectors.toList());
    }

    /**
     * Aplica clustering DBSCAN a todas las detecciones y retorna DTOs enriquecidos
     */
    public List<DeteccionDTO> aplicarClusteringDBSCAN(Double eps, Integer minSamples) {
        try {
            // Obtener todas las detecciones
            List<Deteccion> detecciones = obtenerTodasLasDetecciones();
            
            if (detecciones.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Ejecutar DBSCAN
            Map<String, Object> resultadoClustering = ejecutarDBSCAN(detecciones, eps, minSamples);
            
            // Convertir a DTOs con información de clustering
            return convertirADTOsConClustering(detecciones, resultadoClustering);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al aplicar clustering DBSCAN: " + e.getMessage(), e);
        }
    }
    
    /**
     * Aplica clustering DBSCAN con parámetros por defecto
     */
    public List<DeteccionDTO> aplicarClusteringDBSCAN() {
        return aplicarClusteringDBSCAN(0.5, 3);
    }
    
    /**
     * Aplica clustering DBSCAN a detecciones en un rango de tiempo específico
     */
    public List<DeteccionDTO> aplicarClusteringDBSCANRestringido(Double eps, Integer minSamples, 
                                                                String timestampInicio, String timestampFin) {
        try {
            // Obtener detecciones en el rango de tiempo
            List<Deteccion> detecciones = obtenerDeteccionesPorRango(timestampInicio + "," + timestampFin);
            
            if (detecciones.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Ejecutar DBSCAN
            Map<String, Object> resultadoClustering = ejecutarDBSCAN(detecciones, eps, minSamples);
            
            // Convertir a DTOs con información de clustering
            return convertirADTOsConClustering(detecciones, resultadoClustering);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al aplicar clustering DBSCAN restringido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ejecuta el script de DBSCAN y retorna los resultados
     */
    private Map<String, Object> ejecutarDBSCAN(List<Deteccion> detecciones, Double eps, Integer minSamples) 
            throws IOException, InterruptedException {
        
        // Preparar datos de entrada para el script Python
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("detecciones", detecciones);
        inputData.put("eps", eps != null ? eps : 0.5);
        inputData.put("minSamples", minSamples != null ? minSamples : 3);
        
        String inputJson = objectMapper.writeValueAsString(inputData);
        
        // Ruta al script Python
        String scriptPath = Paths.get("src", "main", "resources", "scripts", "dbscan_simple.py")
                                .toAbsolutePath().toString();
        
        // Ejecutar script Python
        ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
        Process process = pb.start();
        
        // Enviar datos al script
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
            writer.write(inputJson);
            writer.flush();
        }
        
        // Leer resultado
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        
        // Leer errores si los hay
        StringBuilder errors = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                errors.append(line).append("\n");
            }
        }
        
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            String errorOutput = errors.toString();
            String stdOutput = result.toString();
            throw new RuntimeException(String.format(
                "Error ejecutando DBSCAN (exit code: %d). Stderr: %s. Stdout: %s", 
                exitCode, errorOutput, stdOutput));
        }
        
        // Parsear resultado JSON
        JsonNode resultNode = objectMapper.readTree(result.toString());
        
        if (resultNode.has("error") && resultNode.get("error").asBoolean()) {
            throw new RuntimeException("Error en DBSCAN: " + resultNode.get("mensaje").asText());
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = objectMapper.convertValue(resultNode, Map.class);
        return resultMap;
    }
    
    /**
     * Convierte detecciones a DTOs enriquecidos con información de clustering
     */
    private List<DeteccionDTO> convertirADTOsConClustering(List<Deteccion> detecciones, 
                                                          Map<String, Object> resultadoClustering) {
        
        List<DeteccionDTO> dtos = new ArrayList<>();
        
        // Crear mapa de resultados por detección ID
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultados = (List<Map<String, Object>>) 
            resultadoClustering.get("resultados");
        
        Map<String, List<Map<String, Object>>> resultadosPorDeteccion = new HashMap<>();
        for (Map<String, Object> resultado : resultados) {
            String deteccionId = (String) resultado.get("deteccionId");
            resultadosPorDeteccion.computeIfAbsent(deteccionId, k -> new ArrayList<>()).add(resultado);
        }
        
        // Procesar cada detección
        for (Deteccion deteccion : detecciones) {
            List<Map<String, Object>> resultadosDeteccion = 
                resultadosPorDeteccion.get(deteccion.getId());
            
            if (resultadosDeteccion != null && !resultadosDeteccion.isEmpty()) {
                // Si hay múltiples coordenadas, crear un DTO que represente el conjunto
                Map<String, Object> primerResultado = resultadosDeteccion.get(0);
                
                DeteccionDTO dto = new DeteccionDTO(deteccion);
                
                // Determinar el cluster predominante o si es ruido
                long clustersCount = resultadosDeteccion.stream()
                    .filter(r -> !(Boolean) r.get("esRuido"))
                    .count();
                
                if (clustersCount > 0) {
                    // Tomar el primer resultado no-ruido para la información del cluster
                    Map<String, Object> clusterInfo = resultadosDeteccion.stream()
                        .filter(r -> !(Boolean) r.get("esRuido"))
                        .findFirst()
                        .orElse(primerResultado);
                    
                    dto.setClusterId((Integer) clusterInfo.get("clusterId"));
                    dto.setClusterTipo("CLUSTER");
                    dto.setPuntosEnCluster((Integer) clusterInfo.get("puntosEnCluster"));
                    dto.setDensidadCluster((Double) clusterInfo.get("densidadCluster"));
                    
                    Double centroideX = (Double) clusterInfo.get("centroideX");
                    Double centroideY = (Double) clusterInfo.get("centroideY");
                    if (centroideX != null && centroideY != null) {
                        dto.setCentroideCluster(new DeteccionDTO.Coordenada(centroideX, centroideY));
                    }
                } else {
                    // Todos los puntos son ruido
                    dto.setClusterId(-1);
                    dto.setClusterTipo("RUIDO");
                    dto.setPuntosEnCluster(0);
                    dto.setDensidadCluster(0.0);
                    dto.setCentroideCluster(null);
                }
                
                dtos.add(dto);
            } else {
                // Sin información de clustering, agregar DTO básico
                DeteccionDTO dto = new DeteccionDTO(deteccion);
                dtos.add(dto);
            }
        }
        
        return dtos;
    }

    /**
     * Obtiene análisis detallado de clustering incluyendo diagnósticos
     */
    public Map<String, Object> obtenerAnalisisClusteringDetallado(Double eps, Integer minSamples) {
        try {
            // Obtener todas las detecciones
            List<Deteccion> detecciones = obtenerTodasLasDetecciones();
            
            if (detecciones.isEmpty()) {
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("mensaje", "No hay detecciones disponibles para clustering");
                resultado.put("clusters", new ArrayList<>());
                resultado.put("estadisticas", new HashMap<>());
                return resultado;
            }
            
            // Ejecutar DBSCAN con diagnósticos
            Map<String, Object> resultadoClustering = ejecutarDBSCAN(detecciones, eps, minSamples);
            
            // Agregar información adicional
            resultadoClustering.put("totalDetecciones", detecciones.size());
            resultadoClustering.put("parametrosUsados", Map.of(
                "eps", eps != null ? eps : 0.5,
                "minSamples", minSamples != null ? minSamples : 3
            ));
            
            return resultadoClustering;
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("mensaje", "Error en análisis de clustering: " + e.getMessage());
            error.put("clusters", new ArrayList<>());
            error.put("estadisticas", new HashMap<>());
            return error;
        }
    }

    // Clase para estadísticas
    public static class DeteccionStats {
        private long totalDetecciones;
        private List<Deteccion> ultimasDetecciones;
        
        public long getTotalDetecciones() {
            return totalDetecciones;
        }
        
        public void setTotalDetecciones(long totalDetecciones) {
            this.totalDetecciones = totalDetecciones;
        }
        
        public List<Deteccion> getUltimasDetecciones() {
            return ultimasDetecciones;
        }
        
        public void setUltimasDetecciones(List<Deteccion> ultimasDetecciones) {
            this.ultimasDetecciones = ultimasDetecciones;
        }
    }
}
