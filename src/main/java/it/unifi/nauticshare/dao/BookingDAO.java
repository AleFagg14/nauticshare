package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Booking;
import it.unifi.nauticshare.model.RegistrationType;

import java.time.LocalDate;
import java.util.List;

public interface BookingDAO {

    // CRUD base
    boolean insert(Booking booking);
    Booking findById(int id);
    List<Booking> findAll();
    boolean delete(int id);

    // Query custom
    List<Booking> findByMemberId(int memberId);
    List<Booking> findFuture(int memberId);
    List<Booking> findPastBySkipperId(int skipperId); //Utile per SkipperService
    //A differenza di Rental che dura più giorni, un Booking è per una singola giornata. Per questo motivo hasOverlap
    boolean hasOverlap(int boatId, LocalDate date);
}