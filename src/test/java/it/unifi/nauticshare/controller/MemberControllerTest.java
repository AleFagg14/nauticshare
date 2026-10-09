package it.unifi.nauticshare.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unifi.nauticshare.dao.*;
import it.unifi.nauticshare.dto.LoginDTO;
import it.unifi.nauticshare.dto.MemberDTO;
import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.service.MemberService;
import it.unifi.nauticshare.service.MemberServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

// Asserts statici — import static per scrivere assertNotNull() invece di Assertions.assertNotNull()
import static org.junit.jupiter.api.Assertions.*;

// Questa classe testa MemberController usando il DB reale.
class MemberControllerTest {


    private MemberController memberController;
    private MemberDAO memberDAO;
    private RegistrationDAO registrationDAO;

    // ── @BeforeEach ──────────────────────────────────────────────────────────
    // Questo metodo viene eseguito PRIMA DI OGNI singolo @Test.
    // Resetta il DB a uno stato pulito e ricrea la catena di dipendenze.
    // Senza questo ogni test potrebbe essere influenzato dai dati
    // lasciati dal test precedente.
    @BeforeEach
    void setUp() throws SQLException, IOException {
        // Inizializza i DAO concreti — usiamo il DB reale
        memberDAO       = new MemberDAOJdbcImpl();
        registrationDAO = new RegistrationDAOJdbcImpl();


        // Stesso wiring che fa MainApp — ma qui lo facciamo nei test
        MemberService memberService =
                new MemberServiceImpl(memberDAO, registrationDAO);
        memberController = new MemberController(memberService);

        // Resetta e ripopola il DB prima di ogni test
        // Così ogni test parte sempre dagli stessi  membri del default.sql
        resetDatabase();
    }


