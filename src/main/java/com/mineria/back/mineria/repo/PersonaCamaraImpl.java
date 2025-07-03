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
        Query query = new Query(Criteria.where("ID_PERSONA").is(idPersona));
        return mongoTemplate.find(query, PersonaCamara.class);
    }

    @Override
    public List<PersonaCamara> findByIdCamara(String idCamara) {
        Query query = new Query(Criteria.where("ID_CAMARA").is(idCamara));
        return mongoTemplate.find(query, PersonaCamara.class);
    }

    @Override
    public void update(PersonaCamara personaCamara) {
        Query query = new Query(Criteria.where("_id").is(personaCamara.getIdPersonaCamara()));
        Update update = new Update()
            .set("ID_PERSONA", personaCamara.getIdPersona())
            .set("ID_CAMARA", personaCamara.getIdCamara())
            .set("FECHA_DETECCION", personaCamara.getFechaDeteccion())
            .set("CONFIANZA", personaCamara.getConfianza());
        
        mongoTemplate.updateFirst(query, update, PersonaCamara.class);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, PersonaCamara.class);
    }

    @Override
    public void deleteByIdPersona(String idPersona) {
        Query query = new Query(Criteria.where("ID_PERSONA").is(idPersona));
        mongoTemplate.remove(query, PersonaCamara.class);
    }

    @Override
    public void deleteByIdCamara(String idCamara) {
        Query query = new Query(Criteria.where("ID_CAMARA").is(idCamara));
        mongoTemplate.remove(query, PersonaCamara.class);
    }
    
    @Override
    public List<PersonaCamara> findByFechaDeteccionBetween(java.time.LocalDateTime inicio, java.time.LocalDateTime fin) {
        Query query = new Query();
        if (inicio != null && fin != null) {
            query.addCriteria(Criteria.where("FECHA_DETECCION").gte(inicio).lte(fin));
        } else if (inicio != null) {
            query.addCriteria(Criteria.where("FECHA_DETECCION").gte(inicio));
        } else if (fin != null) {
            query.addCriteria(Criteria.where("FECHA_DETECCION").lte(fin));
        }
        return mongoTemplate.find(query, PersonaCamara.class);
    }
}
