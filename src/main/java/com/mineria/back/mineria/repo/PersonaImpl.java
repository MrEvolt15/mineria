package com.mineria.back.mineria.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mineria.back.mineria.repo.model.Persona;

@Repository
public class PersonaImpl implements IPersonaRepo{
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Persona persona) {
        mongoTemplate.save(persona);
    }

    @Override
    public Persona findById(String id) {
        return mongoTemplate.findById(id, Persona.class);
    }

    @Override
    public List<Persona> findAll() {
        return mongoTemplate.findAll(Persona.class);
    }

    @Override
    public void update(Persona persona) {
        Query query = new Query(Criteria.where("_id").is(persona.getIdPersona()));
        Update update = new Update()
            .set("TIPO", persona.getTipo())
            .set("GENERO", persona.getGenero());
        
        mongoTemplate.updateFirst(query, update, Persona.class);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Persona.class);
    }

}
