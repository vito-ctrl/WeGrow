package ma.wegrow.service;

import ma.wegrow.dto.EmployeRequestDTO;
import ma.wegrow.dto.EmployeResponseDTO;

import java.util.List;

/**
 * Interface de service pour la gestion des employés.
 */
public interface EmployeService {

    /**
     * Ajoute un employé dans un département existant.
     * @param departementId l'identifiant du département
     * @param request les données de l'employé à ajouter
     * @return l'employé créé
     */
    EmployeResponseDTO addEmployeToDepartement(Long departementId, EmployeRequestDTO request);

    /**
     * Liste tous les employés appartenant à un département donné.
     * @param departementId l'identifiant du département
     * @return la liste des employés du département
     */
    List<EmployeResponseDTO> getEmployesByDepartement(Long departementId);
}
