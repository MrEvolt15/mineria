#!/usr/bin/env python3
"""
Script simplificado de DBSCAN para integración con la API Java
Recibe datos JSON por stdin y retorna resultados por stdout
"""

import sys
import json
import numpy as np
from sklearn.cluster import DBSCAN
from sklearn.preprocessing import StandardScaler

def aplicar_dbscan_simple(detecciones, eps=0.5, min_samples=3):
    """
    Aplica DBSCAN a los datos de detección y retorna resultados estructurados
    """
    if not detecciones:
        return {"clusters": [], "resultados": []}
    
    # Preparar datos
    puntos = []
    metadatos = []
    
    for deteccion in detecciones:
        coordenadas = deteccion.get('coordenadas', [])
        for coord in coordenadas:
            if 'x' in coord and 'y' in coord:
                puntos.append([coord['x'], coord['y']])
                metadatos.append({
                    'id': deteccion.get('id', ''),
                    'timestamp': deteccion.get('timestamp', ''),
                    'x': coord['x'],
                    'y': coord['y']
                })
    
    if not puntos:
        return {"clusters": [], "resultados": []}
    
    # Análisis de los datos originales
    puntos_array = np.array(puntos)
    stats_originales = {
        'total_puntos': len(puntos),
        'rango_x': (float(np.min(puntos_array[:, 0])), float(np.max(puntos_array[:, 0]))),
        'rango_y': (float(np.min(puntos_array[:, 1])), float(np.max(puntos_array[:, 1]))),
        'std_x': float(np.std(puntos_array[:, 0])),
        'std_y': float(np.std(puntos_array[:, 1]))
    }
    
    # Determinar si necesitamos normalización basado en la varianza
    usar_normalizacion = (stats_originales['std_x'] > 100 or stats_originales['std_y'] > 100 or
                         abs(stats_originales['std_x'] - stats_originales['std_y']) > 50)
    
    if usar_normalizacion:
        scaler = StandardScaler()
        puntos_normalizados = scaler.fit_transform(puntos_array)
    else:
        puntos_normalizados = puntos_array
        # Ajustar eps para datos no normalizados
        if eps < 1.0:  # Si eps es muy pequeño para datos sin normalizar
            eps = min(stats_originales['std_x'], stats_originales['std_y']) * 0.5
    
    # Aplicar DBSCAN
    dbscan = DBSCAN(eps=eps, min_samples=min_samples)
    labels = dbscan.fit_predict(puntos_normalizados)
    
    # Analizar resultados
    clusters_info = {}
    resultados = []
    
    # Procesar cada punto
    for i, (punto, label, meta) in enumerate(zip(puntos_array, labels, metadatos)):
        resultado = {
            'deteccionId': meta['id'],
            'timestamp': meta['timestamp'],
            'x': float(punto[0]),
            'y': float(punto[1]),
            'clusterId': int(label) if label != -1 else None,
            'esRuido': bool(label == -1)  # Convertir a bool nativo de Python
        }
        
        # Agregar info del cluster si no es ruido
        if label != -1:
            if label not in clusters_info:
                clusters_info[label] = {
                    'puntos': [],
                    'centroide_x': 0.0,
                    'centroide_y': 0.0,
                    'densidad': 0.0
                }
            clusters_info[label]['puntos'].append([punto[0], punto[1]])
        
        resultados.append(resultado)
    
    # Calcular estadísticas de clusters
    clusters_final = []
    for cluster_id, info in clusters_info.items():
        puntos_cluster = np.array(info['puntos'])
        centroide = np.mean(puntos_cluster, axis=0)
        
        # Calcular densidad simplificada (puntos por área aproximada)
        if len(puntos_cluster) > 2:
            min_coords = np.min(puntos_cluster, axis=0)
            max_coords = np.max(puntos_cluster, axis=0)
            area = max((max_coords[0] - min_coords[0]) * (max_coords[1] - min_coords[1]), 1.0)
            densidad = len(puntos_cluster) / area
        else:
            densidad = 1.0
        
        clusters_final.append({
            'clusterId': int(cluster_id),
            'tamaño': len(puntos_cluster),
            'centroideX': float(centroide[0]),
            'centroideY': float(centroide[1]),
            'densidad': float(densidad)
        })
    
    # Actualizar resultados con info de clusters
    cluster_map = {c['clusterId']: c for c in clusters_final}
    for resultado in resultados:
        if not resultado['esRuido'] and resultado['clusterId'] is not None:
            cluster_info = cluster_map.get(resultado['clusterId'])
            if cluster_info:
                resultado['puntosEnCluster'] = cluster_info['tamaño']
                resultado['centroideX'] = cluster_info['centroideX']
                resultado['centroideY'] = cluster_info['centroideY']
                resultado['densidadCluster'] = cluster_info['densidad']
        else:
            resultado['puntosEnCluster'] = 0
            resultado['centroideX'] = None
            resultado['centroideY'] = None
            resultado['densidadCluster'] = 0.0
    
    return {
        "clusters": clusters_final,
        "resultados": resultados,
        "estadisticas": {
            "totalPuntos": len(puntos),
            "totalClusters": len(clusters_final),
            "puntosRuido": sum(1 for r in resultados if r['esRuido'])
        },
        "diagnostico": {
            "datosOriginales": stats_originales,
            "normalizacionUsada": usar_normalizacion,
            "parametrosDBSCAN": {
                "eps": eps,
                "min_samples": min_samples
            },
            "distribucionClusters": [
                {"clusterId": int(label), "count": int(np.sum(labels == label))} 
                for label in np.unique(labels)
            ]
        }
    }

def main():
    try:
        # Leer datos desde stdin
        input_data = sys.stdin.read()
        datos = json.loads(input_data)
        
        # Obtener parámetros
        detecciones = datos.get('detecciones', [])
        eps = datos.get('eps', 0.5)
        min_samples = datos.get('minSamples', 3)
        
        # Aplicar DBSCAN
        resultado = aplicar_dbscan_simple(detecciones, eps, min_samples)
        
        # Retornar resultado como JSON
        print(json.dumps(resultado, ensure_ascii=False, indent=2))
        
    except Exception as e:
        # En caso de error, retornar estructura de error
        error_result = {
            "error": True,
            "mensaje": str(e),
            "clusters": [],
            "resultados": []
        }
        print(json.dumps(error_result, ensure_ascii=False))
        sys.exit(1)

if __name__ == "__main__":
    main()
