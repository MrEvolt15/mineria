#!/usr/bin/env python3
"""
Script de generación continua de datos para K-means en tiempo real
Simula carga constante de detecciones y permite visualización web
"""

import requests
import json
import time
import threading
import signal
import sys
from datetime import datetime, timedelta
import random
import math

# Configuración
BASE_URL = "http://localhost:8080/api/realtime"
NUM_DETECCIONES_INICIALES = 30  # Detecciones iniciales para establecer patrones
K_CLUSTERS = 4                  # Número de clusters para K-means (coincide con defaultValue en controller)
INTERVALO_DETECCIONES = 2       # Segundos entre nuevas detecciones
INTERVALO_LOTES = 10           # Segundos entre lotes de detecciones
LOTE_SIZE = 3                  # Tamaño de lotes para simular múltiples cámaras

# Control de ejecución
ejecutando = True
estadisticas = {
    "detecciones_enviadas": 0,
    "lotes_enviados": 0,
    "errores": 0,
    "inicio": None
}

def signal_handler(sig, frame):
    """Maneja la interrupción del script (Ctrl+C)"""
    global ejecutando
    print('\n🛑 Deteniendo generación de datos...')
    ejecutando = False
    mostrar_estadisticas_finales()
    sys.exit(0)

def mostrar_estadisticas_finales():
    """Muestra estadísticas finales del generador"""
    if estadisticas["inicio"]:
        duracion = datetime.now() - estadisticas["inicio"]
        print(f"\n📊 ESTADÍSTICAS FINALES:")
        print(f"⏰ Duración: {duracion}")
        print(f"📡 Detecciones enviadas: {estadisticas['detecciones_enviadas']}")
        print(f"📦 Lotes enviados: {estadisticas['lotes_enviados']}")
        print(f"❌ Errores: {estadisticas['errores']}")
        if duracion.total_seconds() > 0:
            rate = estadisticas['detecciones_enviadas'] / duracion.total_seconds()
            print(f"🚀 Tasa promedio: {rate:.2f} detecciones/segundo")

# Configurar manejo de señales
signal.signal(signal.SIGINT, signal_handler)

def generar_zonas_calientes():
    """Define zonas calientes dinámicas que cambian con el tiempo"""
    # Zonas que simulan diferentes áreas de un edificio
    zonas_base = [
        {"nombre": "Entrada Principal", "centro_x": 0.0, "centro_y": 0.0, "radio": 2.5, "probabilidad": 0.4},
        {"nombre": "Cafetería", "centro_x": 8.0, "centro_y": 3.0, "radio": 2.0, "probabilidad": 0.3},
        {"nombre": "Sala de Reuniones", "centro_x": -5.0, "centro_y": 5.0, "radio": 1.8, "probabilidad": 0.2},
        {"nombre": "Área de Trabajo", "centro_x": 3.0, "centro_y": -4.0, "radio": 3.0, "probabilidad": 0.1}
    ]
    
    # Modificar probabilidades según la hora del día simulada
    hora_simulada = (datetime.now().minute % 24)  # Simula 24 horas en 24 minutos
    
    if 8 <= hora_simulada <= 9:  # Hora pico mañana
        zonas_base[0]["probabilidad"] = 0.6  # Más actividad en entrada
    elif 12 <= hora_simulada <= 13:  # Hora almuerzo
        zonas_base[1]["probabilidad"] = 0.5  # Más actividad en cafetería
    elif 17 <= hora_simulada <= 18:  # Salida
        zonas_base[0]["probabilidad"] = 0.7  # Mucha actividad en entrada
    
    return zonas_base

def generar_detecciones_iniciales():
    """Genera múltiples detecciones realistas para establecer patrones iniciales"""
    detecciones = []
    
    print("🎯 Generando detecciones con zonas calientes dinámicas...")
    
    for i in range(NUM_DETECCIONES_INICIALES):
        deteccion, zona = generar_deteccion_realista()
        detecciones.append(deteccion)
        
        if i % 10 == 0:
            print(f"   📍 Generadas {i+1}/{NUM_DETECCIONES_INICIALES} detecciones...")
        
        # Pequeña pausa para simular tiempo real y evitar IDs duplicados
        time.sleep(0.05)
    
    return detecciones

