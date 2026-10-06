package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dto.RentalDTO;
import it.unifi.nauticshare.exception.BoatNotFoundException;
import it.unifi.nauticshare.exception.BookingConflictException;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.Rental;
import it.unifi.nauticshare.service.RentalService;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.BookingConflictException;

import java.util.List;

public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // CREATE RENTAL
    // Chiamato dal Membro per creare un Noleggio Diretto (UC-10).
    // Gestisce separatamente ogni tipo di eccezione
    public Rental createRental(RentalDTO dto) {
        try {
            return rentalService.createRental(dto);
        } catch (MemberNotFoundException e) {
            System.err.println("[RentalController] Membro non trovato: "
                    + e.getMessage());
            return null;
        } catch (UnauthorizedOperationException e) {
            // Caso più comune — membro senza patente nautica
            System.err.println("[RentalController] Operazione non autorizzata: "
                    + e.getMessage());
            return null;
        } catch (BoatNotFoundException e) {
            System.err.println("[RentalController] Barca non trovata: "
                    + e.getMessage());
            return null;
        } catch (BookingConflictException e) {
            // Barca già occupata nel periodo richiesto
            System.err.println("[RentalController] Conflitto disponibilità: "
                    + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            // Date non valide o numParticipants negativo
            System.err.println("[RentalController] Dati non validi: "
                    + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[RentalController] Errore imprevisto: "
                    + e.getMessage());
            return null;
        }
    }

    //FIND BY ID
    // Recupera un noleggio specifico per id.
    // Usato dalla CLI per mostrare il dettaglio
    // prima di una cancellazione o modifica.
    public Rental findById(int id) {
        try {
            return rentalService.findById(id);
        } catch (IllegalArgumentException e) {
            System.err.println("[RentalController] " + e.getMessage());
            return null;
        }
    }

    //FIND ALL
    // Restituisce tutti i noleggi nel sistema.
    // Usato dall'Admin per monitorare la situazione (UC-5 Monitora Prenotazioni).
    public List<Rental> findAll() {
        try {
            return rentalService.findAll();
        } catch (Exception e) {
            System.err.println("[RentalController] Errore findAll: "
                    + e.getMessage());
            return List.of();
        }
    }

    // FIND BY MEMBER ID
    // Restituisce tutti i noleggi di un membro specifico.
    // Usato dal Membro nella sezione "I miei noleggi"
    public List<Rental> findByMemberId(int memberId) {
        try {
            return rentalService.findByMemberId(memberId);
        } catch (MemberNotFoundException e) {
            System.err.println("[RentalController] Membro non trovato: "
                    + e.getMessage());
            return List.of();
        } catch (Exception e) {
            System.err.println("[RentalController] Errore findByMemberId: "
                    + e.getMessage());
            return List.of();
        }
    }

    // FIND FUTURE
    // Restituisce solo i noleggi futuri di un membro.
    // Usato dalla CLI per mostrare i noleggi cancellabili (UC-11).
    public List<Rental> findFuture(int memberId) {
        try {
            return rentalService.findFuture(memberId);
        } catch (MemberNotFoundException e) {
            System.err.println("[RentalController] Membro non trovato: "
                    + e.getMessage());
            return List.of();
        } catch (Exception e) {
            System.err.println("[RentalController] Errore findFuture: "
                    + e.getMessage());
            return List.of();
        }
    }

    // CANCEL RENTAL
    // Cancella un noleggio — solo se non ancora iniziato.
    public boolean cancelRental(int rentalId) {
        try {
            return rentalService.cancelRental(rentalId);
        } catch (UnauthorizedOperationException e) {
            System.err.println("[RentalController] Cancellazione non consentita: "
                    + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("[RentalController] Noleggio non trovato: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[RentalController] Errore cancelRental: "
                    + e.getMessage());
            return false;
        }
    }

    // ADD PARTICIPANT
    // Aggiunge un partecipante a un noleggio esistente.
    // Ogni partecipante aggiuntivo costa €10 (UC-13 Aggiungi Partecipante a Noleggio).
    public boolean addParticipant(int rentalId) {
        try {
            return rentalService.addParticipant(rentalId);
        } catch (UnauthorizedOperationException e) {
            System.err.println("[RentalController] Aggiunta non consentita: "
                    + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("[RentalController] Noleggio non trovato: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[RentalController] Errore addParticipant: "
                    + e.getMessage());
            return false;
        }
    }
}