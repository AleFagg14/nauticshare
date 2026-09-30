package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dao.BookingDAO;
import it.unifi.nauticshare.dao.BoatDAO;
import it.unifi.nauticshare.dao.MemberDAO;
import it.unifi.nauticshare.dao.RentalDAO;
import it.unifi.nauticshare.dao.SkipperDAO;
import it.unifi.nauticshare.dto.BookingDTO;
import it.unifi.nauticshare.exception.BoatNotFoundException;
import it.unifi.nauticshare.exception.BookingConflictException;
import it.unifi.nauticshare.exception.InvalidRatingException;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.Booking;
import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.model.Skipper;

import java.time.LocalDate;
import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final BookingDAO bookingDAO;
    private final MemberDAO memberDAO;
    private final BoatDAO boatDAO;
    private final SkipperDAO skipperDAO;
    private final RentalDAO rentalDAO;

    // Tariffa fissa
    private static final double SEAT_PRICE = 50.0;

    public BookingServiceImpl(BookingDAO bookingDAO,
                              MemberDAO memberDAO,
                              BoatDAO boatDAO,
                              SkipperDAO skipperDAO,
                              RentalDAO rentalDAO) {
        this.bookingDAO = bookingDAO;
        this.memberDAO  = memberDAO;
        this.boatDAO    = boatDAO;
        this.skipperDAO = skipperDAO;
        this.rentalDAO  = rentalDAO;
    }

    @Override
    public Booking createBooking(BookingDTO dto) {

        // Validazione data
        if (dto.getDate() == null) {
            throw new IllegalArgumentException(
                    "La data della prenotazione è obbligatoria");
        }
        if (!dto.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La data della prenotazione deve essere futura");
        }
        if (dto.getSeatsBooked() <= 0) {
            throw new IllegalArgumentException(
                    "Il numero di posti deve essere maggiore di 0");
        }

        // Verifica membro
        Member member = memberDAO.findById(dto.getMemberId());
        if (member == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + dto.getMemberId() + " non trovato");
        }

        // Verifica barca
        Boat boat = boatDAO.findById(dto.getBoatId());
        if (boat == null) {
            throw new BoatNotFoundException(
                    "Barca con id " + dto.getBoatId() + " non trovata");
        }

        //  Verifica skipper assegnato
        // Un Booking richiede sempre uno skipper sulla barca.
        Skipper skipper = skipperDAO.findByBoatId(dto.getBoatId());
        if (skipper == null) {
            throw new IllegalArgumentException(
                    "La barca '" + boat.getName() + "' non ha uno skipper " +
                            "assegnato. Usa il Noleggio Diretto se hai la patente.");
        }

        //  Verifica capienza
        // 1 posto è sempre riservato allo skipper
        int maxSeats = boat.getSeats() - 1;
        if (dto.getSeatsBooked() > maxSeats) {
            throw new IllegalArgumentException(
                    "Posti richiesti (" + dto.getSeatsBooked() + ") " +
                            "superano la capienza disponibile (" + maxSeats + "). " +
                            "Un posto è riservato allo skipper.");
        }

        // Verifica disponibilità
        // hasOverlap controlla sia Booking che Rental per quella data
        if (bookingDAO.hasOverlap(dto.getBoatId(), dto.getDate())) {
            throw new BookingConflictException(
                    "La barca '" + boat.getName() + "' non è disponibile " +
                            "il giorno " + dto.getDate());
        }

        // Calcolo prezzo
        // Prezzo = numero di posti × tariffa per posto
        double totalPrice = dto.getSeatsBooked() * SEAT_PRICE;

        // Creazione oggetto Booking
        Booking booking = new Booking(
                dto.getMemberId(),
                dto.getBoatId(),
                skipper.getId(),        // skipperId preso dalla barca
                dto.getDate(),
                dto.getSeatsBooked(),
                totalPrice,
                dto.getRegType()
        );

        boolean inserted = bookingDAO.insert(booking);
        if (!inserted) {
            throw new RuntimeException(
                    "Errore durante il salvataggio della prenotazione");
        }

        // Recupera il booking appena inserito
        return bookingDAO.findByMemberId(dto.getMemberId())
                .stream()
                .filter(b -> b.getDate().equals(dto.getDate())
                        && b.getBoatId() == dto.getBoatId())
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Booking inserito ma non recuperabile dal DB"));
    }

    @Override
    public Booking findById(int id) {
        Booking booking = bookingDAO.findById(id);
        if (booking == null) {
            throw new IllegalArgumentException(
                    "Prenotazione con id " + id + " non trovata");
        }
        return booking;
    }

    @Override
    public List<Booking> findAll() {
        return bookingDAO.findAll();
    }

    @Override
    public List<Booking> findByMemberId(int memberId) {
        if (memberDAO.findById(memberId) == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + memberId + " non trovato");
        }
        return bookingDAO.findByMemberId(memberId);
    }

    @Override
    public List<Booking> findFuture(int memberId) {
        if (memberDAO.findById(memberId) == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + memberId + " non trovato");
        }
        return bookingDAO.findFuture(memberId);
    }

    @Override
    public List<Booking> findPastBySkipperId(int skipperId) {
        if (skipperDAO.findById(skipperId) == null) {
            throw new IllegalArgumentException(
                    "Skipper con id " + skipperId + " non trovato");
        }
        return bookingDAO.findPastBySkipperId(skipperId);
    }

    @Override
    public boolean cancelBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException(
                    "Prenotazione con id " + bookingId + " non trovata");
        }

        // isCancellable() è definito nel Model Booking —
        // restituisce true solo se la data è futura
        if (!booking.isCancellable()) {
            throw new UnauthorizedOperationException(
                    "Impossibile cancellare la prenotazione: " +
                            "l'uscita del " + booking.getDate() +
                            " è già avvenuta o è oggi");
        }

        return bookingDAO.delete(bookingId);
    }

    @Override
    public boolean rateSkipper(int bookingId, double rating) {

        // Verifica range valutazione
        if (rating < 1.0 || rating > 5.0) {
            throw new InvalidRatingException(
                    "La valutazione deve essere compresa tra 1.0 e 5.0. " +
                            "Hai inserito: " + rating);
        }

        // Verifica booking
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException(
                    "Prenotazione con id " + bookingId + " non trovata");
        }

        //  Verifica che l'uscita sia già avvenuta

        if (booking.isCancellable()) {
            throw new InvalidRatingException(
                    "Non puoi valutare lo skipper prima che l'uscita " +
                            "del " + booking.getDate() + " sia avvenuta");
        }

        // Verifica che il booking abbia uno skipper
        if (booking.getSkipperId() == null) {
            throw new IllegalArgumentException(
                    "Questa prenotazione non ha uno skipper associato");
        }

        //  Recupera lo skipper e aggiorna la media
        Skipper skipper = skipperDAO.findById(booking.getSkipperId());
        if (skipper == null) {
            throw new IllegalArgumentException(
                    "Skipper con id " + booking.getSkipperId() + " non trovato");
        }

        // updateRating() nel Model ricalcola la media ponderata
        skipper.updateRating(rating);

        // Persiste la nuova media nel DB
        return skipperDAO.updateRating(skipper.getId(), skipper.getAvgRating());
    }
}