package com.mineria.back.mineria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mineria.back.mineria.repo.ICamaraRepo;
import com.mineria.back.mineria.repo.model.Camara;

@RestController
@RequestMapping("/api/admin/camaras")
public class CamaraAdminController {

    @Autowired
    private ICamaraRepo camaraRepo;

    /**
     * Solo para consulta y monitoreo
     */
    @GetMapping("/{id}")
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
    public ResponseEntity<List<Camara>> getAllCamaras() {
        try {
            List<Camara> camaras = camaraRepo.findAll();
            return new ResponseEntity<>(camaras, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/numero/{ncam}")
    public ResponseEntity<Camara> getCamaraByNumero(@PathVariable Integer ncam) {
        try {
            Camara camara = camaraRepo.findByNcam(ncam);
            if (camara != null) {
                return new ResponseEntity<>(camara, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
