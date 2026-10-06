package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dto.LoginDTO;
import it.unifi.nauticshare.dto.MemberDTO;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.service.MemberService;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.BookingConflictException;

import java.util.List;

public class MemberController {

    private final MemberService memberService;

    // Il Service viene iniettato — mai istanziato qui dentro.

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // SIGNUP
    // Chiamato dalla CLI quando un nuovo utente si registra.
    // Restituisce il Member creato oppure null in caso di errore.
    public Member signup(MemberDTO dto) {
        try {
            return memberService.signup(dto);
        } catch (UnauthorizedOperationException e) {
            // Email già registrata
            System.err.println("[MemberController] Registrazione fallita: "
                    + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[MemberController] Errore imprevisto: "
                    + e.getMessage());
            return null;
        }
    }

    // LOGIN
    // Restituisce il Member autenticato se le credenziali sono corrette,
    // null altrimenti. La CLI usa l'oggetto restituito per capire
    // il ruolo dell'utente (Member, Skipper) e aprire il menu giusto.
    public Member login(LoginDTO dto) {
        try {
            return memberService.login(dto);
        } catch (MemberNotFoundException e) {
            System.err.println("[MemberController] Login fallito: "
                    + e.getMessage());
            return null;
        } catch (UnauthorizedOperationException e) {
            System.err.println("[MemberController] Password errata: "
                    + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[MemberController] Errore imprevisto: "
                    + e.getMessage());
            return null;
        }
    }

    // FIND BY ID
    // Usato dall'Admin per visualizzare il profilo di un membro specifico.
    public Member findById(int id) {
        try {
            return memberService.findById(id);
        } catch (MemberNotFoundException e) {
            System.err.println("[MemberController] " + e.getMessage());
            return null;
        }
    }

    // FIND ALL
    // Restituisce sempre una lista — vuota se non ci sono membri,
    // mai null. La CLI può iterare direttamente senza controlli aggiuntivi.
    public List<Member> findAll() {
        try {
            return memberService.findAll();
        } catch (Exception e) {
            System.err.println("[MemberController] Errore findAll: "
                    + e.getMessage());
            return List.of(); // lista vuota immutabile — mai null
        }
    }

    // UPDATE
    // Aggiorna il profilo di un membro esistente.
    // Restituisce true se l'aggiornamento è andato a buon fine.
    public boolean update(int id, MemberDTO dto) {
        try {
            return memberService.update(id, dto);
        } catch (MemberNotFoundException e) {
            System.err.println("[MemberController] Membro non trovato: "
                    + e.getMessage());
            return false;
        } catch (UnauthorizedOperationException e) {
            System.err.println("[MemberController] Aggiornamento non consentito: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[MemberController] Errore update: "
                    + e.getMessage());
            return false;
        }
    }

    // DELETE
    // Elimina un membro — bloccato se ha iscrizione attiva.
    public boolean delete(int id) {
        try {
            return memberService.delete(id);
        } catch (MemberNotFoundException e) {
            System.err.println("[MemberController] Membro non trovato: "
                    + e.getMessage());
            return false;
        } catch (UnauthorizedOperationException e) {
            // Membro con iscrizione attiva — non eliminabile
            System.err.println("[MemberController] Eliminazione bloccata: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[MemberController] Errore delete: "
                    + e.getMessage());
            return false;
        }
    }
}