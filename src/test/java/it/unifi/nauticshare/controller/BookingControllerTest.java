package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dao.*;
import it.unifi.nauticshare.dto.BoatDTO;
import it.unifi.nauticshare.dto.BookingDTO;
import it.unifi.nauticshare.dto.RentalDTO;
import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.Booking;
import it.unifi.nauticshare.model.BoatType;
import it.unifi.nauticshare.model.RegistrationType;
import it.unifi.nauticshare.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingControllerTest {

    private BookingController bookingController;
    private RentalController rentalController;
    private BoatController boatController;
    private MemberDAO memberDAO;
    private BoatDAO boatDAO;
    private SkipperDAO skipperDAO;
    private RentalDAO rentalDAO;
    private BookingDAO bookingDAO;


    private static final int BOAT_WITH_SKIPPER_ID   = 1; // Luna Rossa
    private static final int MEMBER_ID              = 2; // Laura (no patente)
    private static final int MEMBER_WITH_LICENSE_ID = 1; // Mario (ha patente)

    // Usiamo date lontane per evitare conflitti con i booking/rental del default.sql
    private static final LocalDate FUTURE_DATE =
            LocalDate.now().plusDays(200);

    @BeforeEach
    void setUp() throws SQLException, IOException {
        memberDAO  = new MemberDAOJdbcImpl();
        boatDAO    = new BoatDAOJdbcImpl();
        skipperDAO = new SkipperDAOJdbcImpl();
        rentalDAO  = new RentalDAOJdbcImpl();
        bookingDAO = new BookingDAOJdbcImpl();

        BookingService bookingService = new BookingServiceImpl(
                bookingDAO, memberDAO, boatDAO, skipperDAO, rentalDAO);
        bookingController = new BookingController(bookingService);

        RentalService rentalService = new RentalServiceImpl(
                rentalDAO, memberDAO, boatDAO, bookingDAO);
        rentalController = new RentalController(rentalService);

        BoatService boatService = new BoatServiceImpl(boatDAO, skipperDAO);
        boatController = new BoatController(boatService);

        resetDatabase();
    }

    private void resetDatabase() throws SQLException, IOException {
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(
                    "TRUNCATE TABLE registration, booking, rental, " +
                            "skipper, boat, member RESTART IDENTITY CASCADE");
        }
        executeSqlFile("sql/schema.sql");
        executeSqlFile("sql/default.sql");
    }

    private void executeSqlFile(String fileName)
            throws SQLException, IOException {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(fileName)) {
            if (is == null) {
                throw new IOException("File SQL non trovato: " + fileName);
            }
            String sql = new String(
                    is.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection conn = ConnectionManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 17 — Booking valido con skipper (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #17 — Booking con skipper disponibile deve riuscire")
    void testCreateBookingSuccess() {
        BookingDTO dto = new BookingDTO(
                MEMBER_ID,            // Laura
                BOAT_WITH_SKIPPER_ID, // Luna Rossa (SAILBOAT, skipper Mario)
                null,
                FUTURE_DATE,
                3,                    // 3 posti (max 7 = 8-1 skipper)
                RegistrationType.INDIVIDUAL
        );

        Booking result = bookingController.createBooking(dto);

        assertNotNull(result, "Il booking con skipper deve essere creato");
        assertTrue(result.getId() > 0, "L'id deve essere assegnato dal DB");
        assertNotNull(result.getSkipperId(),
                "Lo skipperId deve essere assegnato automaticamente");
        assertEquals(MEMBER_ID, result.getMemberId());
        assertEquals(BOAT_WITH_SKIPPER_ID, result.getBoatId());
        assertEquals(FUTURE_DATE, result.getDate());
        assertEquals(3, result.getSeatsBooked());

        // SAILBOAT: 3 posti × €80 = €240
        assertEquals(240.0, result.getTotalPrice(), 0.01,
                "Il prezzo per 3 posti su SAILBOAT deve essere €240");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 18 — Booking su barca senza skipper (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #18 — Booking su barca senza skipper deve fallire")
    void testCreateBookingNoSkipper() {
        // Creiamo una barca senza skipper appositamente per questo test
        // Usiamo un regNum univoco che non esiste nel default.sql
        BoatDTO newBoatDto = new BoatDTO(
                "IT-NOSK-001", "Barca Senza Skipper",
                BoatType.SPEEDBOAT, 4, null, "Test barca senza skipper"
        );
        Boat boatWithoutSkipper = boatController.addBoat(newBoatDto);
        assertNotNull(boatWithoutSkipper,
                "La barca di test deve essere creata");

        //Tentiamo un booking su questa barca, non ha skipper assegnato
        BookingDTO dto = new BookingDTO(
                MEMBER_ID,
                boatWithoutSkipper.getId(),
                null,
                FUTURE_DATE,
                2,
                RegistrationType.INDIVIDUAL
        );

        Booking result = bookingController.createBooking(dto);

        assertNull(result,
                "Il booking su barca senza skipper deve essere bloccato");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 19 — Booking con troppi posti (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #19 — Booking con posti superiori alla capienza deve fallire")
    void testCreateBookingExceedsCapacity() {
        BookingDTO dto = new BookingDTO(
                MEMBER_ID,
                BOAT_WITH_SKIPPER_ID, // Luna Rossa —> 8 posti totali, max 7
                null,
                FUTURE_DATE,
                8, // 8 > 7 (8-1 skipper) → deve fallire
                RegistrationType.INDIVIDUAL
        );

        Booking result = bookingController.createBooking(dto);

        assertNull(result,
                "Il booking con posti superiori alla capienza deve essere bloccato");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 20 — Booking con data passata (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #20 — Booking con data passata deve essere bloccato")
    void testCreateBookingPastDate() {
        BookingDTO dto = new BookingDTO(
                MEMBER_ID,
                BOAT_WITH_SKIPPER_ID,
                null,
                LocalDate.now().minusDays(1), // ieri
                2,
                RegistrationType.INDIVIDUAL
        );

        Booking result = bookingController.createBooking(dto);

        assertNull(result,
                "Il booking con data passata deve essere bloccato");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 21 — Overlap Booking vs Booking (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #21 — Due booking stesso giorno stessa barca devono fallire")
    void testCreateBookingOverlapWithBooking() {
        BookingDTO firstDto = new BookingDTO(
                MEMBER_ID,
                BOAT_WITH_SKIPPER_ID,
                null,
                FUTURE_DATE,
                2,
                RegistrationType.INDIVIDUAL
        );
        Booking first = bookingController.createBooking(firstDto);
        assertNotNull(first, "Il primo booking deve essere creato");

        BookingDTO secondDto = new BookingDTO(
                MEMBER_WITH_LICENSE_ID, // utente diverso
                BOAT_WITH_SKIPPER_ID,   // stessa barca
                null,
                FUTURE_DATE,            // stesso giorno
                3,
                RegistrationType.INDIVIDUAL
        );

        Booking second = bookingController.createBooking(secondDto);

        assertNull(second,
                "Il secondo booking sulla stessa barca nello stesso giorno " +
                        "deve essere bloccato");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 22 — Overlap Booking vs Rental (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #22 — Booking su barca in noleggio deve fallire")
    void testCreateBookingOverlapWithRental() {
        // Creiamo un rental su Luna Rossa che copre FUTURE_DATE
        LocalDate rentalStart = FUTURE_DATE.minusDays(2);
        LocalDate rentalEnd   = FUTURE_DATE.plusDays(2);

        RentalDTO rentalDto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID, // Mario
                BOAT_WITH_SKIPPER_ID,   // Luna Rossa
                rentalStart,
                rentalEnd,
                0
        );
        var rental = rentalController.createRental(rentalDto);
        assertNotNull(rental, "Il rental su Luna Rossa deve essere creato");

        // Tentiamo booking su Luna Rossa nello stesso periodo
        BookingDTO bookingDto = new BookingDTO(
                MEMBER_ID,             // Laura
                BOAT_WITH_SKIPPER_ID,  // Luna Rossa — occupata dal rental
                null,
                FUTURE_DATE,
                3,
                RegistrationType.INDIVIDUAL
        );

        Booking result = bookingController.createBooking(bookingDto);

        assertNull(result,
                "Il booking su barca in noleggio deve essere bloccato");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 23 — findByMemberId (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #23 — findByMemberId deve restituire i booking del membro")
    void testFindByMemberId() {
        BookingDTO dto = new BookingDTO(
                MEMBER_ID,
                BOAT_WITH_SKIPPER_ID,
                null,
                FUTURE_DATE,
                2,
                RegistrationType.INDIVIDUAL
        );
        bookingController.createBooking(dto);

        List<Booking> bookings = bookingController.findByMemberId(MEMBER_ID);

        assertNotNull(bookings, "La lista non deve essere null");
        assertFalse(bookings.isEmpty(),
                "Laura deve avere almeno un booking");

        // Verifica che tutti i booking appartengano a Laura
        assertTrue(
                bookings.stream().allMatch(b -> b.getMemberId() == MEMBER_ID),
                "Tutti i booking devono appartenere al membro richiesto");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 24 — Cancellazione booking futuro (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #24 — Cancellazione booking futuro deve riuscire")
    void testCancelFutureBooking() {
        BookingDTO dto = new BookingDTO(
                MEMBER_ID,
                BOAT_WITH_SKIPPER_ID,
                null,
                FUTURE_DATE,
                2,
                RegistrationType.INDIVIDUAL
        );
        Booking created = bookingController.createBooking(dto);
        assertNotNull(created,
                "Il booking deve esistere prima della cancellazione");

        boolean result = bookingController.cancelBooking(created.getId());

        assertTrue(result,
                "La cancellazione di un booking futuro deve riuscire");

        Booking deleted = bookingController.findById(created.getId());
        assertNull(deleted,
                "Il booking cancellato non deve essere più trovabile");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 25 — Rating non valido (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #25 — Valutazione skipper con rating > 5 deve fallire")
    void testRateSkipperInvalidRating() {
        BookingDTO dto = new BookingDTO(
                MEMBER_ID,
                BOAT_WITH_SKIPPER_ID,
                null,
                FUTURE_DATE,
                2,
                RegistrationType.INDIVIDUAL
        );
        Booking booking = bookingController.createBooking(dto);
        assertNotNull(booking);

        boolean result = bookingController.rateSkipper(booking.getId(), 6.0);

        assertFalse(result,
                "Una valutazione > 5.0 deve essere bloccata");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 26 — Rating su booking futuro (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #26 — Valutazione skipper su booking futuro deve fallire")
    void testRateSkipperFutureBooking() {
        BookingDTO dto = new BookingDTO(
                MEMBER_ID,
                BOAT_WITH_SKIPPER_ID,
                null,
                FUTURE_DATE,
                2,
                RegistrationType.INDIVIDUAL
        );
        Booking booking = bookingController.createBooking(dto);
        assertNotNull(booking);

        boolean result = bookingController.rateSkipper(booking.getId(), 4.5);

        assertFalse(result,
                "Non si può valutare uno skipper per un'uscita non ancora avvenuta");
    }
}