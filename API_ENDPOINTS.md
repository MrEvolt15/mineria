# API de Minería - Documentación de Endpoints

## 🎯 Visualización Web Interactiva

### Acceso a la Visualización
```
🌐 Archivo: kmeans_visualization.html
🚀 Script: open_visualization.bat
📍 Menú: menu.bat → opción 5
```

**URL Local**: `file:///[ruta-al-proyecto]/kmeans_visualization.html`

### Funcionalidades de la Visualización
- **Mapa interactivo en tiempo real** con detecciones y centroides K-means
- **Simulación automática** de datos con zonas calientes dinámicas
- **Panel de control** para iniciar/detener, ajustar parámetros
- **Estadísticas en vivo** de detecciones y clustering
- **Detecciones manuales** haciendo clic en el canvas
- **Log de actividad** con códigos de color para eventos

### Endpoints Utilizados por la Visualización
La interfaz web consume automáticamente estos endpoints:

| Endpoint | Uso en Visualización |
|----------|---------------------|
| `POST /api/realtime/detecciones/lote` | Envío de detecciones simuladas |
| `GET /api/realtime/heatmap-kmeans?k={k}` | Obtención de centroides |
| `POST /api/realtime/limpiar-cache-kmeans` | Reinicio del sistema |

---

## Endpoints Públicos (Accesibles desde Internet)

### 1. Recibir datos de cámaras
```
POST /api/realtime/camera-data
```
**Uso**: Las cámaras envían datos de detección aquí. **Siempre crea una persona nueva** con los datos proporcionados o valores por defecto.

**Body ejemplo completo**:
```json
{
  "ncam": 1,
  "time": "2025-07-02T10:30:00",
  "posX": 123.45,
  "posY": 67.89,
  "tipoPersona": "E",
  "genero": "M",
  "id": 95
}
```

**Body ejemplo mínimo** (solo datos requeridos):
```json
{
  "ncam": 1,
  "time": "2025-07-02T10:30:00",
  "posX": 123.45,
  "posY": 67.89,
  "id": 123
}
```
*Nota: Si faltan datos de persona, se creará una persona genérica con tipo "E" y género "No especificado"*

### 2. Recibir múltiples datos
```
POST /api/realtime/camera-data/batch
```
**Uso**: Para enviar múltiples detecciones de una vez. **Cada registro crea una persona nueva**.

**Body ejemplo**:
```json
[
  {
    "ncam": 1,
    "time": "2025-07-02T10:30:00",
    "posX": 123.45,
    "posY": 67.89,
    "tipoPersona": "E",
    "genero": "M",
    "id": 95
  },
  {
    "ncam": 2,
    "time": "2025-07-02T10:31:00",
    "posX": 200.10,
    "posY": 150.20,
    "id": 88
  }
]
```
*Nota: El segundo registro creará una persona genérica por falta de datos específicos*

### 3. Obtener datos para mapa de calor
```
GET /api/realtime/heatmap-data
GET /api/realtime/heatmap-data?inicio=2025-07-02T00:00:00&fin=2025-07-02T23:59:59
```
**Uso**: Otros sistemas obtienen datos para generar mapas de calor.

### 4. Verificar estado del sistema
```
GET /api/realtime/stats
```
**Uso**: Verificar que el sistema esté funcionando.

---

## Endpoints de Administración (Solo acceso local)

### Consultar personas
```
GET /api/admin/personas           # Ver todas las personas
GET /api/admin/personas/{id}      # Ver persona específica
```

### Consultar cámaras
```
GET /api/admin/camaras            # Ver todas las cámaras
GET /api/admin/camaras/{id}       # Ver cámara específica
GET /api/admin/camaras/numero/{ncam}  # Ver cámara por número
```

---

## Configuración para Producción

### Usar ngrok para exponer endpoints públicos:
```bash
# Instalar ngrok desde https://ngrok.com/download
# Ejecutar tu aplicación Spring Boot
mvn spring-boot:run

# En otra terminal, exponer puerto 8080
ngrok http 8080
```

### URLs resultantes:
- **Público**: `https://tu-url-ngrok.ngrok.io/api/realtime/*`
- **Admin**: `http://localhost:8080/api/admin/*`

---

## Descripción de Campos

| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| `ncam` | Integer | ✅ | Número identificador de la cámara |
| `time` | String (ISO DateTime) | ✅ | Timestamp de la detección |
| `posX` | Number (Decimal) | ✅ | Coordenada X de la cámara |
| `posY` | Number (Decimal) | ✅ | Coordenada Y de la cámara |
| `id` | Integer | ✅ | ID único de la detección |
| `tipoPersona` | String | ❌ | Tipo de persona: "E", "PR", "PA" (por defecto: "E") |
| `genero` | String | ❌ | Género de la persona (por defecto: "No especificado") |

