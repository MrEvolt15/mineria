#!/usr/bin/env python3
"""
DBSCAN clustering aplicado a datos de detección de personas
Analiza las coordenadas para identificar grupos y patrones espaciales
"""

import requests
import json
import numpy as np
from sklearn.cluster import DBSCAN
from sklearn.preprocessing import StandardScaler
import matplotlib.pyplot as plt
from datetime import datetime

# Configuración
BASE_URL = "http://localhost:8080/api"
HEADERS = {"Content-Type": "application/json"}

def obtener_datos_deteccion():
    """Obtiene todos los datos de detección desde la API"""
    try:
        response = requests.get(f"{BASE_URL}/detecciones/todas", timeout=10)
        if response.status_code == 200:
            data = response.json()
            return data.get('detecciones', [])
        else:
            print(f"Error obteniendo datos: {response.status_code}")
            return []
    except Exception as e:
        print(f"Error de conexión: {e}")
        return []

def preparar_datos_para_clustering(detecciones):
    """
    Prepara los datos en el formato necesario para DBSCAN
    Extrae todas las coordenadas con metadata asociada
    """
    puntos = []
    metadata = []
    
    for i, deteccion in enumerate(detecciones):
        timestamp = deteccion.get('timestamp', '')
        personas = deteccion.get('personas', 0)
        coordenadas = deteccion.get('coordenadas', [])
        
        for j, coord in enumerate(coordenadas):
            if 'x' in coord and 'y' in coord:
                # Punto para clustering (solo x, y)
                puntos.append([coord['x'], coord['y']])
                
                # Metadata para análisis posterior
                metadata.append({
                    'deteccion_id': i,
                    'persona_id': j,
                    'timestamp': timestamp,
                    'total_personas': personas,
                    'x': coord['x'],
                    'y': coord['y']
                })
    
    return np.array(puntos), metadata

def aplicar_dbscan(puntos, eps=50.0, min_samples=3):
    """
    Aplica DBSCAN a los puntos de coordenadas
    
    Args:
        puntos: Array numpy con coordenadas [x, y]
        eps: Distancia máxima entre puntos para ser considerados vecinos
        min_samples: Número mínimo de puntos para formar un cluster
    """
    if len(puntos) == 0:
        return [], []
    
    # Normalizar datos para mejor clustering
    scaler = StandardScaler()
    puntos_normalizados = scaler.fit_transform(puntos)
    
    # Aplicar DBSCAN
    dbscan = DBSCAN(eps=eps, min_samples=min_samples)
    labels = dbscan.fit_predict(puntos_normalizados)
    
    return labels, scaler

def analizar_clusters(puntos, labels, metadata):
    """
    Analiza los clusters generados y crea estructura final de datos
    """
    clusters = {}
    ruido = []
    
    # Procesar cada punto
    for i, (punto, label, meta) in enumerate(zip(puntos, labels, metadata)):
        if label == -1:
            # Punto de ruido (outlier)
            ruido.append({
                'punto_id': i,
                'coordenada': {'x': float(punto[0]), 'y': float(punto[1])},
                'timestamp': meta['timestamp'],
                'deteccion_id': meta['deteccion_id'],
                'persona_id': meta['persona_id'],
                'tipo': 'ruido'
            })
        else:
            # Punto pertenece a un cluster
            if label not in clusters:
                clusters[label] = {
                    'cluster_id': int(label),
                    'puntos': [],
                    'centroide': {'x': 0.0, 'y': 0.0},
                    'tamaño': 0,
                    'timestamps': set(),
                    'densidad': 0.0,
                    'bbox': {'min_x': float('inf'), 'max_x': float('-inf'), 
                            'min_y': float('inf'), 'max_y': float('-inf')}
                }
            
            # Agregar punto al cluster
            clusters[label]['puntos'].append({
                'punto_id': i,
                'coordenada': {'x': float(punto[0]), 'y': float(punto[1])},
                'timestamp': meta['timestamp'],
                'deteccion_id': meta['deteccion_id'],
                'persona_id': meta['persona_id']
            })
            
            # Actualizar timestamps únicos
            clusters[label]['timestamps'].add(meta['timestamp'])
            
            # Actualizar bounding box
            bbox = clusters[label]['bbox']
            bbox['min_x'] = min(bbox['min_x'], punto[0])
            bbox['max_x'] = max(bbox['max_x'], punto[0])
            bbox['min_y'] = min(bbox['min_y'], punto[1])
            bbox['max_y'] = max(bbox['max_y'], punto[1])
    
    # Calcular estadísticas finales para cada cluster
    for label, cluster in clusters.items():
        puntos_cluster = np.array([[p['coordenada']['x'], p['coordenada']['y']] 
                                  for p in cluster['puntos']])
        
        # Centroide
        centroide = np.mean(puntos_cluster, axis=0)
        cluster['centroide'] = {'x': float(centroide[0]), 'y': float(centroide[1])}
        
        # Tamaño
        cluster['tamaño'] = len(cluster['puntos'])
        
        # Densidad (puntos por área del bounding box)
        bbox = cluster['bbox']
        area = (bbox['max_x'] - bbox['min_x']) * (bbox['max_y'] - bbox['min_y'])
        cluster['densidad'] = cluster['tamaño'] / max(area, 1.0)
        
        # Convertir timestamps set a lista
        cluster['timestamps_unicos'] = sorted(list(cluster['timestamps']))
        cluster['num_timestamps'] = len(cluster['timestamps'])
        del cluster['timestamps']  # Remover el set
    
    return clusters, ruido

