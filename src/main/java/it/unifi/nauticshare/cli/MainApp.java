package it.unifi.nauticshare.cli;

import it.unifi.nauticshare.dao.*;
import it.unifi.nauticshare.dto.*;
import it.unifi.nauticshare.exception.BoatNotFoundException;
import it.unifi.nauticshare.exception.InvalidRatingException;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.*;
import it.unifi.nauticshare.service.*;


import java.time.LocalDate;
import java.util.List;

public class MainApp {

    public static void main(String[] args) {

        // STEP 1 — verifica connessione DB
        System.out.println("=== TEST CONNESSIONE ===");
        boolean connected = ConnectionManager.testConnection();
        System.out.println("Connessione DB: " + (connected ? "OK" : "FALLITA"));
        if (!connected) {
            System.err.println("Impossibile connettersi al DB. Controlla db.properties.");
            return;
        }

        // STEP 2 — verifica insert diretto nel DAO (bypassa il Service)
        System.out.println("\n=== TEST INSERT DIRETTO DAO ===");
        MemberDAO memberDAO = new MemberDAOJdbcImpl();

        Member testMember = new Member(
                "Test", "User", "test.debug@email.it",
                "hashed_test", "Firenze",
                LocalDate.of(1995, 6, 15), false
        );

        boolean insertResult = memberDAO.insert(testMember);
        System.out.println("Insert DAO result: " + insertResult);

        // STEP 3 — verifica che il membro sia recuperabile
        Member found = memberDAO.findByEmail("test.debug@email.it");
        System.out.println("FindByEmail result: " +
                (found != null ? "TROVATO id=" + found.getId() : "NULL - non trovato"));

        // STEP 4 — conta tutti i membri nel DB
        System.out.println("\n=== TUTTI I MEMBRI NEL DB ===");
        memberDAO.findAll().forEach(m ->
                System.out.println("  id=" + m.getId() +
                        " email=" + m.getEmail() +
                        " name=" + m.getName()));

        // STEP 5 — test Service completo
        System.out.println("\n=== TEST SERVICE SIGNUP ===");
        RegistrationDAO registrationDAO = new RegistrationDAOJdbcImpl();
        MemberService service = new MemberServiceImpl(memberDAO, registrationDAO);

        try {
            MemberDTO dto = new MemberDTO(
                    "Mario", "Test", "mario.test@email.it",
                    "password123", "Roma",
                    LocalDate.of(1990, 3, 20), true
            );
            Member newMember = service.signup(dto);
            System.out.println("Signup OK — id=" + newMember.getId() +
                    " email=" + newMember.getEmail());
        } catch (Exception e) {
            System.err.println("Signup FALLITO: " + e.getMessage());
            e.printStackTrace();
        }


        //TEST BOAT SERVICE
        System.out.println("\n=== TEST BOAT SERVICE ===");
        BoatDAO boatDAO = new BoatDAOJdbcImpl();
        SkipperDAO skipperDAO = new SkipperDAOJdbcImpl();
        BoatService boatService = new BoatServiceImpl(boatDAO, skipperDAO);
        BookingDAO bookingDAO = new BookingDAOJdbcImpl();

// Test 1 — lista tutte le barche
        System.out.println("Barche nel DB:");
        boatService.findAll().forEach(b ->
                System.out.println("  id=" + b.getId() +
                        " name=" + b.getName() +
                        " type=" + b.getType() +
                        " seats=" + b.getSeats()));

// Test 2 — aggiungi una nuova barca
        try {
            BoatDTO newBoat = new BoatDTO(
                    "IT-TEST-001", "Barca Test", BoatType.YACHT,
                    6, null, "Barca di test"
            );
            Boat added = boatService.addBoat(newBoat);
            System.out.println("Barca aggiunta OK — id=" + added.getId());
        } catch (Exception e) {
            System.err.println("addBoat FALLITO: " + e.getMessage());
        }

// Test 3 — barche disponibili
        try {
            List<Boat> available = boatService.findAvailable(
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(5),
                    null // tutti i tipi
            );
            System.out.println("Barche disponibili: " + available.size());
            available.forEach(b -> System.out.println("  " + b.getName()));
        } catch (Exception e) {
            System.err.println("findAvailable FALLITO: " + e.getMessage());
        }

// Test 4 — barca non esistente
        try {
            boatService.findById(9999);
        } catch (BoatNotFoundException e) {
            System.out.println("BoatNotFoundException OK: " + e.getMessage());
        }



        System.out.println("\n=== TEST RENTAL SERVICE ===");
        RentalDAO rentalDAO = new RentalDAOJdbcImpl();
        RentalService rentalService = new RentalServiceImpl(
                rentalDAO, memberDAO, boatDAO, bookingDAO );

// Test 1 — membro senza patente (Laura, id=2)
        try {
            RentalDTO dto = new RentalDTO(2, 1,
                    LocalDate.now().plusDays(3),
                    LocalDate.now().plusDays(6), 0);
            rentalService.createRental(dto);
        } catch (UnauthorizedOperationException e) {
            System.out.println("Patente richiesta OK: " + e.getMessage());
        }

// Test 2 — noleggio valido (Mario, id=1, ha la patente)
        try {
            RentalDTO dto = new RentalDTO(1, 3,
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(4), 2);
            Rental r = rentalService.createRental(dto);
            System.out.println("Rental creato OK — id=" + r.getId() +
                    " prezzo=" + r.getTotalPrice());
        } catch (Exception e) {
            System.err.println("createRental FALLITO: " + e.getMessage());
        }

// Test 3 — cancellazione
        try {
            List<Rental> futuri = rentalService.findFuture(1);
            if (!futuri.isEmpty()) {
                boolean cancelled = rentalService.cancelRental(futuri.get(0).getId());
                System.out.println("Cancellazione OK: " + cancelled);
            }
        } catch (Exception e) {
            System.err.println("cancelRental FALLITO: " + e.getMessage());
        }


        System.out.println("\n=== TEST BOOKING SERVICE ===");
        BookingService bookingService = new BookingServiceImpl(
                bookingDAO, memberDAO, boatDAO, skipperDAO, rentalDAO);

// Test 1 — prenotazione valida
// Laura (id=2, senza patente) può prenotare con skipper
        try {
            BookingDTO dto = new BookingDTO(
                    2, 1, null,
                    LocalDate.now().plusDays(2),
                    3, RegistrationType.INDIVIDUAL
            );
            Booking b = bookingService.createBooking(dto);
            System.out.println("Booking creato OK — id=" + b.getId() +
                    " prezzo=" + b.getTotalPrice() +
                    " skipperId=" + b.getSkipperId());
        } catch (Exception e) {
            System.err.println("createBooking FALLITO: " + e.getMessage());
        }

// Test 2 — barca senza skipper (Freccia Blu, id=3)
        try {
            BookingDTO dto = new BookingDTO(
                    2, 3, null,
                    LocalDate.now().plusDays(3),
                    2, RegistrationType.INDIVIDUAL
            );
            bookingService.createBooking(dto);
        } catch (IllegalArgumentException e) {
            System.out.println("Skipper mancante OK: " + e.getMessage());
        }

// Test 3 — troppi posti (Luna Rossa ha 8 posti, max 7 per passeggeri)
        try {
            BookingDTO dto = new BookingDTO(
                    2, 1, null,
                    LocalDate.now().plusDays(5),
                    8, RegistrationType.FAMILY
            );
            bookingService.createBooking(dto);
        } catch (IllegalArgumentException e) {
            System.out.println("Capienza superata OK: " + e.getMessage());
        }

// Test 4 — valutazione su booking futuro (deve fallire)
        try {
            List<Booking> bookings = bookingService.findByMemberId(2);
            if (!bookings.isEmpty()) {
                bookingService.rateSkipper(bookings.get(0).getId(), 4.5);
            }
        } catch (InvalidRatingException e) {
            System.out.println("Rating futuro bloccato OK: " + e.getMessage());
        }


        System.out.println("\n=== TEST SKIPPER SERVICE ===");
        SkipperService skipperService = new SkipperServiceImpl(
                skipperDAO, memberDAO);

// Test 1 — lista tutti gli skipper
        System.out.println("Skipper nel DB:");
        skipperService.findAll().forEach(s ->
                System.out.println("  id=" + s.getId() +
                        " name=" + s.getName() +
                        " rating=" + s.getAvgRating()));

// Test 2 — promuovi Laura a skipper (non ha patente — deve fallire)
        try {
            SkipperDTO dto = new SkipperDTO(2, 3,
                    "Patente B", 0.0, "Bio di test");
            skipperService.promoteToSkipper(dto);
        } catch (UnauthorizedOperationException e) {
            System.out.println("Patente mancante OK: " + e.getMessage());
        }

// Test 3 — promuovi Luca (id=3, ha la patente)
// Luca è già skipper nel default.sql — deve fallire
        try {
            SkipperDTO dto = new SkipperDTO(3, 3,
                    "Patente A", 0.0, "Bio Luca");
            skipperService.promoteToSkipper(dto);
        } catch (IllegalArgumentException e) {
            System.out.println("Già skipper OK: " + e.getMessage());
        }

        System.out.println("\n=== TEST REGISTRATION SERVICE ===");
        //RegistrationDAO registrationDAO = new RegistrationDAOJdbcImpl();
        RegistrationService registrationService = new RegistrationServiceImpl(
                registrationDAO, memberDAO);

// Test 1 — crea iscrizione per Laura (id=2)
        try {
            RegistrationDTO dto = new RegistrationDTO(
                    2, RegistrationType.INDIVIDUAL,
                    LocalDate.now().getYear()
            );
            Registration reg = registrationService.createRegistration(dto);
            System.out.println("Iscrizione creata OK — id=" + reg.getId() +
                    " tipo=" + reg.getType());
        } catch (Exception e) {
            System.err.println("createRegistration FALLITO: " + e.getMessage());
        }

// Test 2 — doppia iscrizione per Laura (deve fallire)
        try {
            RegistrationDTO dto = new RegistrationDTO(
                    2, RegistrationType.FAMILY,
                    LocalDate.now().getYear()
            );
            registrationService.createRegistration(dto);
        } catch (IllegalArgumentException e) {
            System.out.println("Doppia iscrizione bloccata OK: " + e.getMessage());
        }

// Test 3 — iscrizione per membro inesistente
        try {
            RegistrationDTO dto = new RegistrationDTO(
                    9999, RegistrationType.INDIVIDUAL,
                    LocalDate.now().getYear()
            );
            registrationService.createRegistration(dto);
        } catch (MemberNotFoundException e) {
            System.out.println("Membro inesistente OK: " + e.getMessage());
        }



    }
}