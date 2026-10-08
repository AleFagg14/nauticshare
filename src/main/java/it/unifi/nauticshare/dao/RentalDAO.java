package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Rental;

import java.time.LocalDate;
import java.util.List;

public interface RentalDAO {
    boolean insert(Rental rental);
    Rental findById(int id);
    List<Rental> findAll();
    boolean update(Rental rental);
    boolean delete(int id);

    // Query custom
    List<Rental> findByMemberId(int memberId);
    List<Rental> findFuture(int memberId);
    boolean hasOverlap(int boatId, LocalDate startDate, LocalDate endDate);
}