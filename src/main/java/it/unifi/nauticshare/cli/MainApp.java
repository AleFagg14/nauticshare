package it.unifi.nauticshare.cli;

import it.unifi.nauticshare.dao.*;
import it.unifi.nauticshare.dto.*;
import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.service.*;

import java.time.LocalDate;

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
    }
}