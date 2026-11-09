package ma.wegrow.service.impl;

import ma.wegrow.dto.EmployeRequestDTO;
import ma.wegrow.dto.EmployeResponseDTO;
import ma.wegrow.entity.Departement;
import ma.wegrow.entity.Employe;
import ma.wegrow.exception.DuplicateResourceException;
import ma.wegrow.exception.ResourceNotFoundException;
import ma.wegrow.repository.DepartementRepository;
import ma.wegrow.repository.EmployeRepository;
import ma.wegrow.service.EmployeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du service de gestion des employés.
 */
@Service
public class EmployeServiceImpl implements EmployeService {

    private final DepartementRepository departementRepository;
    private final EmployeRepository employeRepository;

    /**
     * Injection des dépendances par constructeur.
     */
    public EmployeServiceImpl(DepartementRepository departementRepository, EmployeRepository employeRepository) {
        this.departementRepository = departementRepository;
        this.employeRepository = employeRepository;
    }

    @Override
    @Transactional
    public EmployeResponseDTO addEmployeToDepartement(Long departementId, EmployeRequestDTO request) {
        // Règle : un employé appartient à un seul département, qui doit exister (404 sinon)
        Departement departement = departementRepository.findById(departementId)
                .orElseThrow(() -> new ResourceNotFoundException("Département introuvable avec l'id : " + departementId));

        if (request.getMatricule() == null || request.getMatricule().trim().isEmpty()) {
            throw new IllegalArgumentException("Le matricule de l'employé ne peut pas être vide.");
        }

        String matricule = request.getMatricule().trim();

        // Règle : le matricule d'un employé est unique (409 sinon)
        if (employeRepository.existsByMatricule(matricule)) {
            throw new DuplicateResourceException("Un employé avec le matricule '" + matricule + "' existe déjà.");
        }

        if (request.getNom() == null || request.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'employé est obligatoire.");
        }

        if (request.getPrenom() == null || request.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom de l'employé est obligatoire.");
        }

        if (request.getPoste() == null || request.getPoste().trim().isEmpty()) {
            throw new IllegalArgumentException("Le poste de l'employé est obligatoire.");
        }

        if (request.getContrat() == null) {
            throw new IllegalArgumentException("Le type de contrat est obligatoire (CDI, CDD, STAGE).");
        }

        Employe employe = new Employe(
                matricule,
                request.getNom().trim(),
                request.getPrenom().trim(),
                request.getPoste().trim(),
                request.getContrat(),
                departement
        );

        Employe saved = employeRepository.save(employe);

        return new EmployeResponseDTO(
                saved.getId(),
                saved.getMatricule(),
                saved.getNom(),
                saved.getPrenom(),
                saved.getPoste(),
                saved.getContrat()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeResponseDTO> getEmployesByDepartement(Long departementId) {
        // Règle : le département doit exister (404 sinon)
        if (!departementRepository.existsById(departementId)) {
            throw new ResourceNotFoundException("Département introuvable avec l'id : " + departementId);
        }

        List<Employe> employes = employeRepository.findByDepartementId(departementId);
        List<EmployeResponseDTO> dtos = new ArrayList<>();

        for (Employe emp : employes) {
            dtos.add(new EmployeResponseDTO(
                    emp.getId(),
                    emp.getMatricule(),
                    emp.getNom(),
                    emp.getPrenom(),
                    emp.getPoste(),
                    emp.getContrat()
            ));
        }

        return dtos;
    }
}
