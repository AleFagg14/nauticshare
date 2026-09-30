package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dto.RentalDTO;
import it.unifi.nauticshare.model.Rental;

import java.util.List;

public interface RentalService {

    // Crea un nuovo noleggio diretto e richiede patente nautica
    Rental createRental(RentalDTO dto);

    // Lettura
    Rental findById(int id);
    List<Rental> findAll();
    List<Rental> findByMemberId(int memberId);
    List<Rental> findFuture(int memberId);

    // Cancella un noleggio
    boolean cancelRental(int rentalId);

    // Aggiunge un partecipante al noleggio e aggiorna il prezzo
    boolean addParticipant(int rentalId);
}