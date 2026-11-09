package ma.wegrow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.wegrow.dto.DepartementRequestDTO;
import ma.wegrow.dto.DepartementResponseDTO;
import ma.wegrow.exception.DepartmentNotEmptyException;
import ma.wegrow.exception.DuplicateResourceException;
import ma.wegrow.exception.GlobalExceptionHandler;
import ma.wegrow.exception.ResourceNotFoundException;
import ma.wegrow.service.DepartementService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepartementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DepartementService departementService;

    @InjectMocks
    private DepartementController departementController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(departementController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/departements -> 201 Created")
    void testCreateDepartement_Success() throws Exception {
        DepartementRequestDTO request = new DepartementRequestDTO("Ressources humaines", "Gestion du personnel");
        DepartementResponseDTO response = new DepartementResponseDTO(1L, "Ressources humaines", "Gestion du personnel", 0L);

        when(departementService.createDepartement(any(DepartementRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/departements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Ressources humaines"))
                .andExpect(jsonPath("$.nombreEmployes").value(0));
    }

    @Test
    @DisplayName("POST /api/departements -> 409 Conflict si doublon")
    void testCreateDepartement_Conflict() throws Exception {
        DepartementRequestDTO request = new DepartementRequestDTO("Ressources humaines", "Gestion du personnel");

        when(departementService.createDepartement(any(DepartementRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Un département avec ce nom existe déjà"));

        mockMvc.perform(post("/api/departements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("GET /api/departements -> 200 OK avec nombre d'employés")
    void testGetAllDepartements() throws Exception {
        DepartementResponseDTO d1 = new DepartementResponseDTO(1L, "Ressources humaines", "Gestion du personnel", 2L);
        DepartementResponseDTO d2 = new DepartementResponseDTO(2L, "Informatique", "Développement", 1L);

        when(departementService.getAllDepartements()).thenReturn(Arrays.asList(d1, d2));

        mockMvc.perform(get("/api/departements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Ressources humaines"))
                .andExpect(jsonPath("$[0].nombreEmployes").value(2))
                .andExpect(jsonPath("$[1].nom").value("Informatique"))
                .andExpect(jsonPath("$[1].nombreEmployes").value(1));
    }

    @Test
    @DisplayName("DELETE /api/departements/{id} -> 204 No Content")
    void testDeleteDepartement_Success() throws Exception {
        doNothing().when(departementService).deleteDepartement(1L);

        mockMvc.perform(delete("/api/departements/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/departements/{id} -> 404 Not Found si inexistant")
    void testDeleteDepartement_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Département introuvable"))
                .when(departementService).deleteDepartement(999L);

        mockMvc.perform(delete("/api/departements/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/departements/{id} -> 409 Conflict s'il contient des employés")
    void testDeleteDepartement_NotEmpty() throws Exception {
        doThrow(new DepartmentNotEmptyException("Département contient des employés"))
                .when(departementService).deleteDepartement(1L);

        mockMvc.perform(delete("/api/departements/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
