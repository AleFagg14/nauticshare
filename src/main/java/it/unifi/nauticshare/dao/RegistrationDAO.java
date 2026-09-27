package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Registration;

public interface RegistrationDAO {

    boolean insert(Registration registration);
    Registration findById(int id);
    boolean update(Registration registration);
    boolean delete(int id);

    // Query custom
    Registration findByMemberId(int memberId);
    boolean existsByMemberId(int memberId);
}