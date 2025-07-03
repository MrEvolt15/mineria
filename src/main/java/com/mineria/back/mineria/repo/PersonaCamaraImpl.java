package com.mineria.back.mineria.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mineria.back.mineria.repo.model.PersonaCamara;

@Repository
public class PersonaCamaraImpl implements IPersonaCamaraRepo {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(PersonaCamara personaCamara) {
        mongoTemplate.save(personaCamara);
    }

    @Override
    public PersonaCamara findById(String id) {
        return mongoTemplate.findById(id, PersonaCamara.class);
    }

    @Override
    public List<PersonaCamara> findAll() {
        return mongoTemplate.findAll(PersonaCamara.class);
    }

    @Override
    public List<PersonaCamara> findByIdPersona(String idPersona) {
        Query query = new Query(Criteria.where("idPersona").is(idPersona));
        return mongoTemplate.find(query, PersonaCamara.class);
    }

    @Override
    public List<PersonaCamara> findByIdCamara(String idCamara) {
        Query query = new Query(Criteria.where("idCamara").is(idCamara));
        return mongoTemplate.find(query, PersonaCamara.class);
    }

    @Override
    public void update(PersonaCamara personaCamara) {
        Query query = new Query(Criteria.where("id").is(personaCamara.getIdPersonaCamara()));
        Update update = new Update()
            .set("idPersona", personaCamara.getIdPersona())
            .set("idCamara", personaCamara.getIdCamara())
            .set("fechaDeteccion", personaCamara.getFechaDeteccion())
            .set("confianza", personaCamara.getConfianza());
        
        mongoTemplate.updateFirst(query, update, PersonaCamara.class);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, PersonaCamara.class);
    }

    @Override
    public void deleteByIdPersona(String idPersona) {
        Query query = new Query(Criteria.where("idPersona").is(idPersona));
        mongoTemplate.remove(query, PersonaCamara.class);
    }

    @Override
    public void deleteByIdCamara(String idCamara) {
        Query query = new Query(Criteria.where("idCamara").is(idCamara));
        mongoTemplate.remove(query, PersonaCamara.class);
    }
}
