package it.unifi.nauticshare.cli;

import it.unifi.nauticshare.controller.*;
import it.unifi.nauticshare.model.*;

import java.util.List;
import java.util.Scanner;

public class MenuSkipper extends MenuMembro {

    private final Skipper skipper;
    private final BookingController bookingController;
    private final RentalController rentalController;
    private final Scanner scanner;

    public MenuSkipper(Scanner scanner, Member member, Skipper skipper,
                       BoatController boatController,
                       RentalController rentalController,
                       BookingController bookingController,
                       RegistrationController registrationController,
                       SkipperController skipperController) {
        // Richiama il costruttore di MenuMembro —
        // eredita tutte le funzionalità del Membro
        super(scanner, member, boatController,
                rentalController, bookingController,
                registrationController, skipperController);
        this.scanner            = scanner;
        this.skipper            = skipper;
        this.bookingController  = bookingController;
        this.rentalController   = rentalController;
    }

    // Override del menu — aggiunge le voci specifiche dello Skipper
    @Override
    public void show() {
        boolean active = true;

        while (active) {
            printSkipperMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                // Voci ereditate da MenuMembro
                case "1", "2", "3", "4", "5", "6" -> super.handleChoice(choice);

                // Voci specifiche Skipper
                case "7" -> mieUscitePassate();
                case "8" -> aggiungiPartecipante();

                case "0" -> {
                    System.out.println("Logout effettuato.\n");
                    active = false;
                }
                default -> System.out.println("Scelta non valida.\n");
            }
        }
    }

    // ── USCITE PASSATE ───────────────────────────────────────────────────────
    private void mieUscitePassate() {
        System.out.println("\n─── LE MIE USCITE COME SKIPPER ───");

        List<Booking> past =
                bookingController.findPastBySkipperId(skipper.getId());

        if (past.isEmpty()) {
            System.out.println("Nessuna uscita completata.\n");
            return;
        }

        past.forEach(b -> System.out.printf(
                "  [%d] Data=%s | BarcaId=%d | Posti=%d%n",
                b.getId(), b.getDate(),
                b.getBoatId(), b.getSeatsBooked()));

        System.out.printf("%nValutazione media attuale: %.1f/5.0%n%n",
                skipper.getAvgRating());
    }

    // ── AGGIUNGI PARTECIPANTE ────────────────────────────────────────────────
    private void aggiungiPartecipante() {
        System.out.println("\n─── AGGIUNGI PARTECIPANTE A NOLEGGIO ───");
        System.out.print("ID noleggio: ");

        try {
            int rentalId = Integer.parseInt(scanner.nextLine().trim());
            boolean added = rentalController.addParticipant(rentalId);
            System.out.println(added
                    ? "Partecipante aggiunto! Prezzo aggiornato.\n"
                    : "Operazione fallita.\n");
        } catch (NumberFormatException e) {
            System.out.println("ID non valido.\n");
        }
    }

    private void printSkipperMenu() {
        System.out.println("┌───────────────────────────────┐");
        System.out.println("│         MENU SKIPPER          │");
        System.out.println("├───────────────────────────────┤");
        System.out.println("│  1. Cerca barche disponibili  │");
        System.out.println("│  2. Prenota con skipper       │");
        System.out.println("│  3. Noleggio diretto          │");
        System.out.println("│  4. Le mie prenotazioni       │");
        System.out.println("│  5. Valuta skipper            │");
        System.out.println("│  6. Iscrizione club           │");
        System.out.println("│  7. Le mie uscite             │");
        System.out.println("│  8. Aggiungi partecipante     │");
        System.out.println("│  0. Logout                    │");
        System.out.println("└───────────────────────────────┘");
        System.out.print("Scelta: ");
    }
}