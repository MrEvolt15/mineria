#!/usr/bin/env python3
"""
Script para crear datos de prueba y probar el mapa de calor
"""

import requests
import json
import random
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api"
HEADERS = {"Content-Type": "application/json"}

def crear_datos_realistas():
    """Crea datos de detección realistas para el mapa de calor"""
    print("🎯 Creando datos realistas para el mapa de calor...")
    
    base_time = datetime.now().replace(second=0, microsecond=0)
    
    # Escenarios diferentes
    escenarios = [
        # Hora punta mañana - mucha actividad
        {"hora": 8, "personas_base": 5, "variacion": 3, "descripcion": "Hora punta mañana"},
        {"hora": 9, "personas_base": 7, "variacion": 2, "descripcion": "Inicio actividad laboral"},
        
        # Mediodía - actividad moderada
        {"hora": 12, "personas_base": 3, "variacion": 2, "descripcion": "Pausa almuerzo"},
        {"hora": 13, "personas_base": 4, "variacion": 1, "descripcion": "Regreso almuerzo"},
        
        # Tarde - actividad variable
        {"hora": 15, "personas_base": 2, "variacion": 3, "descripcion": "Tarde tranquila"},
        {"hora": 17, "personas_base": 6, "variacion": 2, "descripcion": "Hora punta tarde"},
        
        # Noche - poca actividad
        {"hora": 20, "personas_base": 1, "variacion": 2, "descripcion": "Actividad nocturna"},
        {"hora": 22, "personas_base": 0, "variacion": 1, "descripcion": "Noche"},
    ]
    
    detecciones_creadas = 0
    
    for escenario in escenarios:
        # Crear varias detecciones por escenario
        for minuto in range(0, 60, 10):  # Cada 10 minutos
            timestamp = base_time.replace(hour=escenario["hora"], minute=minuto)
            timestamp_str = timestamp.strftime("%Y-%m-%dT%H:%M:%S")
            
            # Calcular número de personas para este momento
            personas = max(0, escenario["personas_base"] + random.randint(-escenario["variacion"], escenario["variacion"]))
            
            if personas == 0:
                continue  # Saltear si no hay personas
            
            # Generar coordenadas realistas
            coordenadas = generar_coordenadas_realistas(personas)
            
            deteccion = {
                "timestamp": timestamp_str,
                "personas": personas,
                "coordenadas": coordenadas
            }
            
            try:
                response = requests.post(
                    f"{BASE_URL}/detecciones",
                    headers=HEADERS,
                    data=json.dumps(deteccion)
                )
                
                if response.status_code == 201:
                    detecciones_creadas += 1
                    print(f"✅ {escenario['descripcion']}: {timestamp_str} - {personas} personas")
                else:
                    print(f"❌ Error: {response.status_code}")
                    
            except Exception as e:
                print(f"❌ Error de conexión: {e}")
    
    print(f"\n🎉 Creadas {detecciones_creadas} detecciones de prueba")

def generar_coordenadas_realistas(num_personas):
    """Genera coordenadas realistas que simulan movimiento en un espacio"""
    coordenadas = []
    
    # Definir zonas de actividad típicas
    zonas = [
        {"centro_x": 200, "centro_y": 300, "radio": 100},  # Zona entrada
        {"centro_x": 500, "centro_y": 200, "radio": 80},   # Zona central
        {"centro_x": 300, "centro_y": 500, "radio": 120},  # Zona servicios
        {"centro_x": 700, "centro_y": 400, "radio": 90},   # Zona trabajo
    ]
    
    for i in range(num_personas):
        # Elegir una zona aleatoria
        zona = random.choice(zonas)
        
        # Generar coordenada dentro de la zona con distribución normal
        angulo = random.uniform(0, 2 * 3.14159)
        distancia = random.gauss(0, zona["radio"] / 3)
        distancia = abs(distancia)  # Asegurar que sea positiva
        distancia = min(distancia, zona["radio"])  # Limitar al radio
        
        x = zona["centro_x"] + distancia * random.uniform(-1, 1)
        y = zona["centro_y"] + distancia * random.uniform(-1, 1)
        
        # Asegurar que las coordenadas estén en rango válido
        x = max(10, min(990, x))
        y = max(10, min(990, y))
        
        coordenadas.append({
            "x": round(x, 1),
            "y": round(y, 1)
        })
    
    return coordenadas

def verificar_servidor():
    """Verifica que el servidor esté funcionando"""
    try:
        response = requests.get(f"{BASE_URL}/detecciones/ejemplo", timeout=5)
        return response.status_code == 200
    except:
        return False

def mostrar_estadisticas():
    """Muestra estadísticas de los datos creados"""
    print("\n📊 Obteniendo estadísticas...")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones/estadisticas")
        if response.status_code == 200:
            data = response.json()
            print(f"📈 Total de detecciones: {data['total_detecciones']}")
            
            # Obtener timestamps
            response_timestamps = requests.get(f"{BASE_URL}/detecciones/timestamps")
            if response_timestamps.status_code == 200:
                timestamps_data = response_timestamps.json()
                print(f"🕐 Timestamps disponibles: {timestamps_data['total']}")
                
                if timestamps_data['timestamps']:
                    print(f"⏰ Primer timestamp: {timestamps_data['timestamps'][0]}")
                    print(f"⏰ Último timestamp: {timestamps_data['timestamps'][-1]}")
            
            # Obtener horas disponibles
            response_horas = requests.get(f"{BASE_URL}/detecciones/horas-disponibles")
            if response_horas.status_code == 200:
                horas_data = response_horas.json()
                print(f"🕐 Horas únicas: {horas_data['total_horas']}")
                if horas_data['horas_disponibles']:
                    print("🕐 Horas con actividad:")
                    for hora in horas_data['horas_disponibles']:
                        print(f"   - {hora}")
        
    except Exception as e:
        print(f"❌ Error obteniendo estadísticas: {e}")

def main():
    print("🎯 Generador de datos para Mapa de Calor")
    print(f"🌐 Servidor: {BASE_URL}")
    
    # Verificar servidor
    if not verificar_servidor():
        print("❌ El servidor no está disponible")
        print("💡 Asegúrese de que Spring Boot esté ejecutándose en puerto 8080")
        return
    
    print("✅ Servidor conectado")
    
    # Menú de opciones
    print("\n📋 Opciones:")
    print("1. Crear datos de prueba realistas")
    print("2. Limpiar datos existentes")
    print("3. Mostrar estadísticas")
    print("4. Crear datos y mostrar estadísticas")
    
    try:
        opcion = input("\n➡️  Seleccione una opción (1-4): ").strip()
    except KeyboardInterrupt:
        print("\n👋 Saliendo...")
        return
    
    if opcion == "1":
        crear_datos_realistas()
    elif opcion == "2":
        try:
            response = requests.delete(f"{BASE_URL}/detecciones/limpiar")
            if response.status_code == 200:
                print("✅ Datos limpiados exitosamente")
            else:
                print(f"❌ Error limpiando datos: {response.status_code}")
        except Exception as e:
            print(f"❌ Error: {e}")
    elif opcion == "3":
        mostrar_estadisticas()
    elif opcion == "4":
        crear_datos_realistas()
        mostrar_estadisticas()
    else:
        print("❌ Opción no válida")
        return
    
    print(f"\n🎉 ¡Listo! Ahora puedes abrir 'detecciones_heatmap.html' para ver el mapa de calor")
    print("💡 El archivo HTML se conectará automáticamente al servidor para obtener los datos")

if __name__ == "__main__":
    main()
