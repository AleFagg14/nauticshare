package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dao.*;
import it.unifi.nauticshare.dto.RentalDTO;
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

class RentalControllerTest {


    private RentalController rentalController;
    private MemberDAO memberDAO;
    private BoatDAO boatDAO;
    private RentalDAO rentalDAO;
    private BookingDAO bookingDAO;

    // Usiamo costanti per avere date sempre future e coerenti tra i test.
    private static final LocalDate START_DATE =
            LocalDate.now().plusDays(10);
    private static final LocalDate END_DATE =
            LocalDate.now().plusDays(13);

    // Id fissi dal default.sql — li usiamo nei test per riferirci
    // a dati già presenti nel DB senza doverli ricreare ogni volta
    private static final int MEMBER_WITH_LICENSE_ID    = 1; // Mario
    private static final int MEMBER_WITHOUT_LICENSE_ID = 2; // Laura
    private static final int BOAT_ID                   = 3; // Freccia Blu

    @BeforeEach
    void setUp() throws SQLException, IOException {
        // Inizializza tutti i DAO — usiamo il DB reale
        memberDAO  = new MemberDAOJdbcImpl();
        boatDAO    = new BoatDAOJdbcImpl();
        rentalDAO  = new RentalDAOJdbcImpl();
        bookingDAO = new BookingDAOJdbcImpl();

        // Costruisce la catena completa
        // RentalService ha bisogno di 4 DAO perché verifica
        // sia i Rental (overlap) che i Booking (overlap sul giorno)
        RentalService rentalService = new RentalServiceImpl(
                rentalDAO, memberDAO, boatDAO, bookingDAO);
        rentalController = new RentalController(rentalService);

        // Reset DB prima di ogni test — stato pulito garantito
        resetDatabase();
    }

