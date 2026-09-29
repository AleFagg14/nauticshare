package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dao.MemberDAO;
import it.unifi.nauticshare.dao.RegistrationDAO;
import it.unifi.nauticshare.dto.LoginDTO;
import it.unifi.nauticshare.dto.MemberDTO;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.Member;

import java.util.List;

public class MemberServiceImpl implements MemberService {

    private final MemberDAO memberDAO;
    private final RegistrationDAO registrationDAO;

    // I DAO vengono iniettati nel costruttore — mai istanziati qui dentro.
    // Questo permette di passare mock nei test JUnit senza toccare il DB reale.
    public MemberServiceImpl(MemberDAO memberDAO,
                             RegistrationDAO registrationDAO) {
        this.memberDAO = memberDAO;
        this.registrationDAO = registrationDAO;
    }

    @Override
    public Member signup(MemberDTO dto) {

        // Regola: email univoca
        // Controlliamo prima di procedere per dare un messaggio chiaro all'utente
        if (memberDAO.existsByEmail(dto.getEmail())) {
            throw new UnauthorizedOperationException(
                    "Email già registrata nel sistema: " + dto.getEmail());
        }

        // Regola: hash della password
        // La password in chiaro non deve mai toccare il DB.
        // hashPassword() la trasforma in una stringa non reversibile.
        String hashedPassword = hashPassword(dto.getPassword());

        // Costruiamo il Member senza id (lo assegnerà PostgreSQL con SERIAL)
        Member member = new Member(
                dto.getName(),
                dto.getSurname(),
                dto.getEmail(),
                hashedPassword,       // ← password hashata, non quella del DTO
                dto.getCity(),
                dto.getBirthday(),
                dto.isHasLicense()
        );

        boolean inserted = memberDAO.insert(member);
        if (!inserted) {
            throw new RuntimeException(
                    "Errore durante la registrazione del membro");
        }

        // Recuperiamo il membro appena inserito con l'id assegnato dal DB
        // così il chiamante (Controller) riceve un oggetto completo
        return memberDAO.findByEmail(dto.getEmail());
    }

    @Override
    public Member login(LoginDTO dto) {

        // Regola 1 — l'email deve esistere
        Member member = memberDAO.findByEmail(dto.getEmail());
        if (member == null) {
            throw new MemberNotFoundException(
                    "Nessun account trovato con email: " + dto.getEmail());
        }

        // Regola 2 — la password deve corrispondere all'hash salvato
        // Hassiamo la password inserita e confrontiamo con quella nel DB
        String hashedInput = hashPassword(dto.getPassword());
        if (!hashedInput.equals(member.getPasswordHash())) {
            throw new UnauthorizedOperationException(
                    "Password errata per l'account: " + dto.getEmail());
        }

        return member;
    }

    @Override
    public Member findById(int id) {
        Member member = memberDAO.findById(id);
        if (member == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + id + " non trovato");
        }
        return member;
    }

    @Override
    public List<Member> findAll() {
        return memberDAO.findAll();
    }

    @Override
    public boolean update(int id, MemberDTO dto) {

        // Verifica che il membro esista prima di aggiornare
        Member existing = memberDAO.findById(id);
        if (existing == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + id + " non trovato");
        }

        // Se l'email cambia, verifica che la nuova non sia già usata
        // da un altro membro (diverso dall'utente corrente)
        if (!existing.getEmail().equals(dto.getEmail()) &&
                memberDAO.existsByEmail(dto.getEmail())) {
            throw new UnauthorizedOperationException(
                    "Email già usata da un altro membro: " + dto.getEmail());
        }

        // Aggiorniamo l'oggetto con i nuovi dati
        existing.setName(dto.getName());
        existing.setSurname(dto.getSurname());
        existing.setEmail(dto.getEmail());
        existing.setCity(dto.getCity());
        existing.setBirthday(dto.getBirthday());
        existing.setHasLicense(dto.isHasLicense());

        // La password viene aggiornata solo se l'utente ne ha fornita una nuova
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPasswordHash(hashPassword(dto.getPassword()));
        }

        return memberDAO.update(existing);
    }

    @Override
    public boolean delete(int id) {

        // non si elimina un membro con iscrizione attiva
        Member member = memberDAO.findById(id);
        if (member == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + id + " non trovato");
        }

        if (registrationDAO.existsByMemberId(id)) {
            throw new UnauthorizedOperationException(
                    "Impossibile eliminare il membro: ha un'iscrizione attiva al club");
        }

        return memberDAO.delete(id);
    }

    // Trasforma la password in chiaro in un hash non reversibile.
    // Usiamo Integer.toHexString per ottenere una stringa leggibile.
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
}