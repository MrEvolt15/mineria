# 🎯 Visualización K-means en Tiempo Real

Esta visualización HTML proporciona una interfaz interactiva para monitorear y analizar el sistema de clustering K-means en tiempo real.

## 🚀 Características

### 🎨 Visualización Interactiva
- **Mapa de calor dinámico** con detecciones de personas en tiempo real
- **Centroides K-means** con áreas de influencia visualizadas
- **Animaciones suaves** con efectos de fade para detecciones
- **Conexiones visuales** entre detecciones y centroides más cercanos
- **Grid de referencia** para mejor orientación espacial

### 📊 Panel de Control
- **Botones de control**: Iniciar/detener simulación, actualizar datos, limpiar cache
- **Estadísticas en tiempo real**: Contador de detecciones y centroides
- **Lista de centroides activos** con coordenadas y estadísticas
- **Control de parámetro K** para ajustar el número de clusters

### 📈 Monitoreo en Tiempo Real
- **Log de actividad** con timestamps y códigos de color
- **Indicadores de estado** del sistema y conexiones API
- **Información de tooltips** al pasar el mouse sobre centroides
- **Detecciones manuales** haciendo clic en el canvas

## 🛠️ Uso

### Inicio Rápido
1. **Ejecutar el servidor Spring Boot**:
   ```bash
   cd "d:\Mineria\mineria back\mineria"
   call run.bat
   ```

2. **Abrir la visualización**:
   ```bash
   call open_visualization.bat
   ```
   O usando el menú principal:
   ```bash
   call menu.bat
   # Seleccionar opción 5: "Abrir visualización K-means HTML"
   ```

3. **Iniciar simulación** haciendo clic en "▶️ Iniciar Simulación"

### Controles Disponibles

| Botón | Función |
|-------|---------|
| ▶️ **Iniciar Simulación** | Comienza la generación automática de datos |
| ⏹️ **Detener** | Detiene la simulación en curso |
| 🔄 **Actualizar Centroides** | Recarga los centroides desde el servidor |
| 🗑️ **Limpiar Datos** | Limpia cache y reinicia el sistema |

### Interacciones del Canvas
- **Clic izquierdo**: Agregar detección manual en la posición clickeada
- **Mouse hover**: Ver información detallada de centroides
- **Zoom visual**: Los elementos se adaptan al tamaño de la ventana

## 🎨 Elementos Visuales

### Detecciones de Personas
- **Círculos azules**: Detecciones recientes
- **Fade gradual**: Las detecciones se desvanecen con el tiempo
- **Colores dinámicos**: Diferentes colores para mejor diferenciación

### Centroides K-means
- **Círculos grandes con bordes**: Posición del centroide
- **Áreas de influencia**: Círculos semi-transparentes alrededor
- **Etiquetas**: Identificador y coordenadas
- **Estadísticas**: Número de detecciones asociadas

### Conexiones
- **Líneas punteadas**: Conectan detecciones con centroide más cercano
- **Transparencia temporal**: Se desvanecen gradualmente

## ⚙️ Configuración

### Parámetros Ajustables
- **Número de clusters (K)**: 2-10 clusters
- **Intervalo de actualización**: 2 segundos por defecto
- **Tiempo de fade**: 10 segundos para detecciones

### Configuración del API
```javascript
const CONFIG = {
    API_BASE: 'http://localhost:8080/api/realtime',
    UPDATE_INTERVAL: 2000, // ms
    DETECTION_FADE_TIME: 10000, // ms
    COLORS: ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', ...]
};
```

## 🔧 Endpoints Utilizados

La visualización consume los siguientes endpoints del API:

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/realtime/deteccion` | POST | Envía detección individual |
| `/api/realtime/detecciones/lote` | POST | Envía múltiples detecciones |
| `/api/realtime/heatmap-kmeans?k={k}` | GET | Obtiene centroides K-means |
| `/api/realtime/limpiar-cache-kmeans` | POST | Limpia cache del sistema |

## 📱 Responsive Design

La visualización se adapta automáticamente a diferentes tamaños de pantalla:
- **Desktop**: Layout de 2 columnas con canvas grande
- **Tablet/Mobile**: Layout de 1 columna con elementos apilados
- **Controles adaptativos**: Botones y texto se ajustan al dispositivo

## 🐛 Resolución de Problemas

### Problemas Comunes

**La visualización no se conecta al servidor**
- ✅ Verificar que el servidor Spring Boot esté ejecutándose en puerto 8080
- ✅ Comprobar que no hay firewall bloqueando la conexión
- ✅ Revisar la consola del navegador para errores CORS

**No aparecen centroides**
- ✅ Enviar algunas detecciones primero (mínimo 3-5)
- ✅ Ajustar el parámetro K a un valor apropiado
- ✅ Verificar que el endpoint `/heatmap-kmeans` responde correctamente

**Performance lenta**
- ✅ Reducir el número de detecciones simultáneas
- ✅ Aumentar el intervalo de actualización
- ✅ Cerrar otras pestañas del navegador

### Logs y Debugging

La visualización incluye un sistema de logs detallado:
- **Verde**: Operaciones exitosas
- **Azul**: Información general
- **Rojo**: Errores y problemas

## 🚀 Funcionalidades Avanzadas

### Simulación de Zonas Calientes
- 70% de detecciones se generan cerca de centroides existentes
- 30% se distribuyen aleatoriamente
- Simula patrones realistas de movimiento de personas

### Algoritmo de Clustering Visual
- Actualización incremental de centroides
- Visualización de convergencia del algoritmo
- Métricas de calidad del clustering

### Análisis en Tiempo Real
- Estadísticas de throughput
- Distribución espacial de detecciones
- Evolución temporal de los clusters

## 📝 Extensiones Futuras

### Posibles Mejoras
- 📊 **Gráficos adicionales**: Histogramas de distribución, timeline de actividad
- 🎮 **Controles avanzados**: Velocidad de simulación, filtros por cámara
- 📱 **Notificaciones**: Alertas para eventos importantes
- 💾 **Exportación**: Guardar datos y configuraciones
- 🎨 **Temas**: Modo oscuro, colores personalizables

### Integración con Sistemas
- 📡 **WebSockets**: Conexión en tiempo real sin polling
- 🔐 **Autenticación**: Login y roles de usuario
- 📈 **Analytics**: Métricas avanzadas y reportes
- 🌐 **Multi-cámara**: Visualización de múltiples ubicaciones

## 📄 Archivos Relacionados

- `kmeans_visualization.html` - Archivo principal de visualización
- `open_visualization.bat` - Script para abrir la visualización
- `menu.bat` - Menú principal (opción 5)
- `test_kmeans.py` - Script de generación de datos de prueba
- `README.md` - Documentación principal del proyecto
- `API_ENDPOINTS.md` - Documentación completa del API
