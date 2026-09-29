package it.unifi.nauticshare.service;

import it.unifi.nauticshare.model.Member;
import it.unifi.nauticshare.dto.MemberDTO;
import it.unifi.nauticshare.dto.LoginDTO;
import java.util.List;

public interface MemberService {

    // Registra un nuovo membro — hash della password incluso
    Member signup(MemberDTO dto);

    // Autentica un membro — restituisce l'oggetto Member se ok
    Member login(LoginDTO dto);

    // Lettura
    Member findById(int id);
    List<Member> findAll();

    // Aggiornamento profilo
    boolean update(int id, MemberDTO dto);

    // Eliminazione — bloccata se ha iscrizione attiva
    boolean delete(int id);
}