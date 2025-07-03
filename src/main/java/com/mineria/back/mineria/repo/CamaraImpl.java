package com.mineria.back.mineria.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mineria.back.mineria.repo.model.Camara;

@Repository
public class CamaraImpl implements ICamaraRepo {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Camara camara) {
        mongoTemplate.save(camara);
    }

    @Override
    public Camara findById(String id) {
        return mongoTemplate.findById(id, Camara.class);
    }

    @Override
    public List<Camara> findAll() {
        return mongoTemplate.findAll(Camara.class);
    }

    @Override
    public Camara findByNcam(Integer ncam) {
        Query query = new Query(Criteria.where("NCAM").is(ncam));
        return mongoTemplate.findOne(query, Camara.class);
    }

    @Override
    public void update(Camara camara) {
        Query query = new Query(Criteria.where("_id").is(camara.getIdCamara()));
        Update update = new Update()
            .set("NCAM", camara.getNcam())
            .set("TIME", camara.getTime())
            .set("POS_X", camara.getPosX())
            .set("POS_Y", camara.getPosY());
        
        mongoTemplate.updateFirst(query, update, Camara.class);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Camara.class);
    }
}
