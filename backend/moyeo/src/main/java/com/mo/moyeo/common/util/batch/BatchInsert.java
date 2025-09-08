package com.mo.moyeo.common.util.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchInsert {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public <T> void saveBatch(List<T> rates) {
        for (int i = 0; i < rates.size(); i++) {
            em.persist(rates.get(i));
        }
        em.flush();
        em.clear();
    }
}
