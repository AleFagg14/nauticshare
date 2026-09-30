package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dto.BookingDTO;
import it.unifi.nauticshare.model.Booking;

import java.util.List;

public interface BookingService {

    // Crea una prenotazione con skipper
    Booking createBooking(BookingDTO dto);

    // Lettura
    Booking findById(int id);
    List<Booking> findAll();
    List<Booking> findByMemberId(int memberId);
    List<Booking> findFuture(int memberId);
    List<Booking> findPastBySkipperId(int skipperId);

    // Cancella una prenotazione (futura)
    boolean cancelBooking(int bookingId);

    // Valuta skipper dopo un'uscita avvenuta
    boolean rateSkipper(int bookingId, double rating);
}