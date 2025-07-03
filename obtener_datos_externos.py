#!/usr/bin/env python3
"""
Script para obtener datos de detección desde una URL externa
y enviarlos a la base de datos del módulo de detecciones
"""

import requests
import json
import time
import argparse
from datetime import datetime

# Configuración
BASE_URL = "http://localhost:8080/api"
EXTERNAL_URL = "https://f61b-45-162-74-17.ngrok-free.app/ver"
HEADERS = {"Content-Type": "application/json"}
NGROK_HEADERS = {
    "Content-Type": "application/json",
    "ngrok-skip-browser-warning": "true"
}

def obtener_datos_externos():
    """Obtiene datos de la URL externa"""
    print(f"🌐 Consultando URL externa: {EXTERNAL_URL}")
    
    try:
        response = requests.get(EXTERNAL_URL, headers=NGROK_HEADERS, timeout=10)
        
        if response.status_code != 200:
            print(f"❌ Error HTTP {response.status_code}")
            print(f"Response: {response.text}")
            return None
        
        try:
            datos = response.json()
            print(f"📥 Datos recibidos exitosamente")
            return datos
        except json.JSONDecodeError:
            print("❌ La respuesta no es un JSON válido")
            print(f"Contenido: {response.text[:200]}...")
            return None
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Error de conexión: {e}")
        return None

def validar_deteccion(deteccion, numero=None):
    """Valida que una detección tenga la estructura correcta"""
    if not deteccion:
        print(f"⚠️  Detección {numero or ''} está vacía")
        return False
    
    if not isinstance(deteccion, dict):
        print(f"⚠️  Detección {numero or ''} no es un objeto válido")
        return False
    
    # Verificar campos obligatorios
    if not deteccion.get('timestamp'):
        print(f"⚠️  Detección {numero or ''} sin timestamp")
        return False
    
    if deteccion.get('personas') is None:
        print(f"⚠️  Detección {numero or ''} sin número de personas")
        return False
    
    if not deteccion.get('coordenadas'):
        print(f"⚠️  Detección {numero or ''} sin coordenadas")
        return False
    
    # Verificar consistencia entre personas y coordenadas
    num_personas = deteccion.get('personas', 0)
    coordenadas = deteccion.get('coordenadas', [])
    
    if len(coordenadas) != num_personas:
        print(f"⚠️  Detección {numero or ''}: {len(coordenadas)} coordenadas para {num_personas} personas")
        return False
    
    # Validar cada coordenada
    for i, coord in enumerate(coordenadas):
        if not isinstance(coord, dict):
            print(f"⚠️  Detección {numero or ''}: coordenada {i+1} no es válida")
            return False
        
        if 'x' not in coord or 'y' not in coord:
            print(f"⚠️  Detección {numero or ''}: coordenada {i+1} sin campos x,y")
            return False
        
        if coord['x'] is None or coord['y'] is None:
            print(f"⚠️  Detección {numero or ''}: coordenada {i+1} con valores nulos")
            return False
    
    return True

def enviar_deteccion(deteccion):
    """Envía una detección a la base de datos"""
    try:
        response = requests.post(
            f"{BASE_URL}/detecciones",
            headers=HEADERS,
            data=json.dumps(deteccion)
        )
        
        if response.status_code == 201:
            return True, "Guardada exitosamente"
        else:
            return False, f"Error {response.status_code}: {response.text}"
            
    except Exception as e:
        return False, f"Error de conexión: {e}"