def generar_deteccion_realista():
    """Genera una detección realista basada en zonas calientes"""
    zonas = generar_zonas_calientes()
    
    # Seleccionar zona basada en probabilidades
    rand = random.random()
    prob_acumulada = 0
    zona_seleccionada = zonas[0]
    
    for zona in zonas:
        prob_acumulada += zona["probabilidad"]
        if rand <= prob_acumulada:
            zona_seleccionada = zona
            break
    
    # Generar posición alrededor del centro de la zona
    angle = random.uniform(0, 2 * math.pi)
    radius = random.uniform(0, zona_seleccionada["radio"]) * random.uniform(0.3, 1.0)
    
    pos_x = zona_seleccionada["centro_x"] + radius * math.cos(angle)
    pos_y = zona_seleccionada["centro_y"] + radius * math.sin(angle)
    
    # Agregar algo de ruido para realismo
    pos_x += random.uniform(-0.5, 0.5)
    pos_y += random.uniform(-0.5, 0.5)
    
    # Generar ID único basado en timestamp para evitar problemas
    timestamp_ms = int(datetime.now().timestamp() * 1000)
    unique_id = timestamp_ms % 10000 + random.randint(1, 999)
    
    deteccion = {
        "ncam": random.randint(1, 15),
        "time": datetime.now().isoformat(),
        "posX": round(pos_x, 2),
        "posY": round(pos_y, 2),
        "tipoPersona": random.choice(["E", "PR", "PA"]),  # Tipos válidos según Persona.java
        "genero": random.choice(["M", "F"]),
        "id": unique_id
    }
    
    return deteccion, zona_seleccionada["nombre"]

def enviar_detecciones(detecciones):
    """Envía las detecciones al API"""
    print(f"📡 Enviando {len(detecciones)} detecciones...")
    
    # Enviar en lotes de 10
    lote_size = 10
    for i in range(0, len(detecciones), lote_size):
        lote = detecciones[i:i+lote_size]
        
        try:
            response = requests.post(f"{BASE_URL}/camera-data/batch", json=lote)
            if response.status_code == 200:
                print(f"✅ Lote {i//lote_size + 1} enviado exitosamente")
            else:
                print(f"❌ Error en lote {i//lote_size + 1}: {response.status_code}")
                print(f"   Respuesta: {response.text}")
        except Exception as e:
            print(f"❌ Excepción en lote {i//lote_size + 1}: {e}")
            
        time.sleep(0.5)  # Pausa entre lotes

def probar_kmeans():
    """Prueba los endpoints de K-means"""
    print(f"\n🧠 Probando K-means con {K_CLUSTERS} clusters...")
    
    try:
        # 1. Obtener mapa de calor con K-means
        print("🎯 Aplicando K-means...")
        response = requests.get(f"{BASE_URL}/heatmap-kmeans", params={"k": K_CLUSTERS})
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ K-means aplicado exitosamente")
            print(f"   Total detecciones: {data.get('totalDetecciones', 0)}")
            print(f"   Número de clusters: {data.get('numeroClusters', 0)}")
            
            # Mostrar centroides
            centroides = data.get('centroides', [])
            print(f"\n📍 Centroides encontrados:")
            for i, centroide in enumerate(centroides):
                print(f"   Cluster {centroide.get('clusterId', i)}: "
                      f"pos=({centroide.get('posX', 0):.2f}, {centroide.get('posY', 0):.2f}), "
                      f"densidad={centroide.get('densidad', 0)}, "
                      f"intensidad={centroide.get('intensidad', 0):.2f}")
        else:
            print(f"❌ Error aplicando K-means: {response.status_code}")
            print(f"   Respuesta: {response.text}")
            
    except Exception as e:
        print(f"❌ Excepción probando K-means: {e}")

def probar_centroides_cache():
    """Prueba la obtención de centroides del cache"""
    print(f"\n🎯 Probando obtención de centroides del cache...")
    
    try:
        response = requests.get(f"{BASE_URL}/centroides", params={"k": K_CLUSTERS})
        
        if response.status_code == 200:
            centroides = response.json()
            print(f"✅ Centroides obtenidos del cache: {len(centroides)} centroides")
            
            for centroide in centroides:
                print(f"   🔴 Cluster {centroide.get('clusterId', '?')}: "
                      f"densidad={centroide.get('densidad', 0)}")
        else:
            print(f"❌ Error obteniendo centroides: {response.status_code}")
            
    except Exception as e:
        print(f"❌ Excepción obteniendo centroides: {e}")

