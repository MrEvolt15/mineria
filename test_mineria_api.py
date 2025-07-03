# test_mineria_api.py
import requests
import json
from datetime import datetime

# URL base de tu API
BASE_URL = "http://localhost:8080/api/realtime"

def test_single_detection():
    """Prueba envío de una detección simple"""
    url = f"{BASE_URL}/camera-data"
    
    data = {
        "ncam": 1,
        "time": "2025-07-03T14:30:00",
        "posX": 123.45,
        "posY": 67.89,
        "id": 95,
        "tipoPersona": "E",
        "genero": "M"
    }
    
    response = requests.post(url, json=data)
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text}")

def test_minimal_data():
    """Prueba con datos mínimos"""
    url = f"{BASE_URL}/camera-data"
    
    data = {
        "ncam": 2,
        "time": "2025-07-03T14:31:00",
        "posX": 200.10,
        "posY": 150.20,
        "id": 88
    }
    
    response = requests.post(url, json=data)
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text}")

def test_batch_data():
    """Prueba envío por lotes"""
    url = f"{BASE_URL}/camera-data/batch"
    
    data = [
        {
            "ncam": 1,
            "time": "2025-07-03T14:32:00",
            "posX": 300.75,
            "posY": 250.50,
            "id": 92,
            "tipoPersona": "PR",
            "genero": "F"
        },
        {
            "ncam": 3,
            "time": "2025-07-03T14:33:00",
            "posX": 400.25,
            "posY": 350.75,
            "id": 87
        }
    ]
    
    response = requests.post(url, json=data)
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text}")

def test_heatmap_data():
    """Prueba obtención de datos para mapa de calor"""
    url = f"{BASE_URL}/heatmap-data"
    
    response = requests.get(url)
    print(f"Status: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        print(f"Registros obtenidos: {len(data)}")
        if data:
            print("Primer registro:", json.dumps(data[0], indent=2))
    else:
        print(f"Error: {response.text}")

def simulate_camera_stream():
    """Simula flujo continuo de cámara"""
    import time
    import random
    
    url = f"{BASE_URL}/camera-data"
    
    for i in range(10):
        data = {
            "ncam": random.randint(1, 5),
            "time": datetime.now().isoformat(),
            "posX": round(random.uniform(100, 500), 2),
            "posY": round(random.uniform(50, 300), 2),
            "id": random.randint(70, 99),
            "tipoPersona": random.choice(["E", "PR", "PA"]),
            "genero": random.choice(["M", "F"])
        }
        
        response = requests.post(url, json=data)
        print(f"Detección {i+1}: Status {response.status_code}")
        time.sleep(1)  # Esperar 1 segundo entre envíos

if __name__ == "__main__":
    print("=== Pruebas API Minería ===")
    
    print("\n1. Prueba detección completa:")
    test_single_detection()
    
    print("\n2. Prueba datos mínimos:")
    test_minimal_data()
    
    print("\n3. Prueba por lotes:")
    test_batch_data()
    
    print("\n4. Prueba datos mapa de calor:")
    test_heatmap_data()
    
    print("\n5. Simulación flujo de cámara:")
    simulate_camera_stream()
