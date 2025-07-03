#!/usr/bin/env python3
"""
Script de prueba para el nuevo módulo de detecciones
Prueba todos los endpoints del DeteccionController
"""

import requests
import json
import time
from datetime import datetime, timedelta

# Configuración
BASE_URL = "http://localhost:8080/api"
EXTERNAL_URL = "https://f61b-45-162-74-17.ngrok-free.app/ver"
HEADERS = {"Content-Type": "application/json"}
NGROK_HEADERS = {
    "Content-Type": "application/json",
    "ngrok-skip-browser-warning": "true"
}

def print_separator(title):
    print("\n" + "="*60)
    print(f" {title}")
    print("="*60)

def test_ejemplo():
    """Prueba el endpoint de ejemplo"""
    print("📋 Obteniendo ejemplo del formato...")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones/ejemplo")
        if response.status_code == 200:
            data = response.json()
            print("✅ Ejemplo obtenido:")
            print(json.dumps(data, indent=2, ensure_ascii=False))
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def test_crear_detecciones():
    """Crea varias detecciones de prueba"""
    print("📝 Creando detecciones de prueba...")
    
    # Crear detecciones con diferentes timestamps
    base_time = datetime.now()
    detecciones = []
    
    for i in range(100):
        timestamp = base_time + timedelta(seconds=i)
        timestamp_str = timestamp.strftime("%Y-%m-%dT%H:%M:%S")
        
        num_personas = i + 1
        coordenadas = []
        
        for j in range(num_personas):
            coordenadas.append({
                "x": 100.0 + (j * 50) + (i * 10),
                "y": 200.0 + (j * 30) + (i * 5)
            })
        
        deteccion = {
            "timestamp": timestamp_str,
            "personas": num_personas,
            "coordenadas": coordenadas
        }
        
        detecciones.append(deteccion)
        
        try:
            response = requests.post(
                f"{BASE_URL}/detecciones",
                headers=HEADERS,
                data=json.dumps(deteccion)
            )
            
            if response.status_code == 201:
                print(f"✅ Detección {i+1} creada: {timestamp_str} ({num_personas} personas)")
            else:
                print(f"❌ Error creando detección {i+1}: {response.status_code} - {response.text}")
                
        except Exception as e:
            print(f"❌ Error de conexión creando detección {i+1}: {e}")
    
    return detecciones

def test_buscar_por_segundo(detecciones):
    """Prueba la búsqueda por segundo específico"""
    print("🔍 Probando búsqueda por segundo específico...")
    
    if not detecciones:
        print("❌ No hay detecciones para probar")
        return
    
    # Usar el timestamp de la primera detección
    timestamp = detecciones[0]["timestamp"]
    print(f"Buscando detecciones en el segundo: {timestamp}")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones?segundo={timestamp}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Encontradas {data['total_encontradas']} detecciones:")
            for deteccion in data['detecciones']:
                print(f"   - {deteccion['timestamp']}: {deteccion['personas']} personas")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def test_buscar_por_rango(detecciones):
    """Prueba la búsqueda por rango de tiempo"""
    print("🔍 Probando búsqueda por rango de tiempo...")
    
    if len(detecciones) < 2:
        print("❌ No hay suficientes detecciones para probar rango")
        return
    
    # Usar un rango que incluya varias detecciones
    timestamp_inicio = detecciones[0]["timestamp"]
    timestamp_fin = detecciones[-1]["timestamp"]
    rango = f"{timestamp_inicio},{timestamp_fin}"
    
    print(f"Buscando detecciones en el rango: {rango}")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones?rango={rango}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Encontradas {data['total_encontradas']} detecciones:")
            for deteccion in data['detecciones']:
                print(f"   - {deteccion['timestamp']}: {deteccion['personas']} personas")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def test_obtener_todas():
    """Prueba obtener todas las detecciones"""
    print("📊 Obteniendo todas las detecciones...")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones/todas")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Total de detecciones: {data['total']}")
            print("Primeras detecciones:")
            for i, deteccion in enumerate(data['detecciones'][:3]):
                print(f"   {i+1}. {deteccion['timestamp']}: {deteccion['personas']} personas")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def test_obtener_ultimas():
    """Prueba obtener las últimas detecciones"""
    print("📊 Obteniendo las últimas 3 detecciones...")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones/ultimas?limite=3")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Últimas {data['total_encontradas']} detecciones:")
            for deteccion in data['detecciones']:
                print(f"   - {deteccion['timestamp']}: {deteccion['personas']} personas")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def test_estadisticas():
    """Prueba obtener estadísticas"""
    print("📈 Obteniendo estadísticas...")
    
    try:
        response = requests.get(f"{BASE_URL}/detecciones/estadisticas")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Total de detecciones en la base de datos: {data['total_detecciones']}")
            print("Últimas 5 detecciones:")
            for deteccion in data['ultimas_5_detecciones']:
                print(f"   - {deteccion['timestamp']}: {deteccion['personas']} personas")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def test_errores():
    """Prueba casos de error"""
    print("⚠️  Probando casos de error...")
    
    # Formato de timestamp incorrecto
    try:
        response = requests.get(f"{BASE_URL}/detecciones?segundo=formato-incorrecto")
        if response.status_code == 400:
            print("✅ Error de formato de timestamp manejado correctamente")
        else:
            print(f"❌ Se esperaba error 400, recibido: {response.status_code}")
    except Exception as e:
        print(f"❌ Error de conexión: {e}")
    
    # Ambos parámetros a la vez
    try:
        response = requests.get(f"{BASE_URL}/detecciones?segundo=2025-01-01T10:00:00&rango=2025-01-01T10:00:00,2025-01-01T11:00:00")
        if response.status_code == 400:
            print("✅ Error de parámetros múltiples manejado correctamente")
        else:
            print(f"❌ Se esperaba error 400, recibido: {response.status_code}")
    except Exception as e:
        print(f"❌ Error de conexión: {e}")
    
    # Detección inválida
    deteccion_invalida = {
        "timestamp": "2025-01-01T10:00:00",
        "personas": 2,
        "coordenadas": [{"x": 100, "y": 200}]  # Solo 1 coordenada para 2 personas
    }
    
    try:
        response = requests.post(
            f"{BASE_URL}/detecciones",
            headers=HEADERS,
            data=json.dumps(deteccion_invalida)
        )
        if response.status_code == 400:
            print("✅ Error de validación de detección manejado correctamente")
        else:
            print(f"❌ Se esperaba error 400, recibido: {response.status_code}")
    except Exception as e:
        print(f"❌ Error de conexión: {e}")

