#!/usr/bin/env python3
"""
Script de prueba para el endpoint de clustering DBSCAN
"""

import requests
import json
import random
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api"

def generar_datos_prueba():
    """Genera algunos datos de prueba para clustering"""
    detecciones = []
    base_time = datetime.now()
    
    # Cluster 1: Área de entrada (alrededor de 100, 100)
    for i in range(5):
        timestamp = base_time + timedelta(seconds=i)
        detecciones.append({
            "timestamp": timestamp.strftime("%Y-%m-%dT%H:%M:%S"),
            "personas": random.randint(1, 3),
            "coordenadas": [
                {
                    "x": 100 + random.uniform(-20, 20),
                    "y": 100 + random.uniform(-20, 20)
                } for _ in range(random.randint(1, 3))
            ]
        })
    
    # Cluster 2: Área central (alrededor de 300, 200)
    for i in range(4):
        timestamp = base_time + timedelta(seconds=i + 10)
        detecciones.append({
            "timestamp": timestamp.strftime("%Y-%m-%dT%H:%M:%S"),
            "personas": random.randint(1, 2),
            "coordenadas": [
                {
                    "x": 300 + random.uniform(-25, 25),
                    "y": 200 + random.uniform(-25, 25)
                } for _ in range(random.randint(1, 2))
            ]
        })
    
    # Puntos de ruido (dispersos)
    for i in range(3):
        timestamp = base_time + timedelta(seconds=i + 20)
        detecciones.append({
            "timestamp": timestamp.strftime("%Y-%m-%dT%H:%M:%S"),
            "personas": 1,
            "coordenadas": [
                {
                    "x": random.uniform(500, 800),
                    "y": random.uniform(400, 600)
                }
            ]
        })
    
    return detecciones

def limpiar_datos():
    """Limpia todos los datos existentes"""
    print("🧹 Limpiando datos existentes...")
    try:
        response = requests.delete(f"{BASE_URL}/detecciones/limpiar")
        if response.status_code == 200:
            print("✅ Datos limpiados exitosamente")
        else:
            print(f"❌ Error limpiando datos: {response.status_code}")
    except Exception as e:
        print(f"❌ Error de conexión al limpiar: {e}")

def subir_datos(detecciones):
    """Sube los datos de prueba"""
    print("📤 Subiendo datos de prueba...")
    exitosos = 0
    
    for deteccion in detecciones:
        try:
            response = requests.post(f"{BASE_URL}/detecciones", json=deteccion)
            if response.status_code == 201:
                exitosos += 1
            else:
                print(f"❌ Error subiendo detección: {response.status_code}")
        except Exception as e:
            print(f"❌ Error de conexión: {e}")
    
    print(f"✅ {exitosos}/{len(detecciones)} detecciones subidas exitosamente")
    return exitosos

def probar_clustering():
    """Prueba el endpoint de clustering"""
    print("\n🔍 Probando clustering DBSCAN...")
    
    # Probar con parámetros por defecto
    print("\n📊 Clustering con parámetros por defecto:")
    try:
        response = requests.get(f"{BASE_URL}/detecciones/clustering")
        if response.status_code == 200:
            data = response.json()
            print("✅ Clustering exitoso")
            
            # Mostrar estadísticas
            estadisticas = data.get('estadisticas', {})
            print(f"   Total detecciones: {data.get('total', 0)}")
            print(f"   Clusters encontrados: {estadisticas.get('totalClusters', 0)}")
            print(f"   Puntos en clusters: {estadisticas.get('puntosEnClusters', 0)}")
            print(f"   Puntos de ruido: {estadisticas.get('puntosRuido', 0)}")
            
            # Mostrar algunos ejemplos
            detecciones = data.get('detecciones', [])
            print(f"\n📋 Ejemplos de detecciones con clustering:")
            for i, det in enumerate(detecciones[:3]):
                print(f"   {i+1}. ID: {det.get('id', 'N/A')[:8]}...")
                print(f"      Timestamp: {det.get('timestamp', 'N/A')}")
                print(f"      Cluster ID: {det.get('clusterId', 'N/A')}")
                print(f"      Tipo: {det.get('clusterTipo', 'N/A')}")
                if det.get('centroideCluster'):
                    centroide = det['centroideCluster']
                    print(f"      Centroide: ({centroide.get('x', 0):.1f}, {centroide.get('y', 0):.1f})")
                print()
            
        else:
            print(f"❌ Error en clustering: {response.status_code}")
            print(f"   Respuesta: {response.text}")
    except Exception as e:
        print(f"❌ Error de conexión: {e}")
    
    # Probar con parámetros personalizados
    print("\n📊 Clustering con parámetros personalizados (eps=0.3, minSamples=2):")
    try:
        response = requests.get(f"{BASE_URL}/detecciones/clustering?eps=0.3&minSamples=2")
        if response.status_code == 200:
            data = response.json()
            print("✅ Clustering exitoso")
            
            estadisticas = data.get('estadisticas', {})
            parametros = data.get('parametros', {})
            print(f"   Parámetros: eps={parametros.get('eps')}, minSamples={parametros.get('minSamples')}")
            print(f"   Clusters encontrados: {estadisticas.get('totalClusters', 0)}")
            print(f"   Puntos en clusters: {estadisticas.get('puntosEnClusters', 0)}")
            print(f"   Puntos de ruido: {estadisticas.get('puntosRuido', 0)}")
            
        else:
            print(f"❌ Error en clustering: {response.status_code}")
            print(f"   Respuesta: {response.text}")
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def main():
    print("🧪 Iniciando pruebas de clustering DBSCAN")
    print("=" * 50)
    
    # Generar datos de prueba
    detecciones = generar_datos_prueba()
    print(f"📋 Generadas {len(detecciones)} detecciones de prueba")
    
    # Limpiar datos existentes
    limpiar_datos()
    
    # Subir datos de prueba
    exitosos = subir_datos(detecciones)
    
    if exitosos > 0:
        # Probar clustering
        probar_clustering()
    else:
        print("❌ No se pudieron subir datos, saltando pruebas de clustering")
    
    print("\n" + "=" * 50)
    print("🏁 Pruebas completadas")

if __name__ == "__main__":
    main()
