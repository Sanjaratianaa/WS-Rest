package com.transport.transport.api.repository;

import com.transport.transport.api.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface AffectationRepository extends JpaRepository<Affectation, Integer> {

    List<Affectation> findByEstArchiveFalse();

    List<Affectation> findByEmployeIdAndEstArchiveFalse(Integer idEmploye);

    List<Affectation> findByVehiculeIdAndEstArchiveFalse(Integer idVehicule);

    @Query("SELECT a FROM Affectation a " +
           "LEFT JOIN a.dateTransport dt " +
           "LEFT JOIN a.employe e " +
           "LEFT JOIN e.departement d " +
           "WHERE a.estArchive = false " +
           "AND (:date IS NULL OR dt.dateJour = :date) " +
           "AND (:idVehicule IS NULL OR a.vehicule.id = :idVehicule) " +
           "AND (:idEmploye IS NULL OR a.employe.id = :idEmploye) " +
           "AND (:idSite IS NULL OR a.site.id = :idSite) " +
           "AND (:estValidee IS NULL OR a.estValidee = :estValidee) " +
           "AND (:idDepartement IS NULL OR d.id = :idDepartement)")
    List<Affectation> findWithFilters(
            @Param("date") LocalDate date,
            @Param("idVehicule") Integer idVehicule,
            @Param("idEmploye") Integer idEmploye,
            @Param("idSite") Integer idSite,
            @Param("estValidee") Boolean estValidee,
            @Param("idDepartement") Integer idDepartement);

    @Query("SELECT COUNT(a) FROM Affectation a " +
           "WHERE a.vehicule.id = :idVehicule " +
           "AND a.dateTransport.id = :idDate " +
           "AND a.heureTransport.id = :idHeure " +
           "AND a.estArchive = false")
    long countByVehiculeAndDateAndHeure(
            @Param("idVehicule") Integer idVehicule,
            @Param("idDate") Integer idDate,
            @Param("idHeure") Integer idHeure);
}