def obtener_y_guardar_datos_externos():
    """Obtiene datos de la URL externa y los guarda en la base de datos"""
    print("🌐 Obteniendo datos de la URL externa...")
    print(f"📡 URL externa: {EXTERNAL_URL}")
    
    try:
        # Hacer petición GET a la URL externa
        response = requests.get(EXTERNAL_URL, headers=NGROK_HEADERS, timeout=10)
        
        if response.status_code != 200:
            print(f"❌ Error al obtener datos externos: {response.status_code}")
            print(f"Response: {response.text}")
            return
        
        # Intentar parsear la respuesta como JSON
        try:
            datos_externos = response.json()
        except json.JSONDecodeError:
            print("❌ La respuesta no es un JSON válido")
            print(f"Contenido recibido: {response.text[:200]}...")
            return
        
        print(f"📥 Datos recibidos: {json.dumps(datos_externos, indent=2, ensure_ascii=False)}")
        
        # Verificar si los datos están vacíos o son None
        if not datos_externos:
            print("⚠️  Los datos recibidos están vacíos, no se guardará nada")
            return
        
        # Si es una lista, verificar que no esté vacía
        if isinstance(datos_externos, list) and len(datos_externos) == 0:
            print("⚠️  La lista de detecciones está vacía, no se guardará nada")
            return
        
        # Si es un diccionario, verificar que tenga los campos necesarios
        if isinstance(datos_externos, dict):
            if not datos_externos.get('timestamp') or not datos_externos.get('personas') or not datos_externos.get('coordenadas'):
                print("⚠️  Los datos no tienen la estructura esperada (timestamp, personas, coordenadas)")
                print(f"Campos recibidos: {list(datos_externos.keys())}")
                return
            
            # Procesar una sola detección
            procesar_deteccion_individual(datos_externos)
        
        # Si es una lista de detecciones
        elif isinstance(datos_externos, list):
            detecciones_procesadas = 0
            for i, deteccion in enumerate(datos_externos):
                if not deteccion or not isinstance(deteccion, dict):
                    print(f"⚠️  Detección {i+1} está vacía o no es válida, saltando...")
                    continue
                
                if not deteccion.get('timestamp') or not deteccion.get('personas') or not deteccion.get('coordenadas'):
                    print(f"⚠️  Detección {i+1} no tiene la estructura esperada, saltando...")
                    continue
                
                if procesar_deteccion_individual(deteccion, i+1):
                    detecciones_procesadas += 1
            
            print(f"✅ Total de detecciones procesadas: {detecciones_procesadas}")
        
        else:
            print("❌ Formato de datos no reconocido. Se esperaba dict o list")
            return
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Error de conexión con la URL externa: {e}")
    except Exception as e:
        print(f"❌ Error inesperado: {e}")

