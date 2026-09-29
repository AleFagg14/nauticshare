package it.unifi.nauticshare.cli;

import it.unifi.nauticshare.dao.*;
import it.unifi.nauticshare.dto.*;
import it.unifi.nauticshare.exception.BoatNotFoundException;
import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.BoatType;
import it.unifi.nauticshare.model.Member;
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


    }
}