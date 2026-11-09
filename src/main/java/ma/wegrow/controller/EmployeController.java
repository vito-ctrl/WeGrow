package ma.wegrow.controller;

import ma.wegrow.dto.EmployeRequestDTO;
import ma.wegrow.dto.EmployeResponseDTO;
import ma.wegrow.service.EmployeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des employés d'un département.
 */
@RestController
@RequestMapping("/api/departements/{id}/employes")
public class EmployeController {

    private final EmployeService employeService;

    /**
     * Injection du service par constructeur.
     */
    public EmployeController(EmployeService employeService) {
        this.employeService = employeService;
    }

    /**
     * Ajoute un employé dans un département existant.
     * Code retour : 201 Created (404 si département inexistant, 409 si matricule déjà pris).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeResponseDTO> addEmploye(
            @PathVariable("id") Long departementId,
            @RequestBody EmployeRequestDTO request) {
        EmployeResponseDTO created = employeService.addEmployeToDepartement(departementId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Liste les employés d'un département.
     * Code retour : 200 OK (404 si département inexistant).
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EmployeResponseDTO>> getEmployes(@PathVariable("id") Long departementId) {
        List<EmployeResponseDTO> employes = employeService.getEmployesByDepartement(departementId);
        return ResponseEntity.ok(employes);
    }
}