def procesar_deteccion_individual(deteccion, numero=None):
    """Procesa y guarda una detección individual"""
    try:
        # Validar estructura básica
        if not deteccion.get('timestamp'):
            print(f"⚠️  Detección {numero or ''} sin timestamp, saltando...")
            return False
        
        if deteccion.get('personas') is None:
            print(f"⚠️  Detección {numero or ''} sin número de personas, saltando...")
            return False
        
        if not deteccion.get('coordenadas'):
            print(f"⚠️  Detección {numero or ''} sin coordenadas, saltando...")
            return False
        
        # Verificar que el número de coordenadas coincida con el número de personas
        num_personas = deteccion.get('personas', 0)
        coordenadas = deteccion.get('coordenadas', [])
        
        if len(coordenadas) != num_personas:
            print(f"⚠️  Detección {numero or ''}: número de coordenadas ({len(coordenadas)}) no coincide con personas ({num_personas}), saltando...")
            return False
        
        # Validar coordenadas
        for i, coord in enumerate(coordenadas):
            if not isinstance(coord, dict) or 'x' not in coord or 'y' not in coord:
                print(f"⚠️  Detección {numero or ''}: coordenada {i+1} inválida, saltando...")
                return False
            
            if coord['x'] is None or coord['y'] is None:
                print(f"⚠️  Detección {numero or ''}: coordenada {i+1} tiene valores nulos, saltando...")
                return False
        
        # Preparar la detección para enviar
        deteccion_formateada = {
            "timestamp": deteccion['timestamp'],
            "personas": num_personas,
            "coordenadas": coordenadas
        }
        
        # Enviar a la base de datos
        response = requests.post(
            f"{BASE_URL}/detecciones",
            headers=HEADERS,
            data=json.dumps(deteccion_formateada)
        )
        
        if response.status_code == 201:
            resultado = response.json()
            if numero:
                print(f"✅ Detección {numero} guardada: {deteccion['timestamp']} ({num_personas} personas)")
            else:
                print(f"✅ Detección guardada: {deteccion['timestamp']} ({num_personas} personas)")
            return True
        else:
            error_msg = f"Error {response.status_code}: {response.text}"
            if numero:
                print(f"❌ Error guardando detección {numero}: {error_msg}")
            else:
                print(f"❌ Error guardando detección: {error_msg}")
            return False
            
    except Exception as e:
        if numero:
            print(f"❌ Error procesando detección {numero}: {e}")
        else:
            print(f"❌ Error procesando detección: {e}")
        return False

def monitorear_url_externa(intervalo_segundos=5, max_iteraciones=10):
    """Monitorea la URL externa cada X segundos y guarda los datos"""
    print(f"🔄 Iniciando monitoreo de URL externa cada {intervalo_segundos} segundos")
    print(f"📊 Máximo {max_iteraciones} iteraciones")
    
    for i in range(max_iteraciones):
        print(f"\n--- Iteración {i+1}/{max_iteraciones} ---")
        obtener_y_guardar_datos_externos()
        
        if i < max_iteraciones - 1:  # No esperar en la última iteración
            print(f"⏱️  Esperando {intervalo_segundos} segundos...")
            time.sleep(intervalo_segundos)
    
    print("\n✅ Monitoreo completado")

def main():
    print("🚀 Iniciando pruebas del módulo de detecciones")
    print(f"📡 URL base: {BASE_URL}")
    
    # Verificar que el servidor está funcionando
    try:
        response = requests.get(f"{BASE_URL}/detecciones/ejemplo")
        if response.status_code != 200:
            print("❌ El servidor no está respondiendo correctamente")
            print("Asegúrese de que el servidor Spring Boot está ejecutándose en el puerto 8080")
            return
    except Exception as e:
        print(f"❌ No se puede conectar al servidor: {e}")
        print("Asegúrese de que el servidor Spring Boot está ejecutándose en el puerto 8080")
        return
    
    print("✅ Servidor conectado exitosamente\n")
    
    # Ejecutar todas las pruebas
    print_separator("EJEMPLO DEL FORMATO")
    test_ejemplo()
    
    print_separator("CREACIÓN DE DETECCIONES")
    detecciones = test_crear_detecciones()
    
    print_separator("BÚSQUEDA POR SEGUNDO")
    test_buscar_por_segundo(detecciones)
    
    print_separator("BÚSQUEDA POR RANGO")
    test_buscar_por_rango(detecciones)
    
    print_separator("OBTENER TODAS LAS DETECCIONES")
    test_obtener_todas()
    
    print_separator("OBTENER ÚLTIMAS DETECCIONES")
    test_obtener_ultimas()
    
    print_separator("ESTADÍSTICAS")
    test_estadisticas()
    
    print_separator("PRUEBAS DE ERROR")
    test_errores()
    
    print_separator("MONITOREO DE URL EXTERNA")
    obtener_y_guardar_datos_externos()
    
    print_separator("RESUMEN")
    print("✅ Pruebas completadas")
    print("\n📝 Endpoints disponibles:")
    print("   POST /api/detecciones - Crear nueva detección")
    print("   GET /api/detecciones?segundo=YYYY-MM-DDTHH:mm:ss - Buscar por segundo")
    print("   GET /api/detecciones?rango=inicio,fin - Buscar por rango")
    print("   GET /api/detecciones/todas - Obtener todas las detecciones")
    print("   GET /api/detecciones/ultimas?limite=N - Obtener últimas N detecciones")
    print("   GET /api/detecciones/estadisticas - Obtener estadísticas")
    print("   GET /api/detecciones/ejemplo - Obtener formato de ejemplo")
    print("   DELETE /api/detecciones/limpiar - Limpiar todas las detecciones")

if __name__ == "__main__":
    main()
