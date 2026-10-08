package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dao.BoatDAO;
import it.unifi.nauticshare.dao.BookingDAO;
import it.unifi.nauticshare.dao.MemberDAO;
import it.unifi.nauticshare.dao.RentalDAO;
import it.unifi.nauticshare.dto.RentalDTO;
import it.unifi.nauticshare.exception.BoatNotFoundException;
import it.unifi.nauticshare.exception.BookingConflictException;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.model.Rental;

import java.time.LocalDate;
import java.util.List;

public class RentalServiceImpl implements RentalService {

    private final RentalDAO rentalDAO;
    private final MemberDAO memberDAO;
    private final BoatDAO boatDAO;
    private final BookingDAO bookingDAO;

    // Quattro DAO infatti RentalService deve coordinare
    // più entità per verificare tutte le regole di business
    public RentalServiceImpl(RentalDAO rentalDAO,
                             MemberDAO memberDAO,
                             BoatDAO boatDAO,
                             BookingDAO bookingDAO) {
        this.rentalDAO  = rentalDAO;
        this.memberDAO  = memberDAO;
        this.boatDAO    = boatDAO;
        this.bookingDAO = bookingDAO;
    }

    @Override
    public Rental createRental(RentalDTO dto) {

        // Validazione date
        // Prima di fare qualsiasi query al DB verifichiamo i dati in input
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new IllegalArgumentException(
                    "Le date di inizio e fine sono obbligatorie");
        }
        if (!dto.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La data di inizio deve essere futura");
        }
        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new IllegalArgumentException(
                    "La data di fine deve essere successiva alla data di inizio");
        }
        if (dto.getNumParticipants() < 0) {
            throw new IllegalArgumentException(
                    "Il numero di partecipanti non può essere negativo");
        }

        //Verifica membro
        Member member = memberDAO.findById(dto.getMemberId());
        if (member == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + dto.getMemberId() + " non trovato");
        }

        //Verifica patente nautica
        // Senza patente il noleggio diretto non è consentito.
        if (!member.isHasLicense()) {
            throw new UnauthorizedOperationException(
                    "Patente nautica richiesta per il noleggio diretto. " +
                            "Considera la prenotazione con skipper.");
        }

        //Verifica barca
        Boat boat = boatDAO.findById(dto.getBoatId());
        if (boat == null) {
            throw new BoatNotFoundException(
                    "Barca con id " + dto.getBoatId() + " non trovata");
        }

        // Controlla che non ci sia già un altro Rental per questa barca
        // nel periodo richiesto
        if (rentalDAO.hasOverlap(dto.getBoatId(),
                dto.getStartDate(),
                dto.getEndDate())) {
            throw new BookingConflictException(
                    "La barca '" + boat.getName() + "' non è disponibile " +
                            "nel periodo " + dto.getStartDate() +
                            " — " + dto.getEndDate() +
                            " (conflitto con altro noleggio)");
        }


        // Un Booking occupa la barca per un singolo giorno.
        // Dobbiamo verificare che nessun giorno del periodo richiesto
        // sia già occupato da un Booking.
        LocalDate current = dto.getStartDate();
        while (!current.isAfter(dto.getEndDate())) {
            if (bookingDAO.hasOverlap(dto.getBoatId(), current)) {
                throw new BookingConflictException(
                        "La barca '" + boat.getName() + "' non è disponibile " +
                                "il giorno " + current +
                                " (conflitto con una prenotazione esistente)");
            }
            current = current.plusDays(1);
        }

        //Calcolo prezzo
        Rental rental = new Rental(
                dto.getMemberId(),
                dto.getBoatId(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getNumParticipants(),
                0.0 // verrà calcolato subito sotto
        );

        // Passiamo la tipologia di barca per il calcolo differenziato
        double price = rental.calculatePrice(boat.getType());
        rental.setTotalPrice(price);

        // Salvataggio
        boolean inserted = rentalDAO.insert(rental);
        if (!inserted) {
            throw new RuntimeException(
                    "Errore durante il salvataggio del noleggio nel database");
        }

        // Recuperiamo i rental del membro e prendiamo il più recente
        return rentalDAO.findByMemberId(dto.getMemberId())
                .stream()
                .filter(r -> r.getStartDate().equals(dto.getStartDate())
                        && r.getBoatId() == dto.getBoatId())
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Rental inserito ma non recuperabile dal DB"));
    }

    @Override
    public Rental findById(int id) {
        Rental rental = rentalDAO.findById(id);
        if (rental == null) {
            throw new IllegalArgumentException(
                    "Noleggio con id " + id + " non trovato");
        }
        return rental;
    }

    @Override
    public List<Rental> findAll() {
        return rentalDAO.findAll();
    }

    @Override
    public List<Rental> findByMemberId(int memberId) {
        // Verifica che il membro esista prima di cercare i suoi rental
        if (memberDAO.findById(memberId) == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + memberId + " non trovato");
        }
        return rentalDAO.findByMemberId(memberId);
    }

    @Override
    public List<Rental> findFuture(int memberId) {
        if (memberDAO.findById(memberId) == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + memberId + " non trovato");
        }
        return rentalDAO.findFuture(memberId);
    }

    @Override
    public boolean cancelRental(int rentalId) {

        Rental rental = rentalDAO.findById(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException(
                    "Noleggio con id " + rentalId + " non trovato");
        }

        // isCancellable() controlla che startDate sia futura
        // Metodo definito nel Model — regola di business incapsulata
        // nell'entità stessa
        if (!rental.isCancellable()) {
            throw new UnauthorizedOperationException(
                    "Impossibile cancellare il noleggio: " +
                            "la data di inizio " + rental.getStartDate() +
                            " è già passata o è oggi");
        }

        return rentalDAO.delete(rentalId);
    }

    @Override
    public boolean addParticipant(int rentalId) {

        Rental rental = rentalDAO.findById(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException(
                    "Noleggio con id " + rentalId + " non trovato");
        }

        // Non si possono aggiungere partecipanti a un noleggio già iniziato
        if (!rental.isCancellable()) {
            throw new UnauthorizedOperationException(
                    "Impossibile aggiungere partecipanti: " +
                            "il noleggio è già iniziato o concluso");
        }

        // Recupera la barca per conoscere il tipo e calcolare il prezzo corretto
        Boat boat = boatDAO.findById(rental.getBoatId());
        if (boat == null) {
            throw new BoatNotFoundException(
                    "Barca con id " + rental.getBoatId() + " non trovata");
        }

        // Verifica capienza massima — non si supera il numero di posti della barca
        if (rental.getNumParticipants() + 1 > boat.getSeats()) {
            throw new UnauthorizedOperationException(
                    "Capienza massima raggiunta: la barca '" +
                            boat.getName() + "' ha " + boat.getSeats() +
                            " posti totali");
        }

        // Incrementa partecipanti e ricalcola prezzo con tipo barca
        rental.setNumParticipants(rental.getNumParticipants() + 1);
        double newPrice = rental.calculatePrice(boat.getType());
        rental.setTotalPrice(newPrice);

        // Update — non insert — aggiorna il record esistente
        return rentalDAO.update(rental);
    }
}