## Comportamiento del Sistema

### ✅ **Siempre se crea una persona nueva**
- **Con datos completos**: Usa los valores proporcionados
- **Sin datos de persona**: Crea persona genérica con valores por defecto:
  - `tipoPersona`: "E" (Empleado)
  - `genero`: "No especificado"  

### ✅ **Siempre se registra la detección**
- Cada llamada al endpoint crea una relación persona-cámara
- El campo `id` se convierte automáticamente a valor de confianza (id/100)
- Útil para tracking y análisis de patrones

## Tipos de Persona
- **E**: Empleado (valor por defecto)
- **PR**: Proveedor  
- **PA**: Paciente

## Campo ID
- **Requerido**: Debe estar presente en cada request
- **Conversión automática**: Se convierte a confianza dividiendo por 100
- **Ejemplo**: id=95 → confianza=0.95, id=50 → confianza=0.50

## 🧠 Endpoints de Minería de Datos (K-means)

### 📊 Mapa de Calor con Centroides K-means
- **GET** `/api/realtime/heatmap-kmeans` - Aplica K-means en tiempo real y devuelve detecciones + centroides

**Parámetros:**
- `inicio` (opcional): Fecha inicio en formato ISO (ej: 2025-07-03T10:00:00)
- `fin` (opcional): Fecha fin en formato ISO  
- `k` (opcional): Número de clusters (default: 5)

**Ejemplo:**
```
GET /api/realtime/heatmap-kmeans?k=3&inicio=2025-07-03T10:00:00&fin=2025-07-03T11:00:00
```

**Respuesta:**
```json
{
  "detecciones": [],
  "centroides": [
    {
      "posX": 5.2,
      "posY": -3.1,
      "densidad": 15,
      "intensidad": 1.0,
      "clusterId": 0,
      "ultimaActualizacion": "2025-07-03T10:30:00",
      "radio": 2.5
    }
  ],
  "totalDetecciones": 45,
  "numeroClusters": 3,
  "fechaCalculo": "2025-07-03T10:30:00",
  "convergencia": 0.05
}
```

### 🎯 Centroides K-means
- **GET** `/api/realtime/centroides` - Obtiene solo los centroides (desde cache o calculados)

**Parámetros:** Mismos que heatmap-kmeans

**Respuesta:**
```json
[
  {
    "posX": 5.2,
    "posY": -3.1,
    "densidad": 15,
    "intensidad": 1.0,
    "clusterId": 0,
    "ultimaActualizacion": "2025-07-03T10:30:00",
    "radio": 2.5
  }
]
```

### 🧹 Gestión de Cache
- **POST** `/api/realtime/limpiar-cache-kmeans` - Limpia el cache de centroides

## 🔄 Funcionamiento del K-means en Tiempo Real

### ⚡ Actualización Incremental:
1. **Cada nueva detección** actualiza automáticamente los centroides más cercanos
2. **Density-based coloring**: La intensidad del color cambia según la densidad de detecciones
3. **Cache inteligente**: Los centroides se mantienen en memoria para respuesta rápida
4. **Learning rate**: Ajuste gradual de posiciones para estabilidad

### 🎨 Visualización:
- **posX, posY**: Coordenadas del centroide en el mapa
- **densidad**: Número de detecciones asignadas al cluster
- **intensidad**: Valor 0-1 para intensidad de color (densidad/10)
- **radio**: Radio de influencia del centroide

### 📈 Algoritmo:
1. **Inicialización**: Centroides aleatorios en el rango de datos
2. **Asignación**: Cada punto se asigna al centroide más cercano
3. **Actualización**: Centroides se mueven al promedio de sus puntos
4. **Convergencia**: Se detiene cuando centroides se estabilizan
5. **Incremental**: Nuevas detecciones ajustan centroides existentes

## 🌐 Integración con Frontend

### JavaScript Example:
```javascript
// Obtener centroides en tiempo real
async function obtenerCentroides() {
    const response = await fetch('/api/realtime/centroides?k=5');
    const centroides = await response.json();
    
    // Actualizar mapa de calor
    centroides.forEach(centroide => {
        actualizarMapa(
            centroide.posX, 
            centroide.posY, 
            centroide.intensidad,
            centroide.radio
        );
    });
}

// Llamar cada 5 segundos para tiempo real
setInterval(obtenerCentroides, 5000);
```

### CSS para Intensidad:
```css
.centroide {
    opacity: var(--intensidad); /* 0-1 del centroide */
    background: radial-gradient(
        circle, 
        rgba(255,0,0,var(--intensidad)) 0%, 
        transparent 70%
    );
}
```
