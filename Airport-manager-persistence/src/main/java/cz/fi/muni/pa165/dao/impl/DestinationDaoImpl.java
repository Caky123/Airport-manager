package cz.fi.muni.pa165.dao.impl;

import cz.fi.muni.pa165.dao.DestinationDao;
import cz.fi.muni.pa165.entities.Destination;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Implementation of the {@link DestinationDao} interface.
 *
 * @author Robert Duriancik
 */

@Repository
public class DestinationDaoImpl implements DestinationDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void addDestination(Destination destination) {
        if (destination == null) {
            throw new NullPointerException("Destination is null");
        }

        if (destination.getCountry() == null ||
                destination.getCity() == null) {
            throw new IllegalArgumentException("Destination's city or country is not set");
        }

        em.persist(destination);
    }

    @Override
    public void removeDestination(Destination destination) {
        if (destination == null) {
            throw new NullPointerException("Destination is null");
        }

        Destination destFromDb = getDestination(destination.getId());

        if (destFromDb != null) {
            em.remove(destFromDb);
        } else {
            throw new IllegalArgumentException("Destination: " + destination + " not in the persistence storage.");
        }
    }

    @Override
    public void updateDestination(Destination destination) {
        if (destination == null) {
            throw new NullPointerException("Destination is null");
        }

        Destination destFromDb = getDestination(destination.getId());

        if (destFromDb != null) {
            em.merge(destination);
        } else {
            throw new IllegalArgumentException("Destination: " + destination + " not in the persistence storage.");
        }
    }

    @Override
    public List<Destination> getAllDestinations() {
        return em.createQuery("select d from Destination d", Destination.class).getResultList();
    }

    @Override
    public Destination getDestination(Long id) {
        if (id == null) {
            throw new NullPointerException("Id is null");
        }
        return em.find(Destination.class, id);
    }

    @Override
    public List<Destination> getDestinationsByCity(String city) {
        if (city == null) {
            throw new NullPointerException("City is null.");
        }
        return em.createQuery("SELECT d FROM Destination d WHERE d.city = :city", Destination.class)
                .setParameter("city", city).getResultList();
    }

    @Override
    public List<Destination> getDestinationsByCountry(String country) {
        if(country == null) {
            throw new NullPointerException("Country is null.");
        }
        return em.createQuery("SELECT d FROM Destination d WHERE d.country = :country", Destination.class)
                .setParameter("country", country).getResultList();
    }
}
