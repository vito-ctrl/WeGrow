package ma.wegrow.service.impl;

import ma.wegrow.dto.DepartementRequestDTO;
import ma.wegrow.dto.DepartementResponseDTO;
import ma.wegrow.entity.Departement;
import ma.wegrow.exception.DepartmentNotEmptyException;
import ma.wegrow.exception.DuplicateResourceException;
import ma.wegrow.exception.ResourceNotFoundException;
import ma.wegrow.repository.DepartementRepository;
import ma.wegrow.repository.EmployeRepository;
import ma.wegrow.service.DepartementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service de gestion des départements.
 */
@Service
public class DepartementServiceImpl implements DepartementService {

    private final DepartementRepository departementRepository;
    private final EmployeRepository employeRepository;

    /**
     * Injection des dépendances par constructeur.
     */
    public DepartementServiceImpl(DepartementRepository departementRepository, EmployeRepository employeRepository) {
        this.departementRepository = departementRepository;
        this.employeRepository = employeRepository;
    }

    @Override
    @Transactional
    public DepartementResponseDTO createDepartement(DepartementRequestDTO request) {
        if (request.getNom() == null || request.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du département ne peut pas être vide.");
        }

        String nom = request.getNom().trim();

        // Règle : le nom d'un département est unique (409 sinon)
        if (departementRepository.existsByNom(nom)) {
            throw new DuplicateResourceException("Un département avec le nom '" + nom + "' existe déjà.");
        }

        Departement departement = new Departement(nom, request.getDescription());
        Departement saved = departementRepository.save(departement);

        return new DepartementResponseDTO(saved.getId(), saved.getNom(), saved.getDescription(), 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartementResponseDTO> getAllDepartements() {
        return departementRepository.findAllWithEmployeeCount();
    }

    @Override
    @Transactional
    public void deleteDepartement(Long id) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département introuvable avec l'id : " + id));

        // Règle : un département qui contient des employés ne peut pas être supprimé (409)
        long employeeCount = employeRepository.countByDepartementId(id);
        if (employeeCount > 0) {
            throw new DepartmentNotEmptyException("Impossible de supprimer le département '" + departement.getNom()
                    + "' car il contient " + employeeCount + " employé(s).");
        }

        departementRepository.delete(departement);
    }
}