def generar_estadisticas_globales(clusters, ruido, puntos_totales):
    """Genera estadísticas globales del análisis DBSCAN"""
    num_clusters = len(clusters)
    puntos_en_clusters = sum(cluster['tamaño'] for cluster in clusters.values())
    puntos_ruido = len(ruido)
    
    # Cluster más grande y más denso
    cluster_mas_grande = max(clusters.values(), key=lambda c: c['tamaño']) if clusters else None
    cluster_mas_denso = max(clusters.values(), key=lambda c: c['densidad']) if clusters else None
    
    return {
        'total_puntos_analizados': puntos_totales,
        'num_clusters_encontrados': num_clusters,
        'puntos_en_clusters': puntos_en_clusters,
        'puntos_ruido': puntos_ruido,
        'porcentaje_clustering': (puntos_en_clusters / puntos_totales * 100) if puntos_totales > 0 else 0,
        'cluster_mas_grande': {
            'id': cluster_mas_grande['cluster_id'],
            'tamaño': cluster_mas_grande['tamaño'],
            'centroide': cluster_mas_grande['centroide']
        } if cluster_mas_grande else None,
        'cluster_mas_denso': {
            'id': cluster_mas_denso['cluster_id'],
            'densidad': cluster_mas_denso['densidad'],
            'centroide': cluster_mas_denso['centroide']
        } if cluster_mas_denso else None
    }

