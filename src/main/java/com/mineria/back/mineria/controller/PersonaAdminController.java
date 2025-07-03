package com.mineria.back.mineria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mineria.back.mineria.repo.IPersonaRepo;
import com.mineria.back.mineria.repo.model.Persona;

@RestController
@RequestMapping("/api/admin/personas")
public class PersonaAdminController {

    @Autowired
    private IPersonaRepo personaRepo;

    /**
     * Solo para consulta y monitoreo
     */
    @GetMapping("/{id}")
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
    public ResponseEntity<List<Persona>> getAllPersonas() {
        try {
            List<Persona> personas = personaRepo.findAll();
            return new ResponseEntity<>(personas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
