package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dao.*;
import it.unifi.nauticshare.dto.BoatDTO;
import it.unifi.nauticshare.dto.RentalDTO;
import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.BoatType;
import it.unifi.nauticshare.model.Rental;
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

class BoatControllerTest {

    private BoatController boatController;
    private RentalController rentalController;
    private MemberDAO memberDAO;
    private BoatDAO boatDAO;
    private SkipperDAO skipperDAO;
    private RentalDAO rentalDAO;
    private BookingDAO bookingDAO;


    private static final int EXISTING_BOAT_ID        = 1;  // Luna Rossa
    private static final int MEMBER_WITH_LICENSE_ID  = 1;  // Mario

    private static final int BOAT_FOR_RENTAL_ID = 10; // Onda Chiara SPEEDBOAT 5 posti

    private static final LocalDate START_DATE =
            LocalDate.now().plusDays(60); // Usiamo date lontane per evitare conflitti con i rental del default.sql
    private static final LocalDate END_DATE =
            LocalDate.now().plusDays(63);

    @BeforeEach
    void setUp() throws SQLException, IOException {
        memberDAO  = new MemberDAOJdbcImpl();
        boatDAO    = new BoatDAOJdbcImpl();
        skipperDAO = new SkipperDAOJdbcImpl();
        rentalDAO  = new RentalDAOJdbcImpl();
        bookingDAO = new BookingDAOJdbcImpl();

        BoatService boatService =
                new BoatServiceImpl(boatDAO, skipperDAO);
        boatController = new BoatController(boatService);

        RentalService rentalService = new RentalServiceImpl(
                rentalDAO, memberDAO, boatDAO, bookingDAO);
        rentalController = new RentalController(rentalService);

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
    // TEST 27 — Aggiunta barca con dati validi (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #27 — Aggiunta barca con dati validi deve riuscire")
    void testAddBoatSuccess() {
        BoatDTO dto = new BoatDTO(
                "IT-TEST-999", "Barca Nuova", BoatType.YACHT,
                10, null, "Barca di test"
        );

        Boat result = boatController.addBoat(dto);

        assertNotNull(result, "La barca aggiunta non deve essere null");
        assertTrue(result.getId() > 0, "L'id deve essere assegnato dal DB");
        assertEquals("IT-TEST-999", result.getRegNum());
        assertEquals("Barca Nuova", result.getName());
        assertEquals(BoatType.YACHT, result.getType());
        assertEquals(10, result.getSeats());
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 28 — regNum duplicato (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #28 — Aggiunta barca con regNum duplicato deve fallire")
    void testAddBoatDuplicateRegNum() {
        // IT-FI-001 è Luna Rossa — presente nel default.sql
        BoatDTO dto = new BoatDTO(
                "IT-FI-001", "Altra Barca", BoatType.SAILBOAT,
                6, null, "Duplicato"
        );

        Boat result = boatController.addBoat(dto);

        assertNull(result,
                "L'aggiunta con regNum duplicato deve essere bloccata");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 29 — Posti = 0 (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #29 — Aggiunta barca con posti = 0 deve fallire")
    void testAddBoatZeroSeats() {
        BoatDTO dto = new BoatDTO(
                "IT-TEST-000", "Barca Senza Posti",
                BoatType.SPEEDBOAT, 0, null, "Test"
        );

        Boat result = boatController.addBoat(dto);

        assertNull(result, "Una barca con 0 posti non deve essere aggiunta");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 30 — findAll restituisce tutte le 20 barche (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #30 — findAll deve restituire le 20 barche del default.sql")
    void testFindAllBoats() {
        List<Boat> boats = boatController.findAll();

        assertNotNull(boats, "La lista non deve essere null");
        assertFalse(boats.isEmpty(), "Ci devono essere barche nel catalogo");


        assertEquals(20, boats.size(),
                "Devono esserci esattamente 20 barche dal default.sql");


        assertTrue(boats.stream().anyMatch(b ->
                        b.getId() == 1 && b.getName().equals("Luna Rossa")),
                "Luna Rossa (id=1) deve essere presente");
        assertTrue(boats.stream().anyMatch(b ->
                        b.getId() == 2 && b.getName().equals("Poseidon")),
                "Poseidon (id=2) deve essere presente");
        assertTrue(boats.stream().anyMatch(b ->
                        b.getId() == 3 && b.getName().equals("Freccia Blu")),
                "Freccia Blu (id=3) deve essere presente");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 31 — findAvailable con periodo libero (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #31 — findAvailable con periodo libero deve trovare barche")
    void testFindAvailableAllFree() {
        List<Boat> available = boatController.findAvailable(
                START_DATE, END_DATE, null);

        assertNotNull(available, "La lista non deve essere null");
        assertFalse(available.isEmpty(),
                "Ci devono essere barche disponibili nel periodo scelto");

        // Con date lontane (60+ giorni) tutti i rental del default.sql
        // sono già finiti e tutte le barche devono essere disponibili
        assertEquals(20, available.size(),
                "Tutte e 20 le barche devono essere disponibili " +
                        "nel periodo scelto (nessun rental esistente)");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 32 — findAvailable esclude barche in noleggio (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #32 — findAvailable deve escludere barche in noleggio")
    void testFindAvailableExcludesRentedBoat() {

        RentalDTO rentalDto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID, // Mario
                BOAT_FOR_RENTAL_ID,     // Onda Chiara (id=10)
                START_DATE,
                END_DATE,
                0
        );
        Rental rental = rentalController.createRental(rentalDto);
        assertNotNull(rental, "Il rental deve essere creato");

        List<Boat> available = boatController.findAvailable(
                START_DATE, END_DATE, null);

        // 20 barche - 1 occupata = 19 disponibili
        assertEquals(19, available.size(),
                "Devono esserci 19 barche disponibili con Onda Chiara occupata");

        // Verifica che Onda Chiara non sia nella lista
        assertFalse(
                available.stream().anyMatch(b -> b.getId() == BOAT_FOR_RENTAL_ID),
                "Onda Chiara non deve apparire tra le disponibili");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 33 — findAvailable con filtro SAILBOAT (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #33 — findAvailable con filtro SAILBOAT deve trovare le barche corrette")
    void testFindAvailableFilterByType() {
        List<Boat> sailboats = boatController.findAvailable(
                START_DATE, END_DATE, BoatType.SAILBOAT);

        assertNotNull(sailboats, "La lista non deve essere null");
        assertFalse(sailboats.isEmpty(),
                "Ci devono essere SAILBOAT disponibili");

        assertEquals(9, sailboats.size(),
                "Devono esserci esattamente 9 SAILBOAT nel catalogo");

        assertTrue(
                sailboats.stream().allMatch(b -> b.getType() == BoatType.SAILBOAT),
                "Tutti i risultati devono essere di tipo SAILBOAT");

        // Verifica che Luna Rossa sia presente
        assertTrue(
                sailboats.stream().anyMatch(b -> b.getName().equals("Luna Rossa")),
                "Luna Rossa deve essere presente tra le SAILBOAT");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 34 — date invertite restituisce lista vuota (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #34 — findAvailable con date invertite deve restituire lista vuota")
    void testFindAvailableInvalidDates() {
        List<Boat> result = boatController.findAvailable(
                END_DATE, START_DATE, null); // date invertite

        assertNotNull(result,
                "Il risultato non deve essere null con date invalide");
        assertTrue(result.isEmpty(),
                "Con date invalide deve essere restituita lista vuota");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 35 — update barca (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #35 — Update barca esistente deve aggiornare i dati")
    void testUpdateBoatSuccess() {
        BoatDTO updatedDto = new BoatDTO(
                "IT-FI-001", "Luna Rossa Aggiornata",
                BoatType.SAILBOAT, 10,
                "https://nuova-foto.jpg", "Descrizione aggiornata"
        );

        boolean result = boatController.update(EXISTING_BOAT_ID, updatedDto);

        assertTrue(result, "L'update deve riuscire");

        Boat updated = boatController.findById(EXISTING_BOAT_ID);
        assertNotNull(updated);
        assertEquals("Luna Rossa Aggiornata", updated.getName());
        assertEquals(10, updated.getSeats());
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 36 — delete barca (Happy Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #36 — Delete barca esistente deve rimuoverla dal catalogo")
    void testDeleteBoatSuccess() {
        BoatDTO dto = new BoatDTO(
                "IT-DELETE-001", "Barca Da Eliminare",
                BoatType.SPEEDBOAT, 4, null, "Temporanea"
        );
        Boat toDelete = boatController.addBoat(dto);
        assertNotNull(toDelete, "La barca deve essere creata prima del delete");

        boolean result = boatController.delete(toDelete.getId());

        assertTrue(result, "Il delete deve riuscire");

        Boat deleted = boatController.findById(toDelete.getId());
        assertNull(deleted, "La barca eliminata non deve essere più trovabile");

        // Le 20 barche originali del default.sql devono essere ancora presenti
        List<Boat> remaining = boatController.findAll();
        assertEquals(20, remaining.size(),
                "Le 20 barche originali devono essere ancora presenti");
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 37 — findById inesistente (Error Path)
    // ════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #37 — findById con id inesistente deve restituire null")
    void testFindByIdNotFound() {
        Boat result = boatController.findById(9999);

        assertNull(result,
                "findById con id inesistente deve restituire null");
    }
}