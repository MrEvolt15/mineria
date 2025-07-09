# 🔥 Mapa de Calor - Detección de Personas

Una visualización interactiva y moderna para analizar patrones de movimiento y actividad basada en los datos de detección de personas.

## 🎯 Características Principales

### ✨ **Visualización Avanzada**
- 🗺️ **Mapa de calor dinámico** que muestra zonas de alta actividad
- 👥 **Puntos de personas** con posiciones exactas y animaciones
- 🎨 **Interfaz moderna** con gradientes y efectos visuales
- 📱 **Responsive** - funciona en desktop y móvil

### ⏰ **Control Temporal**
- 🎚️ **Slider temporal** para navegar por todas las detecciones
- ▶️ **Reproducción automática** con controles play/pause
- 🔄 **Reinicio** y navegación manual
- 📊 **Estadísticas en tiempo real**

### 📊 **Información Detallada**
- 👥 **Contador de personas** detectadas en cada momento
- 📈 **Estadísticas totales** de actividad
- 🕐 **Timestamp actual** con formato legible
- 📍 **Coordenadas precisas** de cada persona

## 🚀 Cómo Usar

### **1. Preparar los Datos**
```bash
# Generar datos de prueba realistas
python generar_datos_heatmap.py
```

Opciones disponibles:
- **Crear datos realistas** - Simula actividad durante diferentes horas del día
- **Limpiar datos** - Elimina detecciones existentes
- **Ver estadísticas** - Muestra resumen de datos disponibles

### **2. Iniciar el Servidor**
Asegúrese de que el servidor Spring Boot esté ejecutándose:
```bash
# Desde la carpeta del proyecto Spring Boot
mvn spring-boot:run
```

### **3. Abrir el Mapa de Calor**
Abra el archivo `detecciones_heatmap.html` en su navegador web.

## 🎮 Controles de la Interfaz

### **📊 Panel de Control**
- **Slider temporal**: Arrastra para navegar manualmente por el tiempo
- **Display de tiempo**: Muestra el timestamp actual formateado
- **Estadísticas**: Personas detectadas, total de detecciones, hora actual

### **🎮 Botones de Control**
- **▶️ Reproducir**: Inicia la reproducción automática (1 detección por segundo)
- **⏸️ Pausar**: Pausa la reproducción automática
- **🔄 Reiniciar**: Vuelve al inicio de la línea temporal
- **📊 Actualizar**: Recarga los datos desde el servidor

### **🗺️ Área de Visualización**
- **Grid de fondo**: Ayuda a ubicar las posiciones
- **Zonas de calor**: Áreas rojas indican alta actividad acumulada
- **Puntos de personas**: Círculos rojos con números para cada persona detectada
- **Efectos de pulsación**: Animaciones que indican actividad

## 🎨 Elementos Visuales

### **🔥 Mapa de Calor**
```
Rojo claro (alpha 0.1-0.3) = Poca actividad acumulada
Rojo intenso (alpha 0.3+)   = Alta actividad acumulada
```

### **👥 Personas Detectadas**
```
🔴 Círculo rojo sólido = Persona actual
⚪ Borde blanco = Fácil identificación
🔢 Número = ID de la persona en esa detección
💫 Pulsación = Efecto de actividad
```

### **📊 Información en Pantalla**
```
📍 Esquina superior izquierda = Contador de personas actual
📈 Panel superior = Estadísticas generales
🕐 Display azul = Timestamp actual
📋 Leyenda = Explicación de colores
```

## 📱 Características Técnicas

### **🛠️ Tecnologías Utilizadas**
- **HTML5 Canvas** para renderizado de alto rendimiento
- **JavaScript ES6** con clases y módulos modernos
- **CSS Grid/Flexbox** para layout responsive
- **Fetch API** para comunicación con el backend
- **CSS Animations** para efectos visuales

### **⚡ Optimizaciones**
- **Rendering eficiente** usando Canvas 2D
- **Carga asíncrona** de datos
- **Responsive design** con media queries
- **Manejo de errores** robusto
- **Indicadores de carga** y estado

### **🔌 Integración con Backend**
```javascript
// Endpoints utilizados
GET /api/detecciones/todas    // Obtiene todas las detecciones
GET /api/detecciones/ejemplo  // Verifica conectividad

// Formato de datos esperado
{
  "detecciones": [
    {
      "timestamp": "2025-07-04T14:18:00",
      "personas": 3,
      "coordenadas": [
        {"x": 450.2, "y": 800.1},
        {"x": 470.0, "y": 790.5},
        {"x": 490.5, "y": 810.3}
      ]
    }
  ]
}
```

## 🎯 Casos de Uso

### **🏢 Análisis de Espacios Comerciales**
- Identificar zonas de mayor tráfico
- Optimizar ubicación de productos/servicios
- Análisis de patrones por horario

### **🚶 Estudio de Flujo Peatonal**
- Rutas más utilizadas
- Horarios pico de actividad
- Distribución espacial de personas

### **🔒 Seguridad y Monitoreo**
- Detección de aglomeraciones
- Monitoreo de áreas restringidas
- Análisis de comportamiento temporal

### **📊 Business Intelligence**
- Métricas de ocupación
- Tendencias temporales
- Reportes visuales para decisiones

## 🐛 Solución de Problemas

### **❌ No se cargan los datos**
```bash
# Verificar que el servidor esté corriendo
curl http://localhost:8080/api/detecciones/ejemplo

# Verificar datos en la base de datos
python test_detecciones_api.py
```

### **⚠️ Canvas en blanco**
- Verificar que el navegador soporte HTML5 Canvas
- Abrir las herramientas de desarrollador (F12) para ver errores
- Refrescar la página o limpiar caché

### **🐌 Rendimiento lento**
- Reducir la cantidad de datos de prueba
- Cerrar otras pestañas del navegador
- Usar un navegador moderno (Chrome, Firefox, Safari)

### **📱 Problemas en móvil**
- Verificar que el diseño sea responsive
- Probar con orientación horizontal
- Verificar conectividad de red

## 🔧 Personalización

### **🎨 Cambiar Colores**
```css
/* En el archivo HTML, buscar estas variables */
--primary-color: #667eea;     /* Color principal */
--person-color: #ff6b6b;      /* Color de las personas */
--background: #f8f9fa;        /* Fondo del canvas */
```

### **⚙️ Ajustar Velocidad de Reproducción**
```javascript
// En el método play(), cambiar el intervalo
setInterval(() => {
    // ... código ...
}, 1000); // Cambiar 1000ms (1 segundo) por el valor deseado
```

### **📏 Modificar Tamaño del Grid**
```javascript
// En drawBackground(), cambiar gridSize
const gridSize = 50; // Cambiar por el valor deseado
```

## 📈 Próximas Mejoras

- 🎯 **Filtros avanzados** por rango de tiempo
- 📊 **Gráficos estadísticos** adicionales  
- 🎨 **Temas personalizables** (oscuro/claro)
- 📱 **App móvil nativa** para mejor rendimiento
- 🔄 **Actualizaciones en tiempo real** con WebSockets
- 📂 **Exportación** de visualizaciones como imágenes

## 📞 Soporte

Para problemas o sugerencias:
1. Verificar que el servidor Spring Boot esté funcionando
2. Revisar la consola del navegador (F12) para errores
3. Probar con datos de ejemplo usando `generar_datos_heatmap.py`
4. Verificar la conectividad de red

¡Disfruta explorando tus datos de detección con este mapa de calor interactivo! 🔥📊
