#!/usr/bin/env python3
"""
Test específico para el endpoint de horas disponibles
"""

import requests
import json
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api"
HEADERS = {"Content-Type": "application/json"}

def crear_detecciones_de_prueba():
    """Crea detecciones en diferentes horas para probar el endpoint"""
    print("📝 Creando detecciones de prueba en diferentes horas...")
    
    base_time = datetime.now().replace(minute=0, second=0, microsecond=0)
    
    # Crear detecciones en 3 horas diferentes
    horas_test = [0, 2, 5]  # Hora actual, +2 horas, +5 horas
    
    for i, hora_offset in enumerate(horas_test):
        timestamp = base_time + timedelta(hours=hora_offset)
        timestamp_str = timestamp.strftime("%Y-%m-%dT%H:%M:%S")
        
        deteccion = {
            "timestamp": timestamp_str,
            "personas": i + 1,
            "coordenadas": [
                {"x": 100.0 + (i * 50), "y": 200.0 + (i * 30)}
                for j in range(i + 1)
            ]
        }
        
        try:
            response = requests.post(
                f"{BASE_URL}/detecciones",
                headers=HEADERS,
                data=json.dumps(deteccion)
            )
            
            if response.status_code == 201:
                print(f"✅ Detección creada para hora: {timestamp_str}")
            else:
                print(f"❌ Error creando detección: {response.status_code}")
                
        except Exception as e:
            print(f"❌ Error de conexión: {e}")

def test_horas_disponibles():
    """Test del endpoint de horas disponibles"""
    print("\n🕐 Probando endpoint /detecciones/horas-disponibles...")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones/horas-disponibles")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Respuesta exitosa!")
            print(f"📊 Total de horas disponibles: {data['total_horas']}")
            print("🕐 Horas encontradas:")
            
            for i, hora in enumerate(data['horas_disponibles'], 1):
                print(f"   {i}. {hora}")
            
            print(f"\n📋 Respuesta completa:")
            print(json.dumps(data, indent=2, ensure_ascii=False))
            
        else:
            print(f"❌ Error: {response.status_code}")
            print(f"Response: {response.text}")
            
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def main():
    print("🚀 Test del endpoint /detecciones/horas-disponibles")
    print(f"📡 URL: {BASE_URL}")
    
    # Verificar conectividad
    try:
        response = requests.get(f"{BASE_URL}/detecciones/ejemplo")
        if response.status_code != 200:
            print("❌ El servidor no está disponible")
            return
    except Exception as e:
        print(f"❌ Error de conectividad: {e}")
        return
    
    print("✅ Servidor conectado\n")
    
    # Crear datos de prueba
    crear_detecciones_de_prueba()
    
    # Probar el endpoint
    test_horas_disponibles()
    
    print("\n✅ Test completado")

if __name__ == "__main__":
    main()
