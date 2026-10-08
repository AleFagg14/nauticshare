package it.unifi.nauticshare.cli;

import it.unifi.nauticshare.controller.*;
import it.unifi.nauticshare.dto.BookingDTO;
import it.unifi.nauticshare.dto.RentalDTO;
import it.unifi.nauticshare.dto.RegistrationDTO;
import it.unifi.nauticshare.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuMembro {

    private final Scanner scanner;
    private final Member member;
    private final BoatController boatController;
    private final RentalController rentalController;
    private final BookingController bookingController;
    private final RegistrationController registrationController;
    private final SkipperController skipperController;

    public MenuMembro(Scanner scanner, Member member,
                      BoatController boatController,
                      RentalController rentalController,
                      BookingController bookingController,
                      RegistrationController registrationController,SkipperController skipperController) {
        this.scanner                = scanner;
        this.member                 = member;
        this.boatController         = boatController;
        this.rentalController       = rentalController;
        this.bookingController      = bookingController;
        this.registrationController = registrationController;
        this.skipperController = skipperController;
    }

    public void show() {
        boolean active = true;

        while (active) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> cercaBarche();
                case "2" -> prenotaConSkipper();
                case "3" -> noleggioDisponibile();
                case "4" -> mieiNoleggiBooking();
                case "5" -> valutaSkipper();
                case "6" -> iscrizioneClub();
                case "0" -> {
                    System.out.println("Logout effettuato.\n");
                    active = false;
                }
                default -> System.out.println("Scelta non valida.\n");
            }
        }
    }

    // ── CERCA BARCHE ─────────────────────────────────────────────────────────
    private void cercaBarche() {
        System.out.println("\n─── CERCA BARCHE DISPONIBILI ───");

        LocalDate start = readDate("Data inizio (YYYY-MM-DD): ");
        LocalDate end   = readDate("Data fine   (YYYY-MM-DD): ");

        System.out.println("Tipo (1=SAILBOAT 2=YACHT 3=SPEEDBOAT 0=Tutti): ");
        BoatType type = switch (scanner.nextLine().trim()) {
            case "1" -> BoatType.SAILBOAT;
            case "2" -> BoatType.YACHT;
            case "3" -> BoatType.SPEEDBOAT;
            default  -> null; // null = tutti i tipi
        };

        List<Boat> available = boatController.findAvailable(start, end, type);

        if (available.isEmpty()) {
            System.out.println("Nessuna barca disponibile per il periodo.\n");
        } else {
            System.out.println("Barche disponibili:");
            available.forEach(b -> System.out.printf(
                    "  [%d] %s | %s | %d posti | %s%n",
                    b.getId(), b.getName(), b.getType(),
                    b.getSeats(), b.getDescription()));
            System.out.println();
        }
    }

    // ── PRENOTA CON SKIPPER ──────────────────────────────────────────────────
    private void prenotaConSkipper() {
        System.out.println("\n─── PRENOTA USCITA CON SKIPPER ───");

        // STEP 1 — Data prima di tutto
        LocalDate date = readDate("Data uscita (YYYY-MM-DD): ");

        System.out.print("Numero di posti da prenotare: ");
        int seats = readInt();
        if (seats < 0) return;

        // STEP 2 — Mostra barche disponibili in quella data
        // Usiamo startDate = endDate = date per cercare disponibilità
        // in un singolo giorno
        List<Boat> available = boatController.findAvailable(
                date, date.plusDays(1), null);

        // Filtra solo barche con skipper assegnato
        // (le altre sono solo per noleggio diretto)
        List<Boat> withSkipper = available.stream()
                .filter(b -> skipperController.findByBoatId(b.getId()) != null)
                .toList();

        if (withSkipper.isEmpty()) {
            System.out.println(
                    "Nessuna barca con skipper disponibile per il " + date + "\n");
            return;
        }

        // STEP 3 — Mostra lista con dettagli skipper
        System.out.println("\nBarche disponibili con skipper:");
        withSkipper.forEach(b -> {
            Skipper s = skipperController.findByBoatId(b.getId());
            System.out.printf(
                    "  [%d] %s | %s | %d posti | " +
                            "Skipper: %s %s (★ %.1f) | %s%n",
                    b.getId(), b.getName(), b.getType(),
                    b.getSeats() - 1, // posti disponibili = totale - 1 skipper
                    s.getName(), s.getSurname(), s.getAvgRating(),
                    b.getDescription() != null ? b.getDescription() : "");
        });

        // STEP 4 — Selezione dalla lista
        System.out.print("\nScegli ID barca: ");
        int boatId = readInt();
        if (boatId < 0) return;

        // Verifica che l'id scelto sia nella lista mostrata
        boolean validChoice = withSkipper.stream()
                .anyMatch(b -> b.getId() == boatId);
        if (!validChoice) {
            System.out.println("ID non valido. Scegli dalla lista.\n");
            return;
        }

        // STEP 5 — Tipo iscrizione
        System.out.println("Tipo iscrizione (1=INDIVIDUAL 2=FAMILY): ");
        RegistrationType regType = scanner.nextLine().trim().equals("2")
                ? RegistrationType.FAMILY
                : RegistrationType.INDIVIDUAL;

        // STEP 6 — Crea il booking
        Booking booking = bookingController.createBooking(
                new BookingDTO(member.getId(), boatId,
                        null, date, seats, regType));

        if (booking != null) {
            System.out.printf(
                    "\n✅ Prenotazione confermata!%n" +
                            "   ID prenotazione: %d%n" +
                            "   Data: %s%n" +
                            "   Posti: %d%n" +
                            "   Totale: €%.2f%n%n",
                    booking.getId(), booking.getDate(),
                    booking.getSeatsBooked(), booking.getTotalPrice());
        } else {
            System.out.println("❌ Prenotazione fallita.\n");
        }
    }

    // ── NOLEGGIO DIRETTO ─────────────────────────────────────────────────────
    private void noleggioDisponibile() {
        System.out.println("\n─── NOLEGGIO DIRETTO ───");

        if (!member.isHasLicense()) {
            System.out.println("Non hai la patente nautica.");
            System.out.println("Usa la prenotazione con skipper.\n");
            return;
        }

        System.out.print("ID barca: ");
        int boatId = readInt();
        if (boatId < 0) return;

        LocalDate start = readDate("Data inizio (YYYY-MM-DD): ");
        LocalDate end   = readDate("Data fine   (YYYY-MM-DD): ");

        System.out.print("Numero partecipanti aggiuntivi: ");
        int participants = readInt();
        if (participants < 0) return;

        Rental rental = rentalController.createRental(
                new RentalDTO(member.getId(), boatId,
                        start, end, participants));

        if (rental != null) {
            System.out.printf(
                    "Noleggio confermato! Id=%d | €%.2f%n%n",
                    rental.getId(), rental.getTotalPrice());
        } else {
            System.out.println("Noleggio fallito. Controlla disponibilità.\n");
        }
    }

    // ── MIEI NOLEGGI E BOOKING ───────────────────────────────────────────────
    private void mieiNoleggiBooking() {
        System.out.println("\n─── LE MIE PRENOTAZIONI FUTURE ───");

        List<Booking> futureBookings =
                bookingController.findFuture(member.getId());
        if (futureBookings.isEmpty()) {
            System.out.println("Nessuna prenotazione futura.");
        } else {
            futureBookings.forEach(b -> System.out.printf(
                    "  [%d] Barca=%d | Data=%s | Posti=%d | €%.2f%n",
                    b.getId(), b.getBoatId(), b.getDate(),
                    b.getSeatsBooked(), b.getTotalPrice()));

            System.out.print("\nCancella prenotazione? (id o 0 per no): ");
            int cancelId = readInt();
            if (cancelId > 0) {
                boolean cancelled = bookingController.cancelBooking(cancelId);
                System.out.println(cancelled
                        ? "Prenotazione cancellata!\n"
                        : "Cancellazione fallita.\n");
            }
        }

        System.out.println("\n─── I MIEI NOLEGGI FUTURI ───");
        List<Rental> futureRentals =
                rentalController.findFuture(member.getId());
        if (futureRentals.isEmpty()) {
            System.out.println("Nessun noleggio futuro.\n");
        } else {
            futureRentals.forEach(r -> System.out.printf(
                    "  [%d] Barca=%d | %s → %s | €%.2f%n",
                    r.getId(), r.getBoatId(),
                    r.getStartDate(), r.getEndDate(), r.getTotalPrice()));

            System.out.print("\nCancella noleggio? (id o 0 per no): ");
            int cancelId = readInt();
            if (cancelId > 0) {
                boolean cancelled = rentalController.cancelRental(cancelId);
                System.out.println(cancelled
                        ? "Noleggio cancellato!\n"
                        : "Cancellazione fallita.\n");
            }
        }
    }

    // ── VALUTA SKIPPER ───────────────────────────────────────────────────────
    private void valutaSkipper() {
        System.out.println("\n─── VALUTA SKIPPER ───");

        List<Booking> past = bookingController
                .findByMemberId(member.getId())
                .stream()
                .filter(b -> b.getDate().isBefore(LocalDate.now()))
                .toList();

        if (past.isEmpty()) {
            System.out.println("Nessuna uscita passata da valutare.\n");
            return;
        }

        // Mostra le uscite passate includendo i dettagli dello skipper, usa skipperId del booking,
        // non la barca attuale (lo skipper potrebbe essere cambiato)
        past.forEach(b -> {
            String skipperName = "Non assegnato";
            if (b.getSkipperId() != null) {
                Skipper s = skipperController.findById(b.getSkipperId());
                if (s != null) skipperName = s.getName() + " " + s.getSurname();
            }
            System.out.printf("  [%d] Data=%s | BarcaId=%d | Skipper: %s%n",
                    b.getId(), b.getDate(), b.getBoatId(), skipperName);
        });

        System.out.print("\nID prenotazione da valutare (0 per annullare): ");
        int bookingId = readInt();
        if (bookingId < 0) return;

        // Trova la prenotazione selezionata per mostrare il nome dello skipper confermato
        Booking selected = past.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElse(null);

        if (selected == null) {
            System.out.println("ID prenotazione non valido.\n");
            return;
        }

        // Mostra il nome dello skipper che si sta valutando
        // Usa skipperId del booking — dato storico affidabile
        if (selected.getSkipperId() != null) {
            Skipper targetSkipper = skipperController
                    .findById(selected.getSkipperId());
            if (targetSkipper != null) {
                System.out.printf("Stai valutando lo skipper: %s %s%n",
                        targetSkipper.getName(), targetSkipper.getSurname());
            }
        }

        double rating = -1;
        while (rating < 1.0 || rating > 5.0) {
            System.out.print("Valutazione (1.0 - 5.0): ");
            try {
                rating = Double.parseDouble(scanner.nextLine().trim());
                if (rating < 1.0 || rating > 5.0) {
                    System.out.println("Inserisci un valore tra 1.0 e 5.0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido (es. 4.5).");
            }
        }

        boolean rated = bookingController.rateSkipper(bookingId, rating);
        System.out.println(rated
                ? "Valutazione registrata con successo!\n"
                : "Valutazione fallita.\n");
    }

    // ── ISCRIZIONE CLUB ──────────────────────────────────────────────────────
    private void iscrizioneClub() {
        System.out.println("\n─── ISCRIZIONE AL CLUB ───");

        Registration existing =
                registrationController.findByMemberId(member.getId());

        if (existing != null) {
            System.out.printf(
                    "Hai già un'iscrizione: %s — anno %d%n%n",
                    existing.getType(), existing.getYear());
            return;
        }

        System.out.println("Tipo iscrizione (1=INDIVIDUAL 2=FAMILY): ");
        RegistrationType type = scanner.nextLine().trim().equals("2")
                ? RegistrationType.FAMILY
                : RegistrationType.INDIVIDUAL;

        Registration reg = registrationController.createRegistration(
                new RegistrationDTO(member.getId(), type,
                        LocalDate.now().getYear()));

        System.out.println(reg != null
                ? "Iscrizione completata!\n"
                : "Iscrizione fallita.\n");
    }

    // ── HELPER ───────────────────────────────────────────────────────────────
    private LocalDate readDate(String prompt) {
        LocalDate date = null;
        while (date == null) {
            System.out.print(prompt);
            try {
                date = LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Formato non valido. Usa YYYY-MM-DD.");
            }
        }
        return date;
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.\n");
            return -1;
        }
    }

    private void printMenu() {
        System.out.println("┌───────────────────────────────┐");
        System.out.println("│         MENU MEMBRO           │");
        System.out.println("├───────────────────────────────┤");
        System.out.println("│  1. Cerca barche disponibili  │");
        System.out.println("│  2. Prenota con skipper       │");
        System.out.println("│  3. Noleggio diretto          │");
        System.out.println("│  4. Le mie prenotazioni       │");
        System.out.println("│  5. Valuta skipper            │");
        System.out.println("│  6. Iscrizione club           │");
        System.out.println("│  0. Logout                    │");
        System.out.println("└───────────────────────────────┘");
        System.out.print("Scelta: ");
    }


    // Aggiungi questo metodo in MenuMembro
// Permette a MenuSkipper di delegare le scelte comuni
    public void handleChoice(String choice) {
        switch (choice) {
            case "1" -> cercaBarche();
            case "2" -> prenotaConSkipper();
            case "3" -> noleggioDisponibile();
            case "4" -> mieiNoleggiBooking();
            case "5" -> valutaSkipper();
            case "6" -> iscrizioneClub();
            default  -> System.out.println("Scelta non valida.\n");
        }
    }
}