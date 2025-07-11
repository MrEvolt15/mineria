# 🚀 API Minería - Detección de Personas + DBSCAN Clustering

## 📋 Resumen

API Spring Boot para gestionar detecciones de personas con **clustering DBSCAN** en tiempo real para análisis y visualización de datos.

## 🧠 Funcionalidades Principales

### ✨ Detección y Análisis:
- **Gestión de detecciones** con coordenadas y timestamps
- **Clustering DBSCAN** automático para agrupación inteligente
- **Análisis detallado** con diagnósticos y recomendaciones
- **Visualización web** interactiva con mapas de calor

## 🛠️ Configuración del Sistema

### Prerequisitos:
- ✅ Java 17+
- ✅ Maven 3.9+
- ✅ Python con scikit-learn y numpy
- ✅ MongoDB (opcional para persistencia)

## 🚀 Inicio Rápido

### Compilar y Ejecutar:
```cmd
# Compilar
mvn clean package

# Ejecutar
java -jar target/mineria-0.0.1-SNAPSHOT.jar

# O directamente
mvn spring-boot:run
```

### Visualización:
```cmd
# Abrir en navegador
1visualizacion.html          # Visualización DBSCAN interactiva
detecciones_heatmap.html     # Mapa de calor básico
```

## 🌐 Endpoints API

### � Gestión de Detecciones
- **GET** `/api/detecciones?segundo=2025-07-03T11:10:01` - Detecciones por segundo específico
- **GET** `/api/detecciones?rango=inicio,fin` - Detecciones en rango de tiempo
- **POST** `/api/detecciones` - Crear nueva detección
- **GET** `/api/detecciones/todas` - Todas las detecciones
- **GET** `/api/detecciones/ultimas?limite=10` - Últimas N detecciones
- **GET** `/api/detecciones/estadisticas` - Estadísticas generales
- **GET** `/api/detecciones/timestamps` - Lista de timestamps disponibles

### 🧠 Clustering DBSCAN
- **GET** `/api/detecciones/clustering?eps=0.5&minSamples=3` - Aplicar clustering DBSCAN
- **GET** `/api/detecciones/clustering/restringido` - Clustering por rango de tiempo
- **GET** `/api/detecciones/clustering/analisis` - Análisis detallado con diagnósticos

### 🎯 Utilidades
- **GET** `/api/detecciones/ejemplo` - Generar datos de ejemplo
- **GET** `/api/detecciones/horas-disponibles` - Horas con datos disponibles

## 📝 Formato de Datos

### Detección:
```json
{
  "timestamp": "2025-07-03T11:10:01",
  "personas": 2,
  "coordenadas": [
    {"x": 450.2, "y": 800.1},
    {"x": 470.0, "y": 790.5}
  ]
}
```

### Respuesta Clustering:
```json
{
  "detecciones": [...],
  "estadisticas": {
    "totalClusters": 3,
    "puntosRuido": 5,
    "puntosEnClusters": 25
  },
  "parametros": {"eps": 0.5, "minSamples": 3}
}
```

## 🎨 Visualización de Datos

### � 1visualizacion.html
**Interfaz interactiva completa para análisis DBSCAN:**
- 🎯 Canvas interactivo con clustering en tiempo real
- 📈 Panel de estadísticas y métricas
- ⚙️ Controles para ajustar parámetros EPS y Min Samples
- 📊 Análisis detallado con diagnósticos y recomendaciones
- 📝 Log de actividad en tiempo real
- 🖱️ Visualización de clusters y puntos de ruido

**Características:**
- Actualización automática cada 3 segundos
- Análisis por rangos de tiempo
- Recomendaciones inteligentes para optimizar parámetros
- Información detallada de normalización y distribución

### � detecciones_heatmap.html
**Mapa de calor básico:**
- 🗺️ Visualización simple de densidad de detecciones
- 🎨 Gradiente de colores por intensidad
- 📍 Puntos de detección superpuestos

## 🧪 Ejemplos de Uso

### Obtener clustering:
```bash
curl "http://localhost:8080/api/detecciones/clustering?eps=0.3&minSamples=5"
```

### Análisis detallado:
```bash
curl "http://localhost:8080/api/detecciones/clustering/analisis?eps=0.5&minSamples=3"
```

### Generar datos de prueba:
```bash
curl "http://localhost:8080/api/detecciones/ejemplo"
```

---

## 📞 Soporte

**🎉 API simplificada para detección de personas con clustering DBSCAN!**

### ✅ Funcionalidades:
- 🔍 Gestión completa de detecciones
- 🧠 Clustering DBSCAN con análisis detallado
- 📊 Dos interfaces de visualización
- 🎯 Diagnósticos y recomendaciones automáticas
