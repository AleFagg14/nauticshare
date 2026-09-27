package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Boat;

import java.time.LocalDate;
import java.util.List;

public interface BoatDAO {
    boolean insert(Boat boat);
    Boat findById(int id);
    List<Boat> findAll();
    boolean update(Boat boat);
    boolean delete(int id);

    // Query utili
    boolean existsByRegistrationNumber(String registrationNumber);
    List<Boat> findAvailable(LocalDate startDate, LocalDate endDate);
}