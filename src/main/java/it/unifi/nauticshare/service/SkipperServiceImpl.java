package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dao.MemberDAO;
import it.unifi.nauticshare.dao.SkipperDAO;
import it.unifi.nauticshare.dto.SkipperDTO;
import it.unifi.nauticshare.exception.MemberNotFoundException;
import it.unifi.nauticshare.exception.UnauthorizedOperationException;
import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.model.Skipper;
import java.util.List;

public class SkipperServiceImpl implements SkipperService {

    private final SkipperDAO skipperDAO;
    private final MemberDAO memberDAO;

    public SkipperServiceImpl(SkipperDAO skipperDAO,
                              MemberDAO memberDAO) {
        this.skipperDAO = skipperDAO;
        this.memberDAO  = memberDAO;
    }

    @Override
    public Skipper promoteToSkipper(SkipperDTO dto) {

        // Verifica membro
        Member member = memberDAO.findById(dto.getMemberId());
        if (member == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + dto.getMemberId() + " non trovato");
        }

        // Verifica patente
        if (!member.isHasLicense()) {
            throw new UnauthorizedOperationException(
                    "Il membro non ha la patente nautica " +
                            "richiesta per diventare skipper");
        }

        // Verifica che non sia già skipper
        if (skipperDAO.findByMemberId(dto.getMemberId()) != null) {
            throw new IllegalArgumentException(
                    "Il membro con id " + dto.getMemberId() +
                            " è già registrato come skipper");
        }

        // Costruzione oggetto Skipper
        // Skipper estende Member
        Skipper skipper = new Skipper(
                member.getId(),
                member.getName(),
                member.getSurname(),
                member.getEmail(),
                member.getPasswordHash(),
                member.getCity(),
                member.getBirthday(),
                member.isHasLicense(),
                dto.getBoatId(),
                dto.getCertificate(),
                0.0,              // avgRating parte da 0 — nessuna valutazione ancora
                dto.getBio()
        );

        boolean inserted = skipperDAO.insert(skipper);
        if (!inserted) {
            throw new RuntimeException(
                    "Errore durante la registrazione dello skipper nel database");
        }

        // Recupera lo skipper appena inserito con l'id assegnato dal DB
        return skipperDAO.findByMemberId(dto.getMemberId());
    }

    @Override
    public Skipper findById(int id) {
        Skipper skipper = skipperDAO.findById(id);
        if (skipper == null) {
            throw new IllegalArgumentException(
                    "Skipper con id " + id + " non trovato");
        }
        return skipper;
    }

    @Override
    public Skipper findByMemberId(int memberId) {
        if (memberDAO.findById(memberId) == null) {
            throw new MemberNotFoundException(
                    "Membro con id " + memberId + " non trovato");
        }
        return skipperDAO.findByMemberId(memberId);
    }

    @Override
    public Skipper findByBoatId(int boatId) {
        // Restituisce null se la barca non ha skipper assegnato —
        // comportamento atteso, non è un errore
        return skipperDAO.findByBoatId(boatId);
    }

    @Override
    public List<Skipper> findAll() {
        return skipperDAO.findAll();
    }

    @Override
    public boolean removeSkipper(int skipperId) {
        if (skipperDAO.findById(skipperId) == null) {
            throw new IllegalArgumentException(
                    "Skipper con id " + skipperId + " non trovato");
        }
        return skipperDAO.delete(skipperId);
    }
}