package it.unifi.nauticshare.dao;

import java.util.List;
import it.unifi.nauticshare.model.Skipper;

public interface SkipperDAO {
    boolean insert(Skipper skipper);
    Skipper findById(int id);
    List<Skipper> findAll();
    boolean update(Skipper skipper);
    boolean delete(int id);

    Skipper findByMemberId(int memberId);
    Skipper findByBoatId(int boatId);
    boolean updateRating(int skipperId, double newRating);

}