def procesar_datos(datos_externos):
    """Procesa los datos externos y los envía a la base de datos"""
    if not datos_externos:
        print("⚠️  No hay datos para procesar")
        return 0
    
    # Control de datos vacíos
    if isinstance(datos_externos, list) and len(datos_externos) == 0:
        print("⚠️  Lista de detecciones vacía - no se procesará nada")
        return 0
    
    if isinstance(datos_externos, dict) and len(datos_externos) == 0:
        print("⚠️  Objeto de detección vacío - no se procesará nada")
        return 0
    
    detecciones_procesadas = 0
    
    # Si es una sola detección (dict)
    if isinstance(datos_externos, dict):
        print("📋 Procesando detección individual...")
        
        if validar_deteccion(datos_externos):
            exito, mensaje = enviar_deteccion(datos_externos)
            if exito:
                print(f"✅ Detección guardada: {datos_externos['timestamp']} ({datos_externos['personas']} personas)")
                detecciones_procesadas = 1
            else:
                print(f"❌ Error guardando detección: {mensaje}")
        else:
            print("❌ Detección no válida, no se guardará")
    
    # Si es una lista de detecciones
    elif isinstance(datos_externos, list):
        print(f"📋 Procesando lista de {len(datos_externos)} detecciones...")
        
        for i, deteccion in enumerate(datos_externos, 1):
            print(f"\n--- Procesando detección {i} ---")
            
            if validar_deteccion(deteccion, i):
                exito, mensaje = enviar_deteccion(deteccion)
                if exito:
                    print(f"✅ Detección {i} guardada: {deteccion['timestamp']} ({deteccion['personas']} personas)")
                    detecciones_procesadas += 1
                else:
                    print(f"❌ Error guardando detección {i}: {mensaje}")
            else:
                print(f"❌ Detección {i} no válida, saltando...")
    
    else:
        print("❌ Formato de datos no reconocido")
        return 0
    
    return detecciones_procesadas

def verificar_servidor():
    """Verifica que el servidor esté funcionando"""
    try:
        response = requests.get(f"{BASE_URL}/detecciones/ejemplo", timeout=5)
        return response.status_code == 200
    except:
        return False

def monitorear_continuo(intervalo=10, max_iteraciones=None):
    """Monitorea la URL externa continuamente"""
    print(f"🔄 Iniciando monitoreo continuo cada {intervalo} segundos")
    if max_iteraciones:
        print(f"📊 Máximo {max_iteraciones} iteraciones")
    else:
        print("🔄 Presione Ctrl+C para detener")
    
    iteracion = 0
    try:
        while True:
            iteracion += 1
            print(f"\n{'='*50}")
            print(f"🔄 Iteración {iteracion} - {datetime.now().strftime('%H:%M:%S')}")
            print('='*50)
            
            datos = obtener_datos_externos()
            if datos:
                procesadas = procesar_datos(datos)
                print(f"✅ Detecciones procesadas en esta iteración: {procesadas}")
            else:
                print("❌ No se obtuvieron datos en esta iteración")
            
            if max_iteraciones and iteracion >= max_iteraciones:
                print(f"\n✅ Completadas {max_iteraciones} iteraciones")
                break
            
            print(f"\n⏱️  Esperando {intervalo} segundos...")
            time.sleep(intervalo)
            
    except KeyboardInterrupt:
        print(f"\n🛑 Monitoreo detenido por el usuario después de {iteracion} iteraciones")

def main():
    parser = argparse.ArgumentParser(description='Obtener datos de detección desde URL externa')
    parser.add_argument('--monitorear', '-m', action='store_true', 
                       help='Monitorear la URL continuamente')
    parser.add_argument('--intervalo', '-i', type=int, default=10,
                       help='Intervalo en segundos para monitoreo (default: 10)')
    parser.add_argument('--iteraciones', '-n', type=int,
                       help='Número máximo de iteraciones (default: infinito)')
    parser.add_argument('--url', '-u', 
                       help='URL externa personalizada')
    
    args = parser.parse_args()
    
    # Usar URL personalizada si se proporciona
    global EXTERNAL_URL
    if args.url:
        EXTERNAL_URL = args.url
    
    print("🚀 Script de obtención de datos externos")
    print(f"📡 URL del servidor: {BASE_URL}")
    print(f"🌐 URL externa: {EXTERNAL_URL}")
    
    # Verificar conectividad con el servidor
    if not verificar_servidor():
        print("❌ No se puede conectar al servidor Spring Boot")
        print("Asegúrese de que esté ejecutándose en el puerto 8080")
        return
    
    print("✅ Conexión con el servidor verificada\n")
    
    if args.monitorear:
        # Modo monitoreo continuo
        monitorear_continuo(args.intervalo, args.iteraciones)
    else:
        # Modo una sola consulta
        print("📋 Obteniendo datos una sola vez...")
        datos = obtener_datos_externos()
        
        if datos:
            print(f"\n📊 Datos recibidos: {json.dumps(datos, indent=2, ensure_ascii=False)[:300]}...")
            procesadas = procesar_datos(datos)
            print(f"\n✅ Total de detecciones procesadas: {procesadas}")
        else:
            print("❌ No se pudieron obtener datos")

if __name__ == "__main__":
    main()
