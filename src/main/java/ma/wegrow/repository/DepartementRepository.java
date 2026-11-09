package ma.wegrow.repository;

import ma.wegrow.dto.DepartementResponseDTO;
import ma.wegrow.entity.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité Departement.
 */
@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {

    /**
     * Vérifie si un département avec ce nom existe déjà.
     */
    boolean existsByNom(String nom);

    /**
     * Recherche un département par son nom.
     */
    Optional<Departement> findByNom(String nom);

    /**
     * Récupère tous les départements avec leur nombre d'employés.
     */
    @Query("SELECT new ma.wegrow.dto.DepartementResponseDTO(d.id, d.nom, d.description, COUNT(e)) " +
           "FROM Departement d LEFT JOIN d.employes e " +
           "GROUP BY d.id, d.nom, d.description " +
           "ORDER BY d.id ASC")
    List<DepartementResponseDTO> findAllWithEmployeeCount();
}
