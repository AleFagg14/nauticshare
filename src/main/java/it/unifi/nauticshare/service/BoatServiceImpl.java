package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dao.BoatDAO;
import it.unifi.nauticshare.dao.SkipperDAO;
import it.unifi.nauticshare.dto.BoatDTO;
import it.unifi.nauticshare.exception.BoatNotFoundException;
import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.BoatType;

import java.time.LocalDate;
import java.util.List;

public class BoatServiceImpl implements BoatService {

    private final BoatDAO boatDAO;
    private final SkipperDAO skipperDAO;

    // Dependency Injection — i DAO vengono passati dall'esterno
    public BoatServiceImpl(BoatDAO boatDAO, SkipperDAO skipperDAO) {
        this.boatDAO = boatDAO;
        this.skipperDAO = skipperDAO;
    }

    @Override
    public Boat addBoat(BoatDTO dto) {

        // Regola: validazione campi obbligatori
        if (dto.getRegNum() == null || dto.getRegNum().isBlank()) {
            throw new IllegalArgumentException(
                    "Il numero di registrazione è obbligatorio");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Il nome della barca è obbligatorio");
        }
        if (dto.getType() == null) {
            throw new IllegalArgumentException(
                    "Il tipo di barca è obbligatorio");
        }
        if (dto.getSeats() <= 0) {
            throw new IllegalArgumentException(
                    "Il numero di posti deve essere maggiore di 0");
        }

        // Regola: regNum univoco
        if (boatDAO.existsByRegistrationNumber(dto.getRegNum())) {
            throw new IllegalArgumentException(
                    "Numero di registrazione già presente: " + dto.getRegNum());
        }

        // Costruiamo la Boat senza id (lo assegna PostgreSQL)
        Boat boat = new Boat(
                dto.getRegNum(),
                dto.getName(),
                dto.getType(),
                dto.getSeats(),
                dto.getPhotoUrl(),
                dto.getDescription()
        );

        boolean inserted = boatDAO.insert(boat);
        if (!inserted) {
            throw new RuntimeException(
                    "Errore durante l'inserimento della barca nel database");
        }

        // Recuperiamo la lista e prendiamo l'ultima inserita
        // Non abbiamo findByRegNum nel DAO — usiamo existsByRegistrationNumber
        // per verificare e findAll per recuperare
        List<Boat> all = boatDAO.findAll();
        return all.stream()
                .filter(b -> b.getRegNum().equals(dto.getRegNum()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Barca inserita ma non recuperabile dal DB"));
    }

    @Override
    public Boat findById(int id) {
        Boat boat = boatDAO.findById(id);
        if (boat == null) {
            throw new BoatNotFoundException(
                    "Barca con id " + id + " non trovata");
        }
        return boat;
    }

    @Override
    public List<Boat> findAll() {
        // Nessuna regola di business — delega al DAO
        return boatDAO.findAll();
    }

    @Override
    public List<Boat> findAvailable(LocalDate startDate,
                                    LocalDate endDate,
                                    BoatType type) {

        // Validazione date
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Le date di inizio e fine sono obbligatorie");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    "La data di fine deve essere successiva alla data di inizio");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La data di inizio non può essere nel passato");
        }

        // Recupera tutte le barche disponibili nel periodo
        List<Boat> available = boatDAO.findAvailable(startDate, endDate);


        if (type != null) {
            available = available.stream()
                    .filter(b -> b.getType() == type)
                    .toList();
        }

        return available;
    }

    @Override
    public boolean update(int id, BoatDTO dto) {

        // Verifica esistenza
        Boat existing = boatDAO.findById(id);
        if (existing == null) {
            throw new BoatNotFoundException(
                    "Barca con id " + id + " non trovata");
        }

        // Se il regNum cambia, verifica che il nuovo sia univoco
        if (!existing.getRegNum().equals(dto.getRegNum()) &&
                boatDAO.existsByRegistrationNumber(dto.getRegNum())) {
            throw new IllegalArgumentException(
                    "Numero di registrazione già in uso: " + dto.getRegNum());
        }

        // Aggiorna i campi
        existing.setRegNum(dto.getRegNum());
        existing.setName(dto.getName());
        existing.setType(dto.getType());
        existing.setSeats(dto.getSeats());
        existing.setPhotoUrl(dto.getPhotoUrl());
        existing.setDescription(dto.getDescription());

        return boatDAO.update(existing);
    }

    @Override
    public boolean delete(int id) {
        Boat boat = boatDAO.findById(id);
        if (boat == null) {
            throw new BoatNotFoundException(
                    "Barca con id " + id + " non trovata");
        }
        return boatDAO.delete(id);
    }

    @Override
    public boolean assignSkipper(int boatId, int skipperId) {

        // Verifica che la barca esista
        Boat boat = boatDAO.findById(boatId);
        if (boat == null) {
            throw new BoatNotFoundException(
                    "Barca con id " + boatId + " non trovata");
        }

        // Verifica che lo skipper esista
        if (skipperDAO.findById(skipperId) == null) {
            throw new IllegalArgumentException(
                    "Skipper con id " + skipperId + " non trovato");
        }

        // Verifica che lo skipper non sia già assegnato a un'altra barca
        if (skipperDAO.findByBoatId(boatId) != null) {
            throw new IllegalArgumentException(
                    "La barca ha già uno skipper assegnato");
        }

        // Aggiorna il boatId nello skipper tramite update
        var skipper = skipperDAO.findById(skipperId);
        skipper.setBoatId(boatId);
        return skipperDAO.update(skipper);
    }

    @Override
    public boolean existsByRegNum(String regNum) {
        return boatDAO.existsByRegistrationNumber(regNum);
    }
}