def limpiar_cache():
    """Limpia el cache de K-means"""
    print(f"\n🧹 Limpiando cache de K-means...")
    
    try:
        response = requests.post(f"{BASE_URL}/limpiar-cache-kmeans")
        
        if response.status_code == 200:
            print("✅ Cache limpiado exitosamente")
        else:
            print(f"❌ Error limpiando cache: {response.status_code}")
            
    except Exception as e:
        print(f"❌ Excepción limpiando cache: {e}")

def limpiar_datos_corruptos():
    """Limpia datos corruptos del sistema"""
    print(f"\n🔧 Ejecutando limpieza de datos corruptos...")
    
    try:
        response = requests.post(f"{BASE_URL}/limpiar-datos-corruptos")
        
        if response.status_code == 200:
            print("✅ Limpieza completada:")
            print(response.text)
        else:
            print(f"❌ Error en limpieza: {response.status_code}")
            print(f"   Respuesta: {response.text}")
            
    except Exception as e:
        print(f"❌ Excepción durante limpieza: {e}")

def verificar_servidor():
    """Verifica que el servidor esté corriendo"""
    try:
        response = requests.get(f"{BASE_URL}/stats")
        return response.status_code == 200
    except:
        return False

def ejecutar_modo_continuo():
    """Ejecuta el generador en modo continuo (background)"""
    global ejecutando, estadisticas
    
    print("🔄 MODO CONTINUO ACTIVADO")
    print("=" * 50)
    print(f"⚙️ Configuración:")
    print(f"   • Intervalo entre detecciones: {INTERVALO_DETECCIONES}s")
    print(f"   • Tamaño de lotes: {LOTE_SIZE} detecciones")
    print(f"   • Intervalo entre lotes: {INTERVALO_LOTES}s")
    print(f"   • Clusters K-means: {K_CLUSTERS}")
    print("\n🛑 Presiona Ctrl+C para detener\n")
    
    estadisticas["inicio"] = datetime.now()
    
    try:
        # Limpiar cache inicial y datos corruptos
        limpiar_datos_corruptos()
        
        # Generar datos iniciales
        print("📊 Generando datos iniciales...")
        detecciones_iniciales = generar_detecciones_iniciales()
        enviar_detecciones(detecciones_iniciales)
        estadisticas["detecciones_enviadas"] += len(detecciones_iniciales)
        estadisticas["lotes_enviados"] += 1
        
        # Esperar procesamiento inicial
        time.sleep(5)
        
        # Intentar K-means con manejo de errores
        try:
            probar_kmeans()
        except Exception as e:
            print(f"⚠️ Error inicial en K-means: {e}")
            print("🔧 Ejecutando limpieza automática...")
            limpiar_datos_corruptos()
            time.sleep(2)
            print("🔄 Reintentando K-means...")
            try:
                probar_kmeans()
            except Exception as e2:
                print(f"⚠️ K-means aún no funciona, continuando con envío de datos: {e2}")
        
        print("\n🚀 Iniciando generación continua de datos...")
        
        while ejecutando:
            try:
                # Generar lote de detecciones
                lote_detecciones = []
                for _ in range(LOTE_SIZE):
                    deteccion, zona = generar_deteccion_realista()
                    lote_detecciones.append(deteccion)
                    time.sleep(0.01)  # Pequeña pausa para IDs únicos
                
                # Enviar lote
                response = requests.post(f"{BASE_URL}/camera-data/batch", json=lote_detecciones)
                
                if response.status_code == 200:
                    estadisticas["detecciones_enviadas"] += len(lote_detecciones)
                    estadisticas["lotes_enviados"] += 1
                    
                    # Mostrar progreso cada 10 lotes
                    if estadisticas["lotes_enviados"] % 10 == 0:
                        duracion = datetime.now() - estadisticas["inicio"]
                        rate = estadisticas["detecciones_enviadas"] / duracion.total_seconds()
                        print(f"📈 Lote #{estadisticas['lotes_enviados']}: "
                              f"{estadisticas['detecciones_enviadas']} detecciones "
                              f"({rate:.1f}/s)")
                        
                        # Actualizar centroides ocasionalmente
                        if estadisticas["lotes_enviados"] % 20 == 0:
                            print("🧠 Actualizando centroides K-means...")
                            probar_centroides_cache()
                else:
                    estadisticas["errores"] += 1
                    print(f"❌ Error enviando lote: {response.status_code}")
                    print(f"   Respuesta: {response.text}")
                    if response.status_code == 400:
                        print("   💡 Revisa el formato de los datos enviados")
                
                # Esperar antes del siguiente lote
                time.sleep(INTERVALO_LOTES)
                
            except requests.exceptions.RequestException as e:
                estadisticas["errores"] += 1
                print(f"❌ Error de conexión: {e}")
                time.sleep(5)  # Esperar más tiempo en caso de error
                
            except KeyboardInterrupt:
                break
                
    except KeyboardInterrupt:
        pass
    finally:
        ejecutando = False
        mostrar_estadisticas_finales()

