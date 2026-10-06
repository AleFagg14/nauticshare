package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dto.SkipperDTO;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.Skipper;
import it.unifi.nauticshare.service.SkipperService;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.BookingConflictException;

import java.util.List;

public class SkipperController {

    private final SkipperService skipperService;

    public SkipperController(SkipperService skipperService) {
        this.skipperService = skipperService;
    }

    //PROMOTE TO SKIPPER
    // Chiamato dall'Admin per promuovere un membro esistente a Skipper.
    // Il membro deve avere la patente nautica (UC-2)
    public Skipper promoteToSkipper(SkipperDTO dto) {
        try {
            return skipperService.promoteToSkipper(dto);
        } catch (MemberNotFoundException e) {
            System.err.println("[SkipperController] Membro non trovato: "
                    + e.getMessage());
            return null;
        } catch (UnauthorizedOperationException e) {
            System.err.println("[SkipperController] Patente mancante: "
                    + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("[SkipperController] Operazione non valida: "
                    + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[SkipperController] Errore imprevisto: "
                    + e.getMessage());
            return null;
        }
    }

    //FIND BY ID
    // Recupera uno skipper per id.
    // Usato internamente dalla CLI
    public Skipper findById(int id) {
        try {
            return skipperService.findById(id);
        } catch (IllegalArgumentException e) {
            System.err.println("[SkipperController] " + e.getMessage());
            return null;
        }
    }

    //FIND BY MEMBER ID
    // Controlla se un membro è anche skipper.
    // Usato dalla CLI dopo il login per determinare
    // se aprire il MenuSkipper invece del MenuMembro standard.

    public Skipper findByMemberId(int memberId) {
        try {
            return skipperService.findByMemberId(memberId);
        } catch (MemberNotFoundException e) {
            System.err.println("[SkipperController] Membro non trovato: "
                    + e.getMessage());
            return null;
        }
    }

    // FIND BY BOAT ID
    // Recupera lo skipper assegnato a una barca specifica.
    // Usato dalla CLI nella scheda dettaglio barca (UC-8)
    // per mostrare il profilo dello skipper con bio e valutazione media.
    public Skipper findByBoatId(int boatId) {
        try {
            return skipperService.findByBoatId(boatId);
        } catch (Exception e) {
            System.err.println("[SkipperController] Errore findByBoatId: "
                    + e.getMessage());
            return null;
        }
    }

    // FIND ALL
    // Restituisce tutti gli skipper registrati nel sistema.
    // Usato dall'Admin per visualizzare l'elenco degli skipper
    public List<Skipper> findAll() {
        try {
            return skipperService.findAll();
        } catch (Exception e) {
            System.err.println("[SkipperController] Errore findAll: "
                    + e.getMessage());
            return List.of();
        }
    }

    // REMOVE SKIPPER
    // Rimuove il ruolo skipper da un membro — operazione Admin.
    // Il membro rimane nel sistema come membro normale.
    public boolean removeSkipper(int skipperId) {
        try {
            return skipperService.removeSkipper(skipperId);
        } catch (IllegalArgumentException e) {
            System.err.println("[SkipperController] Skipper non trovato: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[SkipperController] Errore removeSkipper: "
                    + e.getMessage());
            return false;
        }
    }
}