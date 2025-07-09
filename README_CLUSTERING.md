# Clustering DBSCAN - Documentación

## Descripción

El endpoint de clustering DBSCAN permite analizar las detecciones de personas para identificar grupos y patrones espaciales usando el algoritmo DBSCAN (Density-Based Spatial Clustering of Applications with Noise).

## Endpoint

```
GET /api/detecciones/clustering
```

## Parámetros

| Parámetro | Tipo | Requerido | Valor por defecto | Descripción |
|-----------|------|-----------|-------------------|-------------|
| `eps` | Double | No | 0.5 | Radio de vecindad para DBSCAN. Determina la distancia máxima entre puntos para ser considerados vecinos |
| `minSamples` | Integer | No | 3 | Número mínimo de puntos requeridos para formar un cluster |

## Ejemplos de uso

### Clustering con parámetros por defecto
```bash
curl "http://localhost:8080/api/detecciones/clustering"
```

### Clustering con parámetros personalizados
```bash
curl "http://localhost:8080/api/detecciones/clustering?eps=0.3&minSamples=5"
```

## Respuesta

### Estructura de respuesta exitosa

```json
{
  "detecciones": [
    {
      "id": "66c8a7c123456789abcdef01",
      "timestamp": "2025-07-03T11:10:01",
      "personas": 2,
      "coordenadas": [
        {"x": 450.2, "y": 800.1},
        {"x": 470.0, "y": 790.5}
      ],
      "clusterId": 0,
      "clusterTipo": "CLUSTER",
      "densidadCluster": 0.85,
      "puntosEnCluster": 12,
      "centroideCluster": {"x": 460.5, "y": 795.3}
    },
    {
      "id": "66c8a7c123456789abcdef02",
      "timestamp": "2025-07-03T11:10:05",
      "personas": 1,
      "coordenadas": [
        {"x": 900.0, "y": 100.0}
      ],
      "clusterId": -1,
      "clusterTipo": "RUIDO",
      "densidadCluster": 0.0,
      "puntosEnCluster": 0,
      "centroideCluster": null
    }
  ],
  "total": 2,
  "parametros": {
    "eps": 0.5,
    "minSamples": 3
  },
  "estadisticas": {
    "totalClusters": 1,
    "puntosRuido": 1,
    "puntosEnClusters": 1
  },
  "success": true
}
```

### Campos del DTO DeteccionDTO

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | String | ID único de la detección |
| `timestamp` | String | Momento de la detección (formato ISO) |
| `personas` | Integer | Número de personas detectadas |
| `coordenadas` | Array | Lista de coordenadas (x, y) de las personas |
| `clusterId` | Integer | ID del cluster (≥0 para clusters válidos, -1 para ruido, null si no procesado) |
| `clusterTipo` | String | Tipo de clasificación: "CLUSTER", "RUIDO", o "SIN_PROCESAR" |
| `densidadCluster` | Double | Densidad del cluster al que pertenece (puntos por unidad de área) |
| `puntosEnCluster` | Integer | Número total de puntos en el mismo cluster |
| `centroideCluster` | Object | Coordenadas del centroide del cluster ({x, y}) o null para ruido |

### Estadísticas de clustering

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `totalClusters` | Long | Número total de clusters encontrados |
| `puntosRuido` | Long | Número de puntos clasificados como ruido |
| `puntosEnClusters` | Long | Número de puntos que pertenecen a algún cluster |

## Códigos de respuesta

- **200 OK**: Clustering aplicado exitosamente
- **400 Bad Request**: Parámetros inválidos (eps ≤ 0 o minSamples ≤ 0)
- **500 Internal Server Error**: Error interno del servidor

## Algoritmo DBSCAN

### Conceptos clave

1. **Eps (ε)**: Radio de vecindad. Puntos dentro de esta distancia son considerados vecinos.
2. **MinSamples**: Número mínimo de puntos en un radio ε para formar un cluster.
3. **Core Point**: Punto que tiene al menos MinSamples vecinos dentro del radio ε.
4. **Border Point**: Punto no-core que está dentro del radio ε de un core point.
5. **Noise Point**: Punto que no es core ni border.

### Interpretación de resultados

- **Clusters (clusterId ≥ 0)**: Grupos de puntos densamente conectados que representan áreas de alta actividad.
- **Ruido (clusterId = -1)**: Puntos aislados que no pertenecen a ningún cluster, posibles outliers.
- **Densidad**: Valores más altos indican clusters más compactos.

### Recomendaciones de parámetros

| Escenario | eps | minSamples | Descripción |
|-----------|-----|------------|-------------|
| Detección fina | 0.3 | 2 | Detecta clusters pequeños y localizados |
| Detección estándar | 0.5 | 3 | Balance entre sensibilidad y robustez |
| Detección gruesa | 0.8 | 5 | Solo clusters grandes y bien definidos |

## Casos de uso

1. **Análisis de patrones de movimiento**: Identificar áreas de concentración de personas.
2. **Detección de anomalías**: Puntos de ruido pueden indicar comportamientos inusuales.
3. **Optimización de espacios**: Identificar zonas de alta actividad para mejorar la distribución.
4. **Análisis temporal**: Comparar clusters en diferentes períodos de tiempo.

## Script de prueba

Utilizar el script `test_clustering.py` para probar la funcionalidad:

```bash
python test_clustering.py
```

Este script:
1. Genera datos de prueba con clusters conocidos
2. Limpia datos existentes
3. Sube los datos de prueba
4. Ejecuta clustering con diferentes parámetros
5. Muestra estadísticas y ejemplos de resultados
