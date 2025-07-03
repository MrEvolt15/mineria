// test_api.js
const fetch = require('node-fetch'); // npm install node-fetch

const BASE_URL = 'http://localhost:8080/api/realtime';

async function testSingleDetection() {
    const data = {
        ncam: 1,
        time: '2025-07-03T14:30:00',
        posX: 123.45,
        posY: 67.89,
        id: 95,
        tipoPersona: 'E',
        genero: 'M'
    };

    try {
        const response = await fetch(`${BASE_URL}/camera-data`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        console.log('Status:', response.status);
        console.log('Response:', await response.text());
    } catch (error) {
        console.error('Error:', error);
    }
}

async function simulateRealTimeStream() {
    for (let i = 0; i < 5; i++) {
        const data = {
            ncam: Math.floor(Math.random() * 5) + 1,
            time: new Date().toISOString(),
            posX: Math.random() * 500,
            posY: Math.random() * 300,
            id: Math.floor(Math.random() * 30) + 70,
            tipoPersona: ['E', 'PR', 'PA'][Math.floor(Math.random() * 3)],
            genero: ['M', 'F'][Math.floor(Math.random() * 2)]
        };

        await fetch(`${BASE_URL}/camera-data`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        console.log(`Detección ${i + 1} enviada`);
        await new Promise(resolve => setTimeout(resolve, 1000));
    }
}

// Ejecutar pruebas
testSingleDetection();
// simulateRealTimeStream();
