package it.unifi.nauticshare.cli;

import it.unifi.nauticshare.controller.*;
import it.unifi.nauticshare.dto.BoatDTO;
import it.unifi.nauticshare.dto.SkipperDTO;
import it.unifi.nauticshare.model.*;

import java.util.List;
import java.util.Scanner;

public class MenuAdmin {

    private final Scanner scanner;
    private final Member admin;
    private final BoatController boatController;
    private final MemberController memberController;
    private final SkipperController skipperController;
    private final RentalController rentalController;
    private final BookingController bookingController;
    private final RegistrationController registrationController;

    public MenuAdmin(Scanner scanner, Member admin,
                     BoatController boatController,
                     MemberController memberController,
                     SkipperController skipperController,
                     RentalController rentalController,
                     BookingController bookingController,
                     RegistrationController registrationController) {
        this.scanner                = scanner;
        this.admin                  = admin;
        this.boatController         = boatController;
        this.memberController       = memberController;
        this.skipperController      = skipperController;
        this.rentalController       = rentalController;
        this.bookingController      = bookingController;
        this.registrationController = registrationController;
    }

    public void show() {
        boolean active = true;

        while (active) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listBoats();
                case "2" -> addBoat();
                case "3" -> assignSkipper();
                case "4" -> listMembers();
                case "5" -> promoteSkipper();
                case "6" -> monitorBookings();
                case "7" -> monitorRentals();
                case "0" -> {
                    System.out.println("Logout effettuato.\n");
                    active = false;
                }
                default -> System.out.println("Scelta non valida.\n");
            }
        }
    }

    // ── LISTA BARCHE ─────────────────────────────────────────────────────────
    private void listBoats() {
        System.out.println("\n─── FLOTTA ───");
        List<Boat> boats = boatController.findAll();
        if (boats.isEmpty()) {
            System.out.println("Nessuna barca registrata.\n");
            return;
        }
        boats.forEach(b -> System.out.printf(
                "  [%d] %s | %s | %d posti | Reg: %s%n",
                b.getId(), b.getName(), b.getType(),
                b.getSeats(), b.getRegNum()));
        System.out.println();
    }

    // ── AGGIUNGI BARCA ───────────────────────────────────────────────────────
    private void addBoat() {
        System.out.println("\n─── AGGIUNGI BARCA ───");

        System.out.print("Numero registrazione: ");
        String regNum = scanner.nextLine().trim();

        System.out.print("Nome barca: ");
        String name = scanner.nextLine().trim();

        System.out.println("Tipo (1=SAILBOAT, 2=YACHT, 3=SPEEDBOAT): ");
        BoatType type = switch (scanner.nextLine().trim()) {
            case "1" -> BoatType.SAILBOAT;
            case "2" -> BoatType.YACHT;
            case "3" -> BoatType.SPEEDBOAT;
            default  -> BoatType.SAILBOAT;
        };

        System.out.print("Numero posti: ");
        int seats;
        try {
            seats = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Numero non valido.\n");
            return;
        }

        System.out.print("URL foto (invio per saltare): ");
        String photoUrl = scanner.nextLine().trim();

        System.out.print("Descrizione: ");
        String description = scanner.nextLine().trim();

        Boat added = boatController.addBoat(new BoatDTO(
                regNum, name, type, seats,
                photoUrl.isEmpty() ? null : photoUrl,
                description
        ));

        if (added != null) {
            System.out.println("Barca aggiunta con id=" + added.getId() + "\n");
        } else {
            System.out.println("Aggiunta fallita. Controlla i dati.\n");
        }
    }

    // ── ASSEGNA SKIPPER ──────────────────────────────────────────────────────
    private void assignSkipper() {
        System.out.println("\n─── ASSEGNA SKIPPER A BARCA ───");
        listBoats();

        System.out.print("ID barca: ");
        int boatId;
        try {
            boatId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID non valido.\n");
            return;
        }

        System.out.println("Skipper disponibili:");
        skipperController.findAll().forEach(s -> System.out.printf(
                "  [%d] %s %s | Rating: %.1f%n",
                s.getId(), s.getName(), s.getSurname(), s.getAvgRating()));

        System.out.print("ID skipper: ");
        int skipperId;
        try {
            skipperId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID non valido.\n");
            return;
        }

        boolean result = boatController.assignSkipper(boatId, skipperId);
        System.out.println(result
                ? "Skipper assegnato con successo!\n"
                : "Assegnazione fallita.\n");
    }

    // ── LISTA MEMBRI ─────────────────────────────────────────────────────────
    private void listMembers() {
        System.out.println("\n─── MEMBRI REGISTRATI ───");
        List<Member> members = memberController.findAll();
        if (members.isEmpty()) {
            System.out.println("Nessun membro registrato.\n");
            return;
        }
        members.forEach(m -> System.out.printf(
                "  [%d] %s %s | %s | Patente: %s%n",
                m.getId(), m.getName(), m.getSurname(),
                m.getEmail(), m.isHasLicense() ? "Sì" : "No"));
        System.out.println();
    }

    // ── PROMUOVI A SKIPPER ───────────────────────────────────────────────────
    private void promoteSkipper() {
        System.out.println("\n─── PROMUOVI MEMBRO A SKIPPER ───");
        listMembers();

        System.out.print("ID membro da promuovere: ");
        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID non valido.\n");
            return;
        }

        System.out.print("Certificazione: ");
        String certificate = scanner.nextLine().trim();

        System.out.print("Bio (invio per saltare): ");
        String bio = scanner.nextLine().trim();

        System.out.print("ID barca da assegnare (0 per nessuna): ");
        int boatId;
        try {
            boatId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            boatId = 0;
        }

        Skipper promoted = skipperController.promoteToSkipper(
                new SkipperDTO(memberId, boatId,
                        certificate,
                        0.0, // Imposto avgRating iniziale a 0.0
                        bio.isEmpty() ? null : bio));

        System.out.println(promoted != null
                ? "Membro promosso a Skipper!\n"
                : "Promozione fallita.\n");
    }

    // ── MONITORA BOOKING ─────────────────────────────────────────────────────
    private void monitorBookings() {
        System.out.println("\n─── PRENOTAZIONI ATTIVE ───");
        List<Booking> bookings = bookingController.findAll();
        if (bookings.isEmpty()) {
            System.out.println("Nessuna prenotazione.\n");
            return;
        }
        bookings.forEach(b -> System.out.printf(
                "  [%d] MembroId=%d | BarcaId=%d | Data=%s | Posti=%d | €%.2f%n",
                b.getId(), b.getMemberId(), b.getBoatId(),
                b.getDate(), b.getSeatsBooked(), b.getTotalPrice()));
        System.out.println();
    }

    // ── MONITORA RENTAL ──────────────────────────────────────────────────────
    private void monitorRentals() {
        System.out.println("\n─── NOLEGGI ATTIVI ───");
        List<Rental> rentals = rentalController.findAll();
        if (rentals.isEmpty()) {
            System.out.println("Nessun noleggio.\n");
            return;
        }
        rentals.forEach(r -> System.out.printf(
                "  [%d] MembroId=%d | BarcaId=%d | %s → %s | €%.2f%n",
                r.getId(), r.getMemberId(), r.getBoatId(),
                r.getStartDate(), r.getEndDate(), r.getTotalPrice()));
        System.out.println();
    }

    private void printMenu() {
        System.out.println("┌──────────────────────────────┐");
        System.out.println("│        MENU ADMIN            │");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│  1. Lista barche             │");
        System.out.println("│  2. Aggiungi barca           │");
        System.out.println("│  3. Assegna skipper          │");
        System.out.println("│  4. Lista membri             │");
        System.out.println("│  5. Promuovi a skipper       │");
        System.out.println("│  6. Monitora prenotazioni    │");
        System.out.println("│  7. Monitora noleggi         │");
        System.out.println("│  0. Logout                   │");
        System.out.println("└──────────────────────────────┘");
        System.out.print("Scelta: ");
    }
}