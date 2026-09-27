package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Member;
import java.util.List;

public interface MemberDAO {
    boolean insert(Member member);
    Member findById(int id);
    List<Member> findAll();
    boolean update(Member member);
    boolean delete(int id);
    Member findByEmail(String email);
    boolean existsByEmail(String email);
}