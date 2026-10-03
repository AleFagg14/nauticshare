package it.unifi.nauticshare.cli;

import it.unifi.nauticshare.controller.MemberController;
import it.unifi.nauticshare.dto.MemberDTO;
import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.model.RegistrationType;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MenuSignUp {

    private final Scanner scanner;
    private final MemberController memberController;

    public MenuSignUp(Scanner scanner,
                      MemberController memberController) {
        this.scanner          = scanner;
        this.memberController = memberController;
    }

    public void show() {
        System.out.println("\n─── REGISTRAZIONE NUOVO MEMBRO ───");

        System.out.print("Nome: ");
        String name = scanner.nextLine().trim();

        System.out.print("Cognome: ");
        String surname = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Città: ");
        String city = scanner.nextLine().trim();

        // Lettura data con gestione formato errato
        LocalDate birthday = null;
        while (birthday == null) {
            System.out.print("Data di nascita (YYYY-MM-DD): ");
            try {
                birthday = LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Formato non valido. Usa YYYY-MM-DD.");
            }
        }

        System.out.print("Hai la patente nautica? (s/n): ");
        boolean hasLicense = scanner.nextLine().trim()
                .equalsIgnoreCase("s");

        MemberDTO dto = new MemberDTO(
                name, surname, email, password,
                city, birthday, hasLicense
        );

        Member created = memberController.signup(dto);

        if (created != null) {
            System.out.println("\nRegistrazione completata!");
            System.out.println("Benvenuto, " + created.getName()
                    + "! Ora puoi fare il login.\n");
        } else {
            System.out.println("\nRegistrazione fallita. "
                    + "Controlla i dati e riprova.\n");
        }
    }
}