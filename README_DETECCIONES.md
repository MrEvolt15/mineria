# Módulo de Detecciones - API Backend

Este módulo maneja las detecciones de personas con coordenadas en tiempo real, implementando un nuevo formato de datos específicamente diseñado para almacenar y consultar información de detección.

## 🔧 Componentes del Módulo

### 1. Modelo de Datos (`Deteccion.java`)
Representa una detección con el siguiente formato:
```json
{
  "timestamp": "2025-07-03T14:18:00",
  "personas": 5,
  "coordenadas": [
    {"x": 450.2, "y": 800.1},
    {"x": 470.0, "y": 790.5}
  ]
}
```

### 2. Capa de Datos
- **`DeteccionRepository.java`**: Interfaz del repositorio con métodos básicos
- **`DeteccionRepositoryImpl.java`**: Implementación personalizada con MongoDB

### 3. Capa de Servicio
- **`DeteccionService.java`**: Lógica de negocio, validaciones y operaciones

### 4. Capa de Controlador
- **`DeteccionController.java`**: Endpoints REST para interactuar con las detecciones

## 📡 API Endpoints

### POST `/api/detecciones`
Crea una nueva detección.

**Request Body:**
```json
{
  "timestamp": "2025-07-03T14:18:00",
  "personas": 2,
  "coordenadas": [
    {"x": 450.2, "y": 800.1},
    {"x": 470.0, "y": 790.5}
  ]
}
```

**Response (201):**
```json
{
  "deteccion": { /* objeto detección completo con ID */ },
  "success": true,
  "mensaje": "Detección guardada exitosamente"
}
```

### GET `/api/detecciones?segundo=YYYY-MM-DDTHH:mm:ss`
Obtiene todas las detecciones que ocurrieron en un segundo específico.

**Ejemplo:**
```
GET /api/detecciones?segundo=2025-07-03T11:10:01
```

**Response (200):**
```json
{
  "detecciones": [ /* array de detecciones */ ],
  "total_encontradas": 3,
  "tipo_consulta": "segundo",
  "parametro": "2025-07-03T11:10:01",
  "success": true
}
```

### GET `/api/detecciones?rango=inicio,fin`
Obtiene todas las detecciones en un rango de tiempo.

**Ejemplo:**
```
GET /api/detecciones?rango=2025-07-03T11:00:00,2025-07-03T11:10:00
```

**Response (200):**
```json
{
  "detecciones": [ /* array de detecciones */ ],
  "total_encontradas": 15,
  "tipo_consulta": "rango",
  "parametro": "2025-07-03T11:00:00,2025-07-03T11:10:00",
  "success": true
}
```

### GET `/api/detecciones/todas`
Obtiene todas las detecciones ordenadas por timestamp (más recientes primero).

**Response (200):**
```json
{
  "detecciones": [ /* array de todas las detecciones */ ],
  "total": 150,
  "success": true
}
```

### GET `/api/detecciones/ultimas?limite=N`
Obtiene las últimas N detecciones (por defecto 10, máximo 1000).

**Ejemplo:**
```
GET /api/detecciones/ultimas?limite=5
```

**Response (200):**
```json
{
  "detecciones": [ /* array de las últimas 5 detecciones */ ],
  "limite_solicitado": 5,
  "total_encontradas": 5,
  "success": true
}
```

### GET `/api/detecciones/estadisticas`
Obtiene estadísticas básicas de las detecciones.

**Response (200):**
```json
{
  "total_detecciones": 150,
  "ultimas_5_detecciones": [ /* últimas 5 detecciones */ ],
  "success": true
}
```

### GET `/api/detecciones/ejemplo`
Obtiene un ejemplo del formato esperado para las detecciones.

**Response (200):**
```json
{
  "formato_deteccion": { /* ejemplo del formato */ },
  "nota": "Use POST /api/detecciones para enviar detecciones en este formato",
  "consultas": [
    "GET /api/detecciones?segundo=2025-07-03T11:10:01",
    "GET /api/detecciones?rango=2025-07-03T11:00:00,2025-07-03T11:10:00"
  ],
  "success": true
}
```

