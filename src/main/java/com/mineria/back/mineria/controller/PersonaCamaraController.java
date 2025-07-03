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

import com.mineria.back.mineria.repo.IPersonaCamaraRepo;
import com.mineria.back.mineria.repo.model.PersonaCamara;

@RestController
@RequestMapping("/api/persona-camara")
public class PersonaCamaraController {

    @Autowired
    private IPersonaCamaraRepo personaCamaraRepo;

    @PostMapping
    public ResponseEntity<String> createRelacion(@RequestBody PersonaCamara personaCamara) {
        try {
            personaCamaraRepo.save(personaCamara);
            return new ResponseEntity<>("Relación Persona-Cámara creada exitosamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear relación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaCamara> getRelacionById(@PathVariable String id) {
        try {
            PersonaCamara personaCamara = personaCamaraRepo.findById(id);
            if (personaCamara != null) {
                return new ResponseEntity<>(personaCamara, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<PersonaCamara>> getAllRelaciones() {
        try {
            List<PersonaCamara> relaciones = personaCamaraRepo.findAll();
            return new ResponseEntity<>(relaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/persona/{idPersona}")
    public ResponseEntity<List<PersonaCamara>> getRelacionesByPersona(@PathVariable String idPersona) {
        try {
            List<PersonaCamara> relaciones = personaCamaraRepo.findByIdPersona(idPersona);
            return new ResponseEntity<>(relaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/camara/{idCamara}")
    public ResponseEntity<List<PersonaCamara>> getRelacionesByCamara(@PathVariable String idCamara) {
        try {
            List<PersonaCamara> relaciones = personaCamaraRepo.findByIdCamara(idCamara);
            return new ResponseEntity<>(relaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateRelacion(@PathVariable String id, @RequestBody PersonaCamara personaCamara) {
        try {
            PersonaCamara existingRelacion = personaCamaraRepo.findById(id);
            if (existingRelacion != null) {
                personaCamara.setIdPersonaCamara(id);
                personaCamaraRepo.update(personaCamara);
                return new ResponseEntity<>("Relación actualizada exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Relación no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar relación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRelacion(@PathVariable String id) {
        try {
            PersonaCamara existingRelacion = personaCamaraRepo.findById(id);
            if (existingRelacion != null) {
                personaCamaraRepo.deleteById(id);
                return new ResponseEntity<>("Relación eliminada exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Relación no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar relación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
