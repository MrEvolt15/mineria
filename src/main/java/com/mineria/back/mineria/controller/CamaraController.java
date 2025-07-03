package com.mineria.back.mineria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mineria.back.mineria.repo.ICamaraRepo;
import com.mineria.back.mineria.repo.model.Camara;

@RestController
@RequestMapping("/api/camaras")
public class CamaraController {

    @Autowired
    private ICamaraRepo camaraRepo;

    @PostMapping
    // http://localhost:8080/api/camaras
    public ResponseEntity<String> createCamara(@RequestBody Camara camara) {
        try {
            camaraRepo.save(camara);
            return new ResponseEntity<>("Cámara creada exitosamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear cámara: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    // http://localhost:8080/api/camaras/{id}
    public ResponseEntity<Camara> getCamaraById(@PathVariable String id) {
        try {
            Camara camara = camaraRepo.findById(id);
            if (camara != null) {
                return new ResponseEntity<>(camara, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    // http://localhost:8080/api/camaras
    public ResponseEntity<List<Camara>> getAllCamaras() {
        try {
            List<Camara> camaras = camaraRepo.findAll();
            return new ResponseEntity<>(camaras, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    // http://localhost:8080/api/camaras/{id}
    public ResponseEntity<String> updateCamara(@PathVariable String id, @RequestBody Camara camara) {
        try {
            Camara existingCamara = camaraRepo.findById(id);
            if (existingCamara != null) {
                camara.setIdCamara(id); // Asegurar que el ID se mantenga
                camaraRepo.update(camara);
                return new ResponseEntity<>("Cámara actualizada exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cámara no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar cámara: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    // http://localhost:8080/api/camaras/{id}
    public ResponseEntity<String> deleteCamara(@PathVariable String id) {
        try {
            Camara existingCamara = camaraRepo.findById(id);
            if (existingCamara != null) {
                camaraRepo.deleteById(id);
                return new ResponseEntity<>("Cámara eliminada exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cámara no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar cámara: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
