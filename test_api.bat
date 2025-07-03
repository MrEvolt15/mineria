# test_api.bat - Script para Windows
@echo off
echo === Pruebas API Mineria ===

echo.
echo 1. Prueba deteccion completa:
curl -X POST http://localhost:8080/api/realtime/camera-data ^
  -H "Content-Type: application/json" ^
  -d "{\"ncam\": 1, \"time\": \"2025-07-03T14:30:00\", \"posX\": 123.45, \"posY\": 67.89, \"id\": 95, \"tipoPersona\": \"E\", \"genero\": \"M\"}"

echo.
echo.
echo 2. Prueba datos minimos:
curl -X POST http://localhost:8080/api/realtime/camera-data ^
  -H "Content-Type: application/json" ^
  -d "{\"ncam\": 2, \"time\": \"2025-07-03T14:31:00\", \"posX\": 200.10, \"posY\": 150.20, \"id\": 88}"

echo.
echo.
echo 3. Obtener datos mapa de calor:
curl -X GET http://localhost:8080/api/realtime/heatmap-data

echo.
echo.
echo 4. Verificar estado del sistema:
curl -X GET http://localhost:8080/api/realtime/stats

pause
