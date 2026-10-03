package it.unifi.nauticshare.cli;

import it.unifi.nauticshare.controller.*;
import it.unifi.nauticshare.dao.*;
import it.unifi.nauticshare.dto.LoginDTO;
import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.model.Skipper;
import it.unifi.nauticshare.service.*;

import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {

        // ── Verifica connessione DB ──────────────────────────────────────────
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║     Benvenuto in NauticShare     ║");
        System.out.println("╚══════════════════════════════════╝");

        if (!ConnectionManager.testConnection()) {
            System.err.println("Impossibile connettersi al database.");
            System.err.println("Verifica db.properties e che PostgreSQL sia attivo.");
            return;
        }
        System.out.println("Connessione al database: OK\n");

        // ── Inizializzazione DAO ─────────────────────────────────────────────
        // Tutti i DAO vengono creati una sola volta qui
        // e passati ai Service tramite Dependency Injection.
        // Nessun DAO viene istanziato dentro i Service o i Controller.
        MemberDAO memberDAO             = new MemberDAOJdbcImpl();
        BoatDAO boatDAO                 = new BoatDAOJdbcImpl();
        SkipperDAO skipperDAO           = new SkipperDAOJdbcImpl();
        RentalDAO rentalDAO             = new RentalDAOJdbcImpl();
        BookingDAO bookingDAO           = new BookingDAOJdbcImpl();
        RegistrationDAO registrationDAO = new RegistrationDAOJdbcImpl();

        // ── Inizializzazione Service ─────────────────────────────────────────
        MemberService memberService =
                new MemberServiceImpl(memberDAO, registrationDAO);
        BoatService boatService =
                new BoatServiceImpl(boatDAO, skipperDAO);
        SkipperService skipperService =
                new SkipperServiceImpl(skipperDAO, memberDAO);
        RentalService rentalService =
                new RentalServiceImpl(rentalDAO, memberDAO, boatDAO, bookingDAO);
        BookingService bookingService =
                new BookingServiceImpl(bookingDAO, memberDAO, boatDAO,
                        skipperDAO, rentalDAO);
        RegistrationService registrationService =
                new RegistrationServiceImpl(registrationDAO, memberDAO);

        // ── Inizializzazione Controller ──────────────────────────────────────
        MemberController memberController =
                new MemberController(memberService);
        BoatController boatController =
                new BoatController(boatService);
        SkipperController skipperController =
                new SkipperController(skipperService);
        RentalController rentalController =
                new RentalController(rentalService);
        BookingController bookingController =
                new BookingController(bookingService);
        RegistrationController registrationController =
                new RegistrationController(registrationService);

        // ── Loop principale ──────────────────────────────────────────────────
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> {
                    // LOGIN
                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();

                    Member logged = memberController.login(
                            new LoginDTO(email, password));

                    if (logged == null) {
                        System.out.println("Credenziali non valide. Riprova.\n");
                        break;
                    }

                    System.out.println("\nBenvenuto, " + logged.getName()
                            + " " + logged.getSurname() + "!");

                    // Determina il ruolo e apri il menu corretto
                    // Admin — email convenzionale per il corso
                    if (logged.getEmail().equals("admin@nauticshare.it")) {
                        new MenuAdmin(
                                scanner, logged,
                                boatController, memberController,
                                skipperController, rentalController,
                                bookingController, registrationController
                        ).show();

                    } else {
                        // Controlla se il membro è anche skipper
                        Skipper asSkipper =
                                skipperController.findByMemberId(logged.getId());

                        if (asSkipper != null) {
                            new MenuSkipper(
                                    scanner, logged, asSkipper,
                                    boatController, rentalController,
                                    bookingController, registrationController
                            ).show();
                        } else {
                            new MenuMembro(
                                    scanner, logged,
                                    boatController, rentalController,
                                    bookingController, registrationController
                            ).show();
                        }
                    }
                }

                case "2" -> {
                    // SIGN-UP — nuovo membro
                    new MenuSignUp(scanner, memberController).show();
                }

                case "0" -> {
                    System.out.println("Arrivederci!");
                    running = false;
                }

                default -> System.out.println("Scelta non valida. Riprova.\n");
            }
        }

        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("┌─────────────────────────┐");
        System.out.println("│       MENU PRINCIPALE   │");
        System.out.println("├─────────────────────────┤");
        System.out.println("│  1. Login               │");
        System.out.println("│  2. Registrati          │");
        System.out.println("│  0. Esci                │");
        System.out.println("└─────────────────────────┘");
        System.out.print("Scelta: ");
    }
}