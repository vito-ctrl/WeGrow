package ma.wegrow.service;

import ma.wegrow.dto.DepartementRequestDTO;
import ma.wegrow.dto.DepartementResponseDTO;

import java.util.List;

/**
 * Interface de service pour la gestion des départements.
 */
public interface DepartementService {

    /**
     * Crée un nouveau département.
     * @param request les données du département
     * @return le département créé avec son identifiant
     */
    DepartementResponseDTO createDepartement(DepartementRequestDTO request);

    /**
     * Liste tous les départements avec leur nombre d'employés.
     * @return la liste des départements
     */
    List<DepartementResponseDTO> getAllDepartements();

    /**
     * Supprime un département par son identifiant.
     * @param id l'identifiant du département
     */
    void deleteDepartement(Long id);
}
