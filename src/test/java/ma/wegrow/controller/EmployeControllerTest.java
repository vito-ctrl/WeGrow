package ma.wegrow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.wegrow.dto.EmployeRequestDTO;
import ma.wegrow.dto.EmployeResponseDTO;
import ma.wegrow.entity.Contrat;
import ma.wegrow.exception.DuplicateResourceException;
import ma.wegrow.exception.GlobalExceptionHandler;
import ma.wegrow.exception.ResourceNotFoundException;
import ma.wegrow.service.EmployeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeService employeService;

    @InjectMocks
    private EmployeController employeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/departements/{id}/employes -> 201 Created")
    void testAddEmploye_Success() throws Exception {
        EmployeRequestDTO request = new EmployeRequestDTO("EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI);
        EmployeResponseDTO response = new EmployeResponseDTO(1L, "EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI);

        when(employeService.addEmployeToDepartement(eq(1L), any(EmployeRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/departements/1/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.matricule").value("EMP001"))
                .andExpect(jsonPath("$.nom").value("Alami"))
                .andExpect(jsonPath("$.prenom").value("Sara"))
                .andExpect(jsonPath("$.poste").value("Responsable RH"))
                .andExpect(jsonPath("$.contrat").value("CDI"));
    }

    @Test
    @DisplayName("POST /api/departements/{id}/employes -> 404 si département inexistant")
    void testAddEmploye_DepartementNotFound() throws Exception {
        EmployeRequestDTO request = new EmployeRequestDTO("EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI);

        when(employeService.addEmployeToDepartement(eq(999L), any(EmployeRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Département introuvable"));

        mockMvc.perform(post("/api/departements/999/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/departements/{id}/employes -> 409 si matricule en doublon")
    void testAddEmploye_DuplicateMatricule() throws Exception {
        EmployeRequestDTO request = new EmployeRequestDTO("EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI);

        when(employeService.addEmployeToDepartement(eq(1L), any(EmployeRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Matricule déjà utilisé"));

        mockMvc.perform(post("/api/departements/1/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("GET /api/departements/{id}/employes -> 200 OK")
    void testGetEmployes_Success() throws Exception {
        EmployeResponseDTO e1 = new EmployeResponseDTO(1L, "EMP001", "Alami", "Sara", "Responsable RH", Contrat.CDI);
        EmployeResponseDTO e2 = new EmployeResponseDTO(2L, "EMP002", "Bennani", "Youssef", "Assistant RH", Contrat.CDD);

        when(employeService.getEmployesByDepartement(1L)).thenReturn(Arrays.asList(e1, e2));

        mockMvc.perform(get("/api/departements/1/employes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].matricule").value("EMP001"))
                .andExpect(jsonPath("$[0].contrat").value("CDI"))
                .andExpect(jsonPath("$[1].matricule").value("EMP002"))
                .andExpect(jsonPath("$[1].contrat").value("CDD"));
    }

    @Test
    @DisplayName("GET /api/departements/{id}/employes -> 404 si département inexistant")
    void testGetEmployes_DepartementNotFound() throws Exception {
        when(employeService.getEmployesByDepartement(999L))
                .thenThrow(new ResourceNotFoundException("Département introuvable"));

        mockMvc.perform(get("/api/departements/999/employes"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
