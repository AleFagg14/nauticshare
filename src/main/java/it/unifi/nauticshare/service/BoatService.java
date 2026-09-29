package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dto.BoatDTO;
import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.BoatType;

import java.time.LocalDate;
import java.util.List;

public interface BoatService {

    // Aggiunge una nuova barca alla flotta
    Boat addBoat(BoatDTO dto);

    // Lettura
    Boat findById(int id);
    List<Boat> findAll();

    // Ricerca barche disponibili per un periodo di tempo e tipo
    List<Boat> findAvailable(LocalDate startDate,
                             LocalDate endDate,
                             BoatType type);

    // Modifica dati barca
    boolean update(int id, BoatDTO dto);

    // Elimina barca dalla flotta
    boolean delete(int id);

    // Assegna uno skipper a una barca
    boolean assignSkipper(int boatId, int skipperId);

    // Verifica se un numero di registrazione è già usato
    boolean existsByRegNum(String regNum);
}