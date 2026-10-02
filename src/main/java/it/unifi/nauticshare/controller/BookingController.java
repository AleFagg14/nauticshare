package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dto.BookingDTO;
import it.unifi.nauticshare.exception.BoatNotFoundException;
import it.unifi.nauticshare.exception.BookingConflictException;
import it.unifi.nauticshare.exception.InvalidRatingException;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.Booking;
import it.unifi.nauticshare.service.BookingService;

import java.util.List;

public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // CREATE BOOKING
    // Chiamato dal Membro per prenotare un'uscita con skipper (UC-9).

    public Booking createBooking(BookingDTO dto) {
        try {
            return bookingService.createBooking(dto);
        } catch (MemberNotFoundException e) {
            System.err.println("[BookingController] Membro non trovato: "
                    + e.getMessage());
            return null;
        } catch (BoatNotFoundException e) {
            System.err.println("[BookingController] Barca non trovata: "
                    + e.getMessage());
            return null;
        } catch (BookingConflictException e) {
            // Barca già occupata in quella data da altro booking o rental
            System.err.println("[BookingController] Conflitto disponibilità: "
                    + e.getMessage());
            return null;
        } catch (UnauthorizedOperationException e) {
            System.err.println("[BookingController] Operazione non autorizzata: "
                    + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            // Data passata, posti superati, skipper mancante
            System.err.println("[BookingController] Dati non validi: "
                    + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[BookingController] Errore imprevisto: "
                    + e.getMessage());
            return null;
        }
    }

    //FIND BY ID
    // Recupera una prenotazione specifica per id.
    // Usato dalla CLI prima di mostrare dettagli.
    public Booking findById(int id) {
        try {
            return bookingService.findById(id);
        } catch (IllegalArgumentException e) {
            System.err.println("[BookingController] " + e.getMessage());
            return null;
        }
    }

    // FIND ALL
    // Restituisce tutte le prenotazioni nel sistema.
    // Usato dall'Admin per il monitoraggio generale (UC-5).
    public List<Booking> findAll() {
        try {
            return bookingService.findAll();
        } catch (Exception e) {
            System.err.println("[BookingController] Errore findAll: "
                    + e.getMessage());
            return List.of();
        }
    }

    // FIND BY MEMBER ID
    // Restituisce tutte le prenotazioni di un membro incluse quelle passate.
    public List<Booking> findByMemberId(int memberId) {
        try {
            return bookingService.findByMemberId(memberId);
        } catch (MemberNotFoundException e) {
            System.err.println("[BookingController] Membro non trovato: "
                    + e.getMessage());
            return List.of();
        } catch (Exception e) {
            System.err.println("[BookingController] Errore findByMemberId: "
                    + e.getMessage());
            return List.of();
        }
    }

    //FIND FUTURE
    // Restituisce solo le prenotazioni future di un membro.
    // Usato dalla CLI per mostrare quelle ancora cancellabili (UC-11).
    public List<Booking> findFuture(int memberId) {
        try {
            return bookingService.findFuture(memberId);
        } catch (MemberNotFoundException e) {
            System.err.println("[BookingController] Membro non trovato: "
                    + e.getMessage());
            return List.of();
        } catch (Exception e) {
            System.err.println("[BookingController] Errore findFuture: "
                    + e.getMessage());
            return List.of();
        }
    }

    // FIND PAST BY SKIPPER IDd
    // Recupera le uscite passate di uno skipper specifico.
    // Usato dallo Skipper nel MenuSkipper per vedere
    // le proprie uscite completate e le valutazioni ricevute.
    public List<Booking> findPastBySkipperId(int skipperId) {
        try {
            return bookingService.findPastBySkipperId(skipperId);
        } catch (IllegalArgumentException e) {
            System.err.println("[BookingController] Skipper non trovato: "
                    + e.getMessage());
            return List.of();
        } catch (Exception e) {
            System.err.println("[BookingController] Errore findPastBySkipperId: "
                    + e.getMessage());
            return List.of();
        }
    }

    //CANCEL BOOKIN
    // Cancella una prenotazione — solo se futura (UC-11).
    public boolean cancelBooking(int bookingId) {
        try {
            return bookingService.cancelBooking(bookingId);
        } catch (UnauthorizedOperationException e) {
            System.err.println("[BookingController] Cancellazione non consentita: "
                    + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("[BookingController] Prenotazione non trovata: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[BookingController] Errore cancelBooking: "
                    + e.getMessage());
            return false;
        }
    }

    //ùRATE SKIPPER
    // Permette al Membro di valutare lo skipper dopo un'uscita avvenuta (UC-12).
    // Due regole fondamentali verificate dal Service:
    // 1. L'uscita deve essere già avvenuta (data passata)
    // 2. Il rating deve essere tra 1.0 e 5.0
    public boolean rateSkipper(int bookingId, double rating) {
        try {
            return bookingService.rateSkipper(bookingId, rating);
        } catch (InvalidRatingException e) {
            // Rating fuori range OPPURE uscita non ancora avvenuta
            System.err.println("[BookingController] Valutazione non valida: "
                    + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("[BookingController] Prenotazione non trovata: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[BookingController] Errore rateSkipper: "
                    + e.getMessage());
            return false;
        }
    }
}