package ma.wegrow.controller;

import ma.wegrow.dto.DepartementRequestDTO;
import ma.wegrow.dto.DepartementResponseDTO;
import ma.wegrow.service.DepartementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des départements.
 */
@RestController
@RequestMapping("/api/departements")
public class DepartementController {

    private final DepartementService departementService;

    /**
     * Injection du service par constructeur.
     */
    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }

    /**
     * Crée un nouveau département.
     * Code retour : 201 Created (ou 409 Conflict si le nom existe déjà).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartementResponseDTO> createDepartement(@RequestBody DepartementRequestDTO request) {
        DepartementResponseDTO created = departementService.createDepartement(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Liste tous les départements avec leur nombre d'employés.
     * Code retour : 200 OK.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartementResponseDTO>> getAllDepartements() {
        List<DepartementResponseDTO> departements = departementService.getAllDepartements();
        return ResponseEntity.ok(departements);
    }

    /**
     * Supprime un département.
     * Code retour : 204 No Content (404 si introuvable, 409 s'il contient des employés).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable("id") Long id) {
        departementService.deleteDepartement(id);
        return ResponseEntity.noContent().build();
    }
}
