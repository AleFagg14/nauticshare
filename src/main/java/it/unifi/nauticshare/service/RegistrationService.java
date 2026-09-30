package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dto.RegistrationDTO;
import it.unifi.nauticshare.model.Registration;

public interface RegistrationService {

    // Crea una nuova iscrizione al club
    Registration createRegistration(RegistrationDTO dto);

    // Lettura
    Registration findByMemberId(int memberId);
    Registration findById(int id);

    // Aggiorna tipo o anno iscrizione
    boolean update(int registrationId, RegistrationDTO dto);

    // Elimina iscrizione
    boolean delete(int registrationId);
}