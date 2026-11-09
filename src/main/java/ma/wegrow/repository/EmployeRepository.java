package ma.wegrow.repository;

import ma.wegrow.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité Employe.
 */
@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    /**
     * Vérifie si un employé avec ce matricule existe déjà.
     */
    boolean existsByMatricule(String matricule);

    /**
     * Recherche un employé par son matricule.
     */
    Optional<Employe> findByMatricule(String matricule);

    /**
     * Liste tous les employés d'un département donné.
     */
    List<Employe> findByDepartementId(Long departementId);

    /**
     * Compte le nombre d'employés dans un département donné.
     */
    long countByDepartementId(Long departementId);
}
