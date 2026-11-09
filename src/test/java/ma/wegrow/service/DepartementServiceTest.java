package ma.wegrow.service;

import ma.wegrow.dto.DepartementRequestDTO;
import ma.wegrow.dto.DepartementResponseDTO;
import ma.wegrow.entity.Departement;
import ma.wegrow.exception.DepartmentNotEmptyException;
import ma.wegrow.exception.DuplicateResourceException;
import ma.wegrow.exception.ResourceNotFoundException;
import ma.wegrow.repository.DepartementRepository;
import ma.wegrow.repository.EmployeRepository;
import ma.wegrow.service.impl.DepartementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartementServiceTest {

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private DepartementServiceImpl departementService;

    @Test
    @DisplayName("Création d'un département avec succès")
    void testCreateDepartement_Success() {
        DepartementRequestDTO request = new DepartementRequestDTO("Finance", "Gestion financière");
        when(departementRepository.existsByNom("Finance")).thenReturn(false);

        Departement saved = new Departement(1L, "Finance", "Gestion financière");
        when(departementRepository.save(any(Departement.class))).thenReturn(saved);

        DepartementResponseDTO response = departementService.createDepartement(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Finance", response.getNom());
        assertEquals("Gestion financière", response.getDescription());
        assertEquals(0L, response.getNombreEmployes());
        verify(departementRepository, times(1)).save(any(Departement.class));
    }

    @Test
    @DisplayName("Création d'un département échoue si le nom existe déjà (409)")
    void testCreateDepartement_DuplicateNom() {
        DepartementRequestDTO request = new DepartementRequestDTO("Informatique", "IT");
        when(departementRepository.existsByNom("Informatique")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> departementService.createDepartement(request));
        verify(departementRepository, never()).save(any(Departement.class));
    }

    @Test
    @DisplayName("Liste des départements avec leur nombre d'employés")
    void testGetAllDepartements() {
        List<DepartementResponseDTO> dtos = Arrays.asList(
                new DepartementResponseDTO(1L, "RH", "Ressources Humaines", 2L),
                new DepartementResponseDTO(2L, "IT", "Informatique", 1L)
        );
        when(departementRepository.findAllWithEmployeeCount()).thenReturn(dtos);

        List<DepartementResponseDTO> result = departementService.getAllDepartements();

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getNombreEmployes());
        assertEquals(1L, result.get(1).getNombreEmployes());
    }

    @Test
    @DisplayName("Suppression d'un département sans employés avec succès")
    void testDeleteDepartement_Success() {
        Departement dep = new Departement(1L, "Marketing", "Marketing digital");
        when(departementRepository.findById(1L)).thenReturn(Optional.of(dep));
        when(employeRepository.countByDepartementId(1L)).thenReturn(0L);

        assertDoesNotThrow(() -> departementService.deleteDepartement(1L));
        verify(departementRepository, times(1)).delete(dep);
    }

    @Test
    @DisplayName("Suppression échoue si le département contient des employés (409)")
    void testDeleteDepartement_NotEmpty() {
        Departement dep = new Departement(1L, "RH", "Ressources Humaines");
        when(departementRepository.findById(1L)).thenReturn(Optional.of(dep));
        when(employeRepository.countByDepartementId(1L)).thenReturn(3L);

        assertThrows(DepartmentNotEmptyException.class, () -> departementService.deleteDepartement(1L));
        verify(departementRepository, never()).delete(any(Departement.class));
    }

    @Test
    @DisplayName("Suppression échoue si le département n'existe pas (404)")
    void testDeleteDepartement_NotFound() {
        when(departementRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departementService.deleteDepartement(999L));
        verify(departementRepository, never()).delete(any(Departement.class));
    }
}
