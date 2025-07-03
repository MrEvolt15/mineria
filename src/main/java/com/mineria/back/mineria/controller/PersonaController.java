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

import com.mineria.back.mineria.repo.IPersonaRepo;
import com.mineria.back.mineria.repo.model.Persona;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @Autowired
    private IPersonaRepo personaRepo;

    @PostMapping
    // http://localhost:8080/api/personas
    public ResponseEntity<String> createPersona(@RequestBody Persona persona) {
        try {
            personaRepo.save(persona);
            return new ResponseEntity<>("Persona creada exitosamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear persona: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    // http://localhost:8080/api/personas/{id}
    public ResponseEntity<Persona> getPersonaById(@PathVariable String id) {
        try {
            Persona persona = personaRepo.findById(id);
            if (persona != null) {
                return new ResponseEntity<>(persona, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    // http://localhost:8080/api/personas
    public ResponseEntity<List<Persona>> getAllPersonas() {
        try {
            List<Persona> personas = personaRepo.findAll();
            return new ResponseEntity<>(personas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    // http://localhost:8080/api/personas/{id}
    public ResponseEntity<String> updatePersona(@PathVariable String id, @RequestBody Persona persona) {
        try {
            Persona existingPersona = personaRepo.findById(id);
            if (existingPersona != null) {
                persona.setIdPersona(id); // Asegurar que el ID se mantenga
                personaRepo.update(persona);
                return new ResponseEntity<>("Persona actualizada exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Persona no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar persona: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    // http://localhost:8080/api/personas/{id}
    public ResponseEntity<String> deletePersona(@PathVariable String id) {
        try {
            Persona existingPersona = personaRepo.findById(id);
            if (existingPersona != null) {
                personaRepo.deleteById(id);
                return new ResponseEntity<>("Persona eliminada exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Persona no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar persona: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
