package it.unifi.nauticshare.service;

import it.unifi.nauticshare.dto.SkipperDTO;
import it.unifi.nauticshare.model.Skipper;
import java.util.List;

public interface SkipperService {

    // Promuove un membro esistente a Skipper
    Skipper promoteToSkipper(SkipperDTO dto);

    // Lettura
    Skipper findById(int id);
    Skipper findByMemberId(int memberId);
    Skipper findByBoatId(int boatId);
    List<Skipper> findAll();

    // Rimuove il ruolo skipper da un membro
    boolean removeSkipper(int skipperId);
}