def main():
    global INTERVALO_LOTES, LOTE_SIZE, K_CLUSTERS
    import argparse
    
    parser = argparse.ArgumentParser(description='Generador de datos K-means para API Minería')
    parser.add_argument('--continuo', '-c', action='store_true', 
                       help='Ejecutar en modo continuo (background)')
    parser.add_argument('--limpiar', '-l', action='store_true',
                       help='Solo limpiar datos corruptos y salir')
    parser.add_argument('--intervalo', '-i', type=int, default=INTERVALO_LOTES,
                       help='Intervalo entre lotes en segundos (default: 10)')
    parser.add_argument('--lote-size', '-s', type=int, default=LOTE_SIZE,
                       help='Tamaño de lotes (default: 3)')
    parser.add_argument('--clusters', '-k', type=int, default=K_CLUSTERS,
                       help='Número de clusters K (default: 4)')
    
    args = parser.parse_args()
    
    # Actualizar configuración global
    INTERVALO_LOTES = args.intervalo
    LOTE_SIZE = args.lote_size
    K_CLUSTERS = args.clusters
    
    if args.continuo:
        print("🔄 GENERADOR CONTINUO K-MEANS - API MINERÍA")
        print("=" * 50)
    else:
        print("🧠 PRUEBA DE K-MEANS - API MINERÍA")
        print("=" * 50)
    
    # Verificar servidor
    if not verificar_servidor():
        print("❌ Error: El servidor no está corriendo en localhost:8080")
        print("   Por favor, ejecuta la aplicación Spring Boot primero.")
        return
    
    print("✅ Servidor encontrado")
    
    # Solo limpiar si se especifica la opción
    if args.limpiar:
        print("🔧 MODO LIMPIEZA - Solo limpiando datos corruptos")
        limpiar_datos_corruptos()
        return
    
    if args.continuo:
        ejecutar_modo_continuo()
    else:
        ejecutar_modo_prueba()

def ejecutar_modo_prueba():
    """Ejecuta el generador en modo de prueba (foreground)"""
    try:
        # 1. Limpiar cache previo y datos corruptos
        limpiar_datos_corruptos()
        
        # 2. Generar detecciones con patrones
        print(f"\n📊 Generando {NUM_DETECCIONES_INICIALES} detecciones con patrones geográficos...")
        detecciones = generar_detecciones_iniciales()
        
        # 3. Enviar detecciones
        enviar_detecciones(detecciones)
        
        # 4. Esperar un poco para que se procesen
        print("\n⏳ Esperando procesamiento...")
        time.sleep(3)
        
        # 5. Probar K-means con manejo de errores
        try:
            probar_kmeans()
        except Exception as e:
            print(f"⚠️ Error en K-means: {e}")
            print("🔧 Ejecutando limpieza automática...")
            limpiar_datos_corruptos()
            time.sleep(2)
            print("🔄 Reintentando K-means...")
            try:
                probar_kmeans()
            except Exception as e2:
                print(f"⚠️ K-means aún presenta problemas: {e2}")
                print("💡 Continúa enviando más datos para tener suficientes registros válidos")
        
        # 6. Probar cache de centroides
        probar_centroides_cache()
        
        # 7. Simular actualización en tiempo real
        print(f"\n🔄 Simulando detecciones en tiempo real...")
        for i in range(5):
            deteccion, zona = generar_deteccion_realista()
            
            requests.post(f"{BASE_URL}/camera-data", json=deteccion)
            print(f"   ➕ Detección {i+1} enviada: ({deteccion['posX']}, {deteccion['posY']}) - Zona: {zona}")
            time.sleep(1)
        
        # 8. Verificar centroides actualizados
        print(f"\n🔄 Verificando centroides actualizados...")
        probar_centroides_cache()
        
        print(f"\n🎉 ¡Prueba de K-means completada!")
        print(f"💡 Los centroides se actualizan automáticamente con cada nueva detección")
        print(f"🌐 Usa GET /api/realtime/centroides en tu frontend para visualización en tiempo real")
        
    except Exception as e:
        print(f"\n❌ Error durante la prueba: {e}")

if __name__ == "__main__":
    main()
