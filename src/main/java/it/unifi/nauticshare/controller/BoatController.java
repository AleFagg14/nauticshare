package it.unifi.nauticshare.controller;

import it.unifi.nauticshare.dto.BoatDTO;
import it.unifi.nauticshare.exception.BoatNotFoundException;
import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.BoatType;
import it.unifi.nauticshare.service.BoatService;

import java.time.LocalDate;
import java.util.List;

public class BoatController {

    private final BoatService boatService;

    public BoatController(BoatService boatService) {
        this.boatService = boatService;
    }

    // ADD BOAT
    // Chiamato dall'Admin quando aggiunge una nuova barca alla flotta.
    // Restituisce la Boat creata con l'id assegnato dal DB,
    // oppure null se la validazione o l'inserimento falliscono.
    public Boat addBoat(BoatDTO dto) {
        try {
            return boatService.addBoat(dto);
        } catch (IllegalArgumentException e) {
            System.err.println("[BoatController] Dati non validi: "
                    + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("[BoatController] Errore addBoat: "
                    + e.getMessage());
            return null;
        }
    }

    // FIND BY ID
    // Usato dalla CLI per visualizzare il dettaglio di una barca specifica.
    public Boat findById(int id) {
        try {
            return boatService.findById(id);
        } catch (BoatNotFoundException e) {
            System.err.println("[BoatController] " + e.getMessage());
            return null;
        }
    }

    // FIND ALL
    // Restituisce il catalogo completo delle barche.

    public List<Boat> findAll() {
        try {
            return boatService.findAll();
        } catch (Exception e) {
            System.err.println("[BoatController] Errore findAll: "
                    + e.getMessage());
            return List.of();
        }
    }

    //FIND AVAILABLE
    // Cerca barche disponibili per un periodo e tipo opzionale. (UC-7)
    // type = null significa "tutti i tipi".
    public List<Boat> findAvailable(LocalDate startDate,
                                    LocalDate endDate,
                                    BoatType type) {
        try {
            return boatService.findAvailable(startDate, endDate, type);
        } catch (IllegalArgumentException e) {
            System.err.println("[BoatController] Date non valide: "
                    + e.getMessage());
            return List.of();
        } catch (Exception e) {
            System.err.println("[BoatController] Errore findAvailable: "
                    + e.getMessage());
            return List.of();
        }
    }

    //UPDATE
    // Aggiorna i dati di una barca esistente.
    // Usato dall'Admin per modificare scheda tecnica o foto.
    public boolean update(int id, BoatDTO dto) {
        try {
            return boatService.update(id, dto);
        } catch (BoatNotFoundException e) {
            System.err.println("[BoatController] Barca non trovata: "
                    + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("[BoatController] Dati non validi: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[BoatController] Errore update: "
                    + e.getMessage());
            return false;
        }
    }

    // DELETE
    // Rimuove una barca dalla flotta.
    // Usato dall'Admin gestisce automaticamente
    // l'eliminazione a cascata di skipper, rental e booking associati
    public boolean delete(int id) {
        try {
            return boatService.delete(id);
        } catch (BoatNotFoundException e) {
            System.err.println("[BoatController] Barca non trovata: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[BoatController] Errore delete: "
                    + e.getMessage());
            return false;
        }
    }

    //ASSIGN SKIPPER
    // Assegna uno skipper a una barca — operazione Admin. (UC-9)

    public boolean assignSkipper(int boatId, int skipperId) {
        try {
            return boatService.assignSkipper(boatId, skipperId);
        } catch (BoatNotFoundException e) {
            System.err.println("[BoatController] Barca non trovata: "
                    + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("[BoatController] Assegnazione non valida: "
                    + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[BoatController] Errore assignSkipper: "
                    + e.getMessage());
            return false;
        }
    }

    //EXISTS BY REG NUM
    // Usato dalla CLI per validare l'input prima di chiamare addBoat —
    // mostra subito all'utente se il numero di registrazione è già usato
    // senza aspettare la risposta del Service.
    public boolean existsByRegNum(String regNum) {
        try {
            return boatService.existsByRegNum(regNum);
        } catch (Exception e) {
            System.err.println("[BoatController] Errore existsByRegNum: "
                    + e.getMessage());
            return false;
        }
    }
}