### DELETE `/api/detecciones/limpiar`
Elimina todas las detecciones (útil para limpieza y pruebas).

**Response (200):**
```json
{
  "mensaje": "Todas las detecciones han sido eliminadas",
  "success": true
}
```

## ⚡ Características

### Validaciones
- **Timestamp obligatorio**: Debe estar en formato `yyyy-MM-ddTHH:mm:ss`
- **Personas >= 0**: El número de personas no puede ser negativo
- **Coordenadas válidas**: 
  - Debe haber exactamente una coordenada por persona
  - Las coordenadas X e Y deben ser números positivos
  - No pueden ser null

### Manejo de Errores
- **400 Bad Request**: Parámetros inválidos, formato incorrecto, validaciones fallidas
- **500 Internal Server Error**: Errores del servidor o base de datos
- Mensajes de error descriptivos en español
- Respuestas consistentes con campo `success: false`

### Formato de Timestamp
- **Formato**: `yyyy-MM-ddTHH:mm:ss`
- **Ejemplo**: `2025-07-03T14:18:30`
- **Zona horaria**: Local del servidor

## 🧪 Pruebas

### Script de Prueba
Ejecute el script de prueba para verificar todos los endpoints:

```bash
python test_detecciones_api.py
```

Este script:
- ✅ Verifica conectividad con el servidor
- ✅ Prueba creación de detecciones
- ✅ Prueba búsqueda por segundo específico
- ✅ Prueba búsqueda por rango de tiempo
- ✅ Prueba obtención de todas las detecciones
- ✅ Prueba obtención de últimas detecciones
- ✅ Prueba estadísticas
- ✅ Prueba casos de error y validaciones

### Ejemplos de Uso

#### Crear una detección
```bash
curl -X POST http://localhost:8080/api/detecciones \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": "2025-07-03T14:18:00",
    "personas": 2,
    "coordenadas": [
      {"x": 450.2, "y": 800.1},
      {"x": 470.0, "y": 790.5}
    ]
  }'
```

#### Buscar por segundo
```bash
curl "http://localhost:8080/api/detecciones?segundo=2025-07-03T14:18:00"
```

#### Buscar por rango
```bash
curl "http://localhost:8080/api/detecciones?rango=2025-07-03T14:00:00,2025-07-03T15:00:00"
```

## 📊 Base de Datos

### Colección MongoDB: `detecciones`
```javascript
{
  "_id": ObjectId("..."),
  "timestamp": ISODate("2025-07-03T14:18:00.000Z"),
  "personas": 2,
  "coordenadas": [
    {"x": 450.2, "y": 800.1},
    {"x": 470.0, "y": 790.5}
  ]
}
```

### Índices Recomendados
```javascript
// Índice en timestamp para consultas rápidas
db.detecciones.createIndex({"timestamp": 1})

// Índice compuesto para consultas de rango
db.detecciones.createIndex({"timestamp": 1, "personas": 1})
```

## 🔄 Integración con Sistema Existente

Este módulo es independiente y no afecta a los módulos existentes:
- **Personas**: Continúa funcionando normalmente
- **Cámaras**: Sin cambios
- **K-means**: Compatible y sin conflictos

## 📝 Notas de Desarrollo

- **CORS habilitado**: Permite llamadas desde el frontend
- **Validación robusta**: Todas las entradas son validadas
- **Manejo de errores**: Errores descriptivos y códigos HTTP apropiados
- **Logging**: Preparado para agregar logs de auditoría
- **Escalabilidad**: Diseñado para manejar grandes volúmenes de datos

## 🚀 Próximos Pasos

1. **Índices de MongoDB**: Crear índices en producción para optimizar consultas
2. **Métricas**: Agregar métricas de rendimiento
3. **Cachê**: Implementar cachê para consultas frecuentes
4. **Paginación**: Agregar paginación para consultas con muchos resultados
5. **Exportación**: Endpoints para exportar datos en diferentes formatos
