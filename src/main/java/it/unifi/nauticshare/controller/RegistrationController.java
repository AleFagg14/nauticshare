package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dto.RegistrationDTO;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.model.Registration;
import it.unifi.nauticshare.service.RegistrationService;

public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    //CREATE REGISTRATION
    // Chiamato dal Membro quando si iscrive al club (UC-4).
    // Il Service verifica che il membro non abbia già un'iscrizione
    public Registration createRegistration(RegistrationDTO dto) {
        try {
            return registrationService.createRegistration(dto);
        } catch (MemberNotFoundException e) {
            System.err.println("[RegistrationController] Membro non trovato: "
                    + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            // Iscrizione già esistente oppure anno nel passato
            System.err.println("[RegistrationController] Dati non validi: "
                    + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[RegistrationController] Errore imprevisto: "
                    + e.getMessage());
            return null;
        }
    }

    //FIND BY MEMBER ID
    // Recupera l'iscrizione di un membro specifico.
    // Usato dalla CLI per mostrare lo stato dell'iscrizione nel profilo del membro.
    public Registration findByMemberId(int memberId) {
        try {
            return registrationService.findByMemberId(memberId);
        } catch (MemberNotFoundException e) {
            System.err.println("[RegistrationController] Membro non trovato: "
                    + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[RegistrationController] Errore findByMemberId: "
                    + e.getMessage());
            return null;
        }
    }

    //FIND BY ID
    // Recupera un'iscrizione per id.
    // Usato internamente prima di un update o delete.
    public Registration findById(int id) {
        try {
            return registrationService.findById(id);
        } catch (IllegalArgumentException e) {
            System.err.println("[RegistrationController] " + e.getMessage());
            return null;
        }
    }

    // UPDATE
    // Aggiorna il tipo (INDIVIDUAL/FAMILY) o l'anno di iscrizione.
    // Usato dal Membro o dall'Admin per modificar una iscrizione già esistente.
    public boolean update(int registrationId, RegistrationDTO dto) {
        try {
            return registrationService.update(registrationId, dto);
        } catch (IllegalArgumentException e) {
            System.err.println("[RegistrationController] Iscrizione non trovata: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[RegistrationController] Errore update: "
                    + e.getMessage());
            return false;
        }
    }

    // DELETE
    // Elimina un'iscrizione al club.
    public boolean delete(int registrationId) {
        try {
            return registrationService.delete(registrationId);
        } catch (IllegalArgumentException e) {
            System.err.println("[RegistrationController] Iscrizione non trovata: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[RegistrationController] Errore delete: "
                    + e.getMessage());
            return false;
        }
    }
}