def ejecutar_dbscan_completo(eps=50.0, min_samples=3):
    """
    Función principal que ejecuta todo el proceso DBSCAN
    
    Estructura final de datos:
    {
        "analisis_dbscan": {
            "parametros": {
                "eps": 50.0,
                "min_samples": 3,
                "fecha_analisis": "2025-07-06T14:30:00"
            },
            "estadisticas_globales": {
                "total_puntos_analizados": 150,
                "num_clusters_encontrados": 5,
                "puntos_en_clusters": 135,
                "puntos_ruido": 15,
                "porcentaje_clustering": 90.0,
                "cluster_mas_grande": {
                    "id": 2,
                    "tamaño": 45,
                    "centroide": {"x": 450.5, "y": 320.8}
                },
                "cluster_mas_denso": {
                    "id": 1,
                    "densidad": 0.85,
                    "centroide": {"x": 200.3, "y": 150.7}
                }
            },
            "clusters": {
                "0": {
                    "cluster_id": 0,
                    "centroide": {"x": 300.5, "y": 200.8},
                    "tamaño": 25,
                    "densidad": 0.45,
                    "bbox": {"min_x": 250.0, "max_x": 350.0, "min_y": 150.0, "max_y": 250.0},
                    "num_timestamps": 8,
                    "timestamps_unicos": ["2025-07-06T08:00:00", "2025-07-06T09:15:00", ...],
                    "puntos": [
                        {
                            "punto_id": 0,
                            "coordenada": {"x": 305.2, "y": 198.7},
                            "timestamp": "2025-07-06T08:00:00",
                            "deteccion_id": 5,
                            "persona_id": 1
                        },
                        ...
                    ]
                },
                "1": { ... },
                ...
            },
            "ruido": [
                {
                    "punto_id": 120,
                    "coordenada": {"x": 50.5, "y": 950.2},
                    "timestamp": "2025-07-06T22:30:00",
                    "deteccion_id": 89,
                    "persona_id": 0,
                    "tipo": "ruido"
                },
                ...
            ]
        }
    }
    """
    
    print("🔍 Iniciando análisis DBSCAN...")
    
    # 1. Obtener datos
    detecciones = obtener_datos_deteccion()
    if not detecciones:
        print("❌ No se encontraron datos para analizar")
        return None
    
    print(f"📊 Detecciones obtenidas: {len(detecciones)}")
    
    # 2. Preparar datos
    puntos, metadata = preparar_datos_para_clustering(detecciones)
    if len(puntos) == 0:
        print("❌ No se encontraron coordenadas válidas")
        return None
    
    print(f"📍 Puntos extraídos: {len(puntos)}")
    
    # 3. Aplicar DBSCAN
    labels, scaler = aplicar_dbscan(puntos, eps=eps, min_samples=min_samples)
    
    # 4. Analizar resultados
    clusters, ruido = analizar_clusters(puntos, labels, metadata)
    
    # 5. Generar estadísticas
    stats = generar_estadisticas_globales(clusters, ruido, len(puntos))
    
    # 6. Crear estructura final
    resultado = {
        "analisis_dbscan": {
            "parametros": {
                "eps": eps,
                "min_samples": min_samples,
                "fecha_analisis": datetime.now().strftime("%Y-%m-%dT%H:%M:%S")
            },
            "estadisticas_globales": stats,
            "clusters": clusters,
            "ruido": ruido
        }
    }
    
    # 7. Mostrar resumen
    print(f"\n✅ Análisis DBSCAN completado:")
    print(f"   🎯 Clusters encontrados: {stats['num_clusters_encontrados']}")
    print(f"   📊 Puntos en clusters: {stats['puntos_en_clusters']}")
    print(f"   🔴 Puntos de ruido: {stats['puntos_ruido']}")
    print(f"   📈 Porcentaje agrupado: {stats['porcentaje_clustering']:.1f}%")
    
    if stats['cluster_mas_grande']:
        print(f"   🏆 Cluster más grande: #{stats['cluster_mas_grande']['id']} ({stats['cluster_mas_grande']['tamaño']} puntos)")
    
    return resultado

def guardar_resultados_json(resultado, archivo="dbscan_resultados.json"):
    """Guarda los resultados en un archivo JSON"""
    try:
        with open(archivo, 'w', encoding='utf-8') as f:
            json.dump(resultado, f, indent=2, ensure_ascii=False)
        print(f"💾 Resultados guardados en: {archivo}")
    except Exception as e:
        print(f"❌ Error guardando archivo: {e}")

if __name__ == "__main__":
    # Ejecutar análisis DBSCAN con parámetros por defecto
    resultado = ejecutar_dbscan_completo(eps=50.0, min_samples=3)
    
    if resultado:
        # Guardar resultados
        guardar_resultados_json(resultado)
        
        # Mostrar algunos clusters como ejemplo
        clusters = resultado["analisis_dbscan"]["clusters"]
        if clusters:
            print(f"\n📋 Ejemplo de clusters encontrados:")
            for cluster_id, cluster_data in list(clusters.items())[:3]:  # Mostrar primeros 3
                print(f"   Cluster #{cluster_id}:")
                print(f"     • Centroide: ({cluster_data['centroide']['x']:.1f}, {cluster_data['centroide']['y']:.1f})")
                print(f"     • Tamaño: {cluster_data['tamaño']} puntos")
                print(f"     • Densidad: {cluster_data['densidad']:.3f}")
                print(f"     • Timestamps únicos: {cluster_data['num_timestamps']}")
    else:
        print("❌ No se pudo completar el análisis DBSCAN")