    // Esegue reset.sql + schema.sql + default.sql in sequenza.
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
    // TEST 1 — Signup con dati validi (Happy Path)
    // Verifica UC-0: un nuovo utente si registra correttamente
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #1 — Signup con dati validi deve creare il membro")
    void testSignupSuccess() {
        // ARRANGE
        MemberDTO dto = new MemberDTO(
                "Giuseppe", "Verdi", "giuseppe.verdi@test.it",
                "password123", "Firenze",
                LocalDate.of(1990, 5, 20), false
        );

        // ACT
        Member result = memberController.signup(dto);

        // ASSERT
        assertNotNull(result,
                "Il membro creato non deve essere null");

        // assertEquals: verifica che i dati salvati corrispondano all'input
        assertEquals("Giuseppe", result.getName(),
                "Il nome deve corrispondere");
        assertEquals("giuseppe.verdi@test.it", result.getEmail(),
                "L'email deve corrispondere");

        // assertTrue: verifica che l'id sia stato assegnato dal DB
        // id > 0 significa che PostgreSQL ha assegnato un SERIAL valido
        assertTrue(result.getId() > 0,
                "L'id deve essere > 0 dopo l'inserimento nel DB");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 2 — Signup con email duplicata (Error Path)
    // Verifica UC-0 flusso alternativo 4a: email già registrata
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #2 — Signup con email già esistente deve fallire")
    void testSignupDuplicateEmail() {
        // ARRANGE — mario.rossi@email.it è già nel DB
        MemberDTO dto = new MemberDTO(
                "Mario", "Duplicato", "mario.rossi@email.it",
                "password123", "Roma",
                LocalDate.of(1985, 3, 15), false
        );

        // ACT
        Member result = memberController.signup(dto);

        // ASSERT
        assertNull(result,
                "Il signup con email duplicata deve restituire null");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 3 — Login con credenziali corrette (Happy Path)
    // Verifica UC-1 flusso base
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #3 — Login con credenziali corrette deve autenticare")
    void testLoginSuccess() {
        // ARRANGE
        MemberDTO signupDto = new MemberDTO(
                "Test", "Login", "test.login@test.it",
                "mypassword", "Milano",
                LocalDate.of(1995, 8, 10), false
        );
        memberController.signup(signupDto);


        LoginDTO loginDto = new LoginDTO("test.login@test.it", "mypassword");

        // ACT
        Member result = memberController.login(loginDto);

        // ASSERT
        assertNotNull(result,
                "Il login con credenziali corrette deve restituire il membro");
        assertEquals("test.login@test.it", result.getEmail(),
                "L'email del membro autenticato deve corrispondere");
        assertEquals("Test", result.getName(),
                "Il nome del membro autenticato deve corrispondere");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 4 — Login con password errata (Error Path)
    // Verifica UC-1 flusso alternativo 4b
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #4 — Login con password errata deve fallire")
    void testLoginWrongPassword() {
        // ARRANGE
        MemberDTO signupDto = new MemberDTO(
                "Test", "WrongPwd", "wrongpwd@test.it",
                "passwordgiusta", "Roma",
                LocalDate.of(1990, 1, 1), false
        );
        memberController.signup(signupDto);


        LoginDTO loginDto = new LoginDTO("wrongpwd@test.it", "passwordsbagliata");

        // ACT
        Member result = memberController.login(loginDto);

        // ASSERT
        assertNull(result,
                "Il login con password errata deve restituire null");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 5 — Login con email non esistente (Error Path)
    // Verifica UC-1 flusso alternativo 4a
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #5 — Login con email inesistente deve fallire")
    void testLoginEmailNotFound() {
        // ARRANGE
        LoginDTO loginDto = new LoginDTO(
                "nonexistent@test.it", "qualsiasi");

        // ACT
        Member result = memberController.login(loginDto);

        // ASSERT
        assertNull(result,
                "Il login con email inesistente deve restituire null");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 6 — findAll restituisce lista non vuota
    // Verifica UC-3 (Gestisci Membri) flusso base
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #6 — findAll deve restituire i membri del default.sql")
    void testFindAllReturnsMembers() {
        // ACT , il DB ha già i dati del default.sql
        List<Member> members = memberController.findAll();

        // ASSERT
        assertNotNull(members, "La lista non deve essere null");


        assertFalse(members.isEmpty(),
                "Ci devono essere membri nel DB dopo il default.sql");


        assertEquals(20, members.size(),
                "Devono esserci esattamente 20 membri dal default.sql");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 7 — delete membro senza iscrizione (Happy Path)
    // Verifica UC-3 elimina membro — flusso base
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #7 — delete membro senza iscrizione deve riuscire")
    void testDeleteMemberSuccess() {
        // ARRANGE
        MemberDTO dto = new MemberDTO(
                "Da", "Eliminare", "da.eliminare@test.it",
                "password", "Torino",
                LocalDate.of(1988, 6, 15), false
        );
        Member created = memberController.signup(dto);
        assertNotNull(created, "Il membro deve essere creato prima del delete");

        // ACT
        boolean result = memberController.delete(created.getId());

        // ASSERT
        assertTrue(result, "Il delete di un membro senza iscrizione deve riuscire");

        // Verifica che il membro non sia più recuperabile
        Member deleted = memberController.findById(created.getId());
        assertNull(deleted, "Il membro eliminato non deve essere più trovabile");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TEST 8 — delete membro con iscrizione attiva (Error Path)
    // Verifica UC-3 elimina membro — flusso alternativo 3a
    // ════════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Test #8 — delete membro con iscrizione attiva deve fallire")
    void testDeleteMemberWithActiveRegistration() {
        // ARRANGE
        MemberDTO dto = new MemberDTO(
                "Con", "Iscrizione", "con.iscrizione@test.it",
                "password", "Bologna",
                LocalDate.of(1992, 3, 10), false
        );
        Member created = memberController.signup(dto);
        assertNotNull(created);

        // Creiamo l'iscrizione per questo membro
        RegistrationController registrationController =
                new RegistrationController(
                        new it.unifi.nauticshare.service.RegistrationServiceImpl(
                                registrationDAO, memberDAO));

        registrationController.createRegistration(
                new it.unifi.nauticshare.dto.RegistrationDTO(
                        created.getId(),
                        it.unifi.nauticshare.model.RegistrationType.INDIVIDUAL,
                        2026
                )
        );

        // ACT
        boolean result = memberController.delete(created.getId());

        // ASSERT
        assertFalse(result,
                "Il delete di un membro con iscrizione attiva deve restituire false");
    }
}