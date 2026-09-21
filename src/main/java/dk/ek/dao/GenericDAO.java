package dk.ek.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GenericDAO<T> {

    protected final EntityManagerFactory emf;
    private final Class<T> entityClass;

    public GenericDAO(EntityManagerFactory emf, Class<T> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    // CREATE
    //DAO tag entity
    public void create(T entity) {
//arbejder med db
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(entity);//gemmer Entity-n i db
        em.getTransaction().commit();//konfirmmer

        em.close();//likker em
    }


    // READ - Find by ID
    public T findById(Long id) {

        EntityManager em = emf.createEntityManager();

        T entity = em.find(entityClass, id);

        em.close();

        return entity;
    }


    // READ - Find all
    public List<T> findAll() {

        EntityManager em = emf.createEntityManager();

        List<T> entities = em.createQuery(
                        "SELECT e FROM " + entityClass.getSimpleName() + " e",
                        entityClass
                )
                .getResultList();

        em.close();

        return entities;
    }


    // UPDATE
    public T update(T entity) {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        T updatedEntity = em.merge(entity);

        em.getTransaction().commit();
        em.close();

        return updatedEntity;
    }


    // DELETE
    public void delete(Long id) {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        T entity = em.find(entityClass, id);

        if (entity != null) {
            em.remove(entity);
        }

        em.getTransaction().commit();
        em.close();
    }
}