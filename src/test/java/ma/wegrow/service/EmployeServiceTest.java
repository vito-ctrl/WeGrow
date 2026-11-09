package ma.wegrow.service;

import ma.wegrow.dto.EmployeRequestDTO;
import ma.wegrow.dto.EmployeResponseDTO;
import ma.wegrow.entity.Contrat;
import ma.wegrow.entity.Departement;
import ma.wegrow.entity.Employe;
import ma.wegrow.exception.DuplicateResourceException;
import ma.wegrow.exception.ResourceNotFoundException;
import ma.wegrow.repository.DepartementRepository;
import ma.wegrow.repository.EmployeRepository;
import ma.wegrow.service.impl.EmployeServiceImpl;
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
class EmployeServiceTest {

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private EmployeServiceImpl employeService;

    @Test
    @DisplayName("Ajout d'un employé dans un département existant avec succès")
    void testAddEmploye_Success() {
        Departement dep = new Departement(1L, "RH", "Ressources Humaines");
        EmployeRequestDTO request = new EmployeRequestDTO("EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI);

        when(departementRepository.findById(1L)).thenReturn(Optional.of(dep));
        when(employeRepository.existsByMatricule("EMP001")).thenReturn(false);

        Employe saved = new Employe(10L, "EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI, dep);
        when(employeRepository.save(any(Employe.class))).thenReturn(saved);

        EmployeResponseDTO response = employeService.addEmployeToDepartement(1L, request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("EMP001", response.getMatricule());
        assertEquals("Alami", response.getNom());
        assertEquals(Contrat.CDI, response.getContrat());
        verify(employeRepository, times(1)).save(any(Employe.class));
    }

    @Test
    @DisplayName("Ajout d'un employé échoue si le département n'existe pas (404)")
    void testAddEmploye_DepartementNotFound() {
        EmployeRequestDTO request = new EmployeRequestDTO("EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI);
        when(departementRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeService.addEmployeToDepartement(999L, request));
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    @DisplayName("Ajout d'un employé échoue si le matricule existe déjà (409)")
    void testAddEmploye_DuplicateMatricule() {
        Departement dep = new Departement(1L, "RH", "Ressources Humaines");
        EmployeRequestDTO request = new EmployeRequestDTO("EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI);

        when(departementRepository.findById(1L)).thenReturn(Optional.of(dep));
        when(employeRepository.existsByMatricule("EMP001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeService.addEmployeToDepartement(1L, request));
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    @DisplayName("Liste des employés d'un département existant")
    void testGetEmployesByDepartement_Success() {
        Departement dep = new Departement(1L, "RH", "Ressources Humaines");
        List<Employe> employes = Arrays.asList(
                new Employe(1L, "EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI, dep),
                new Employe(2L, "EMP002", "Bennani", "Youssef", "Assistant RH", Contrat.CDD, dep)
        );

        when(departementRepository.existsById(1L)).thenReturn(true);
        when(employeRepository.findByDepartementId(1L)).thenReturn(employes);

        List<EmployeResponseDTO> result = employeService.getEmployesByDepartement(1L);

        assertEquals(2, result.size());
        assertEquals("EMP001", result.get(0).getMatricule());
        assertEquals("EMP002", result.get(1).getMatricule());
    }

    @Test
    @DisplayName("Liste des employés échoue si le département n'existe pas (404)")
    void testGetEmployesByDepartement_DepartementNotFound() {
        when(departementRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> employeService.getEmployesByDepartement(999L));
        verify(employeRepository, never()).findByDepartementId(any());
    }
}
