#!/usr/bin/env python3
"""
Test específico para el endpoint de timestamps
"""

import requests
import json
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api"
HEADERS = {"Content-Type": "application/json"}

def crear_detecciones_con_timestamps():
    """Crea detecciones con timestamps específicos para probar"""
    print("📝 Creando detecciones con timestamps específicos...")
    
    base_time = datetime.now().replace(second=0, microsecond=0)
    
    # Crear detecciones con diferentes timestamps
    timestamps_test = [
        base_time,
        base_time + timedelta(minutes=1),
        base_time + timedelta(minutes=3),
        base_time + timedelta(minutes=5),
        base_time + timedelta(minutes=10)
    ]
    
    for i, timestamp in enumerate(timestamps_test):
        timestamp_str = timestamp.strftime("%Y-%m-%dT%H:%M:%S")
        
        deteccion = {
            "timestamp": timestamp_str,
            "personas": 1,
            "coordenadas": [
                {"x": 100.0 + (i * 10), "y": 200.0 + (i * 10)}
            ]
        }
        
        try:
            response = requests.post(
                f"{BASE_URL}/detecciones",
                headers=HEADERS,
                data=json.dumps(deteccion)
            )
            
            if response.status_code == 201:
                print(f"✅ Detección creada: {timestamp_str}")
            else:
                print(f"❌ Error creando detección: {response.status_code}")
                
        except Exception as e:
            print(f"❌ Error de conexión: {e}")

def test_timestamps():
    """Test del endpoint de timestamps"""
    print("\n🕒 Probando endpoint /detecciones/timestamps...")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones/timestamps")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Respuesta exitosa!")
            print(f"📊 Total de timestamps: {data['total']}")
            print("🕒 Timestamps encontrados:")
            
            for i, timestamp in enumerate(data['timestamps'], 1):
                print(f"   {i}. {timestamp}")
            
            # Verificar que los timestamps están ordenados
            timestamps = data['timestamps']
            if len(timestamps) > 1:
                ordenados = all(timestamps[i] <= timestamps[i+1] for i in range(len(timestamps)-1))
                if ordenados:
                    print("✅ Los timestamps están correctamente ordenados")
                else:
                    print("⚠️  Los timestamps no están ordenados")
            
            print(f"\n📋 Respuesta completa:")
            print(json.dumps(data, indent=2, ensure_ascii=False))
            
        else:
            print(f"❌ Error: {response.status_code}")
            print(f"Response: {response.text}")
            
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def comparar_con_detecciones_completas():
    """Compara el resultado con el endpoint de detecciones completas"""
    print("\n🔍 Comparando con endpoint de detecciones completas...")
    
    try:
        # Obtener timestamps
        response_timestamps = requests.get(f"{BASE_URL}/detecciones/timestamps")
        # Obtener detecciones completas
        response_completas = requests.get(f"{BASE_URL}/detecciones/todas")
        
        if response_timestamps.status_code == 200 and response_completas.status_code == 200:
            timestamps_data = response_timestamps.json()
            completas_data = response_completas.json()
            
            timestamps_solo = timestamps_data['timestamps']
            timestamps_completas = [det['timestamp'] for det in completas_data['detecciones']]
            
            if timestamps_solo == timestamps_completas:
                print("✅ Los timestamps coinciden perfectamente con las detecciones completas")
            else:
                print("⚠️  Hay diferencias entre los timestamps:")
                print(f"   Solo timestamps: {len(timestamps_solo)} elementos")
                print(f"   Detecciones completas: {len(timestamps_completas)} elementos")
                
        else:
            print("❌ Error obteniendo datos para comparación")
            
    except Exception as e:
        print(f"❌ Error en comparación: {e}")

def main():
    print("🚀 Test del endpoint /detecciones/timestamps")
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
    crear_detecciones_con_timestamps()
    
    # Probar el endpoint
    test_timestamps()
    
    # Comparar con otros endpoints
    comparar_con_detecciones_completas()
    
    print("\n✅ Test completado")

if __name__ == "__main__":
    main()