    private void resetDatabase() throws SQLException, IOException {
        executeSqlFile("sql/reset.sql");
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
            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection conn = ConnectionManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 9 — Noleggio diretto con patente (Happy Path)
    // Verifica UC-10 flusso base completo
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #9 — Noleggio diretto con patente deve riuscire")
    void testCreateRentalWithLicenseSuccess() {
        // ARRANGE — Mario (id=1) ha la patente, Freccia Blu (id=3) è libera
        RentalDTO dto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID,  // memberId = 1 (Mario, ha patente)
                BOAT_ID,                 // boatId = 3 (Freccia Blu, SPEEDBOAT)
                START_DATE,              // 10 giorni da oggi
                END_DATE,                // 13 giorni da oggi → 3 giorni
                0                        // nessun partecipante aggiuntivo
        );

        // ACT
        Rental result = rentalController.createRental(dto);

        // ASSERT
        assertNotNull(result,
                "Il rental con patente deve essere creato");


        assertTrue(result.getId() > 0,
                "L'id del rental deve essere assegnato dal DB");


        assertEquals(MEMBER_WITH_LICENSE_ID, result.getMemberId(),
                "Il memberId deve corrispondere");
        assertEquals(BOAT_ID, result.getBoatId(),
                "Il boatId deve corrispondere");
        assertEquals(START_DATE, result.getStartDate(),
                "La data di inizio deve corrispondere");

        // Verifica il prezzo calcolato correttamente
        assertEquals(750.0, result.getTotalPrice(), 0.01,
                "Il prezzo per 3 giorni di SPEEDBOAT deve essere €750");
        // Il terzo parametro (0.01) è la tolleranza per i double
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 10 — Noleggio senza patente (Error Path)
    // Verifica UC-10 flusso alternativo 3a — regola di business principale
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #10 — Noleggio diretto senza patente deve essere bloccato")
    void testCreateRentalWithoutLicense() {
        // ARRANGE
        // Questa è la regola di business più importante del sistema:
        // il noleggio diretto richiede la patente nautica
        RentalDTO dto = new RentalDTO(
                MEMBER_WITHOUT_LICENSE_ID, // Laura — senza patente
                BOAT_ID,
                START_DATE,
                END_DATE,
                0
        );

        // ACT
        Rental result = rentalController.createRental(dto);

        // ASSERT — il Controller deve restituire null
        assertNull(result,
                "Il noleggio senza patente deve essere bloccato (null)");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 11 — Noleggio con partecipanti aggiuntivi e prezzo corretto
    // Verifica il calcolo del prezzo con partecipanti extra
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #11 — Prezzo con partecipanti aggiuntivi deve essere corretto")
    void testCreateRentalWithParticipantsPrice() {
        // ARRANGE
        RentalDTO dto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID,
                BOAT_ID,
                START_DATE,
                END_DATE,
                2  // 2 partecipanti aggiuntivi
        );

        // ACT
        Rental result = rentalController.createRental(dto);

        // ASSERT
        assertNotNull(result, "Il rental deve essere creato");

        assertEquals(800.0, result.getTotalPrice(), 0.01,
                "Il prezzo con 2 partecipanti deve essere €800");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 12 — Overlap: stessa barca, date sovrapposte (Error Path)
    // Verifica UC-10 flusso alternativo 4a — conflitto disponibilità
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #12 — Rental su barca già occupata deve fallire")
    void testCreateRentalOverlap() {
        // ARRANGE
        RentalDTO firstDto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID,
                BOAT_ID,
                START_DATE,
                END_DATE,
                0
        );
        Rental first = rentalController.createRental(firstDto);
        assertNotNull(first, "Il primo rental deve essere creato");

        // Proviamo a creare un secondo rental con date sovrapposte
        RentalDTO overlappingDto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID,
                BOAT_ID,
                START_DATE.plusDays(1), // inizia un giorno dopo → dentro il primo
                END_DATE.plusDays(2),   // finisce dopo
                0
        );

        // ACT
        Rental result = rentalController.createRental(overlappingDto);

        // ASSERT — il secondo rental deve essere bloccato
        assertNull(result,
                "Un rental su barca già occupata deve essere bloccato (null)");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 13 — Rental con data di inizio nel passato (Error Path)
    // Verifica validazione date — UC-10 validazione input
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #13 — Rental con data passata deve essere bloccato")
    void testCreateRentalPastDate() {
        // ARRANGE — data di inizio nel passato
        RentalDTO dto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID,
                BOAT_ID,
                LocalDate.now().minusDays(1), // ieri — data passata
                LocalDate.now().plusDays(2),
                0
        );

        // ACT
        Rental result = rentalController.createRental(dto);

        // ASSERT — il Service deve bloccare date passate
        assertNull(result,
                "Un rental con data di inizio passata deve essere bloccato");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 14 — findFuture restituisce solo rental futuri (Happy Path)
    // Verifica che findFuture filtri correttamente
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #14 — findFuture deve restituire solo i rental futuri")
    void testFindFutureRentals() {
        // ARRANGE — creiamo un rental futuro
        RentalDTO dto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID,
                BOAT_ID,
                START_DATE,
                END_DATE,
                0
        );
        rentalController.createRental(dto);

        // ACT
        List<Rental> futureRentals =
                rentalController.findFuture(MEMBER_WITH_LICENSE_ID);

        // ASSERT
        assertNotNull(futureRentals,
                "La lista dei rental futuri non deve essere null");
        assertFalse(futureRentals.isEmpty(),
                "Ci deve essere almeno un rental futuro");

        // Verifica che tutti i rental restituiti abbiano startDate futura
        // Questo è il controllo più importante — findFuture non deve
        // restituire rental passati
        assertTrue(
                futureRentals.stream()
                        .allMatch(r -> r.getStartDate().isAfter(LocalDate.now())),
                "Tutti i rental restituiti devono avere startDate futura"
        );
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 15 — Cancellazione rental futuro (Happy Path)
    // Verifica UC-11 flusso base
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #15 — Cancellazione rental futuro deve riuscire")
    void testCancelFutureRental() {
        // ARRANGE — creiamo un rental futuro da cancellare
        RentalDTO dto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID,
                BOAT_ID,
                START_DATE,
                END_DATE,
                0
        );
        Rental created = rentalController.createRental(dto);
        assertNotNull(created, "Il rental deve essere creato prima della cancellazione");

        // ACT — cancella il rental
        boolean result = rentalController.cancelRental(created.getId());

        // ASSERT
        assertTrue(result,
                "La cancellazione di un rental futuro deve riuscire");

        // Verifica che il rental non sia più presente nel DB
        Rental deleted = rentalController.findById(created.getId());
        assertNull(deleted,
                "Il rental cancellato non deve essere più recuperabile");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 16 — addParticipant aggiorna il prezzo correttamente
    // Verifica UC-13 flusso base con ricalcolo prezzo
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #16 — addParticipant deve aggiornare prezzo nel DB")
    void testAddParticipantUpdatesPrice() {
        // ARRANGE — creiamo un rental senza partecipanti
        RentalDTO dto = new RentalDTO(
                MEMBER_WITH_LICENSE_ID,
                BOAT_ID,
                START_DATE,
                END_DATE,
                0  // partenza da 0 partecipanti
        );
        Rental created = rentalController.createRental(dto);
        assertNotNull(created);

        assertEquals(750.0, created.getTotalPrice(), 0.01,
                "Il prezzo iniziale senza partecipanti deve essere €750");

        // ACT
        boolean added = rentalController.addParticipant(created.getId());

        // ASSERT
        assertTrue(added, "L'aggiunta del partecipante deve riuscire");

        // Recupera il rental aggiornato dal DB per verificare il nuovo prezzo
        Rental updated = rentalController.findById(created.getId());
        assertNotNull(updated, "Il rental aggiornato deve essere recuperabile");

        assertEquals(775.0, updated.getTotalPrice(), 0.01,
                "Il prezzo dopo aggiunta partecipante deve essere €775");

        assertEquals(1, updated.getNumParticipants(),
                "Il numero di partecipanti deve essere 1 dopo l'aggiunta");
    }
}