# API de Minería - Documentación de Endpoints

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
