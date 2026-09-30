package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dao.MemberDAO;
import it.unifi.nauticshare.dao.RegistrationDAO;
import it.unifi.nauticshare.dto.RegistrationDTO;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.model.Registration;

public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationDAO registrationDAO;
    private final MemberDAO memberDAO;

    public RegistrationServiceImpl(RegistrationDAO registrationDAO,
                                   MemberDAO memberDAO) {
        this.registrationDAO = registrationDAO;
        this.memberDAO       = memberDAO;
    }

    @Override
    public Registration createRegistration(RegistrationDTO dto) {

        // Verifica membro
        if (memberDAO.findById(dto.getMemberId()) == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + dto.getMemberId() + " non trovato");
        }

        // Verifica iscrizione già esistente
        if (registrationDAO.existsByMemberId(dto.getMemberId())) {
            throw new IllegalArgumentException(
                    "Il membro con id " + dto.getMemberId() +
                            " ha già un'iscrizione attiva al club");
        }

        // Validazione anno
        int currentYear = java.time.LocalDate.now().getYear();
        if (dto.getYear() < currentYear) {
            throw new IllegalArgumentException(
                    "L'anno di iscrizione non può essere nel passato. " +
                            "Anno corrente: " + currentYear);
        }

        Registration registration = new Registration(
                dto.getMemberId(),
                dto.getType(),
                dto.getYear()
        );

        boolean inserted = registrationDAO.insert(registration);
        if (!inserted) {
            throw new RuntimeException(
                    "Errore durante la creazione dell'iscrizione nel database");
        }

        // Recupera l'iscrizione appena creata con l'id del DB
        return registrationDAO.findByMemberId(dto.getMemberId());
    }

    @Override
    public Registration findByMemberId(int memberId) {
        if (memberDAO.findById(memberId) == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + memberId + " non trovato");
        }
        // Restituisce null se il membro non ha iscrizioni —
        // comportamento corretto, non è un errore
        return registrationDAO.findByMemberId(memberId);
    }

    @Override
    public Registration findById(int id) {
        Registration reg = registrationDAO.findById(id);
        if (reg == null) {
            throw new IllegalArgumentException(
                    "Iscrizione con id " + id + " non trovata");
        }
        return reg;
    }

    @Override
    public boolean update(int registrationId, RegistrationDTO dto) {
        Registration existing = registrationDAO.findById(registrationId);
        if (existing == null) {
            throw new IllegalArgumentException(
                    "Iscrizione con id " + registrationId + " non trovata");
        }

        existing.setType(dto.getType());
        existing.setYear(dto.getYear());

        return registrationDAO.update(existing);
    }

    @Override
    public boolean delete(int registrationId) {
        if (registrationDAO.findById(registrationId) == null) {
            throw new IllegalArgumentException(
                    "Iscrizione con id " + registrationId + " non trovata");
        }
        return registrationDAO.delete(registrationId);
    }
}