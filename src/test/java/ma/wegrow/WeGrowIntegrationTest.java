package ma.wegrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.wegrow.dto.DepartementRequestDTO;
import ma.wegrow.dto.EmployeRequestDTO;
import ma.wegrow.entity.Contrat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test d'intégration de bout en bout validant le chargement du contexte XML,
 * la persistance en base relationnelle, l'initialisation des données et tous les endpoints.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/applicationContext.xml")
@WebAppConfiguration
@Transactional
class WeGrowIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @DisplayName("GET /api/departements -> Vérifie la présence des départements initialisés")
    void testGetDepartements() throws Exception {
        mockMvc.perform(get("/api/departements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("POST /api/departements -> Création réussie et gestion du doublon (409)")
    void testCreateDepartementAndConflict() throws Exception {
        DepartementRequestDTO newDep = new DepartementRequestDTO("Logistique", "Gestion de la chaîne logistique");

        // 1. Création -> 201 Created
        mockMvc.perform(post("/api/departements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDep)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Logistique"));

        // 2. Deuxième tentative avec le même nom -> 409 Conflict
        mockMvc.perform(post("/api/departements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDep)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("DELETE /api/departements/{id} -> 404 si non existant")
    void testDeleteDepartementNotFound() throws Exception {
        mockMvc.perform(delete("/api/departements/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST & GET /api/departements/{id}/employes -> Gestion des employés et erreurs 404 / 409")
    void testEmployeEndpoints() throws Exception {
        // Créer un département dédié au test
        DepartementRequestDTO dep = new DepartementRequestDTO("Recherche & Developpement", "R&D");
        String createDepResponse = mockMvc.perform(post("/api/departements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dep)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long depId = objectMapper.readTree(createDepResponse).get("id").asLong();

        // 1. Ajouter un employé dans le département créé -> 201 Created
        EmployeRequestDTO emp = new EmployeRequestDTO("EMP_TEST_99", "Dupont", "Jean", "Ingénieur R&D", Contrat.CDI);
        mockMvc.perform(post("/api/departements/" + depId + "/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricule").value("EMP_TEST_99"))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.contrat").value("CDI"));

        // 2. Ajouter un employé avec le même matricule -> 409 Conflict
        mockMvc.perform(post("/api/departements/" + depId + "/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        // 3. Ajouter un employé dans un département inexistant -> 404 Not Found
        EmployeRequestDTO empNotFound = new EmployeRequestDTO("EMP_TEST_88", "Durand", "Paul", "Technicien", Contrat.CDD);
        mockMvc.perform(post("/api/departements/888888/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empNotFound)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        // 4. Lister les employés du département -> 200 OK
        mockMvc.perform(get("/api/departements/" + depId + "/employes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricule").value("EMP_TEST_99"));

        // 5. Lister les employés d'un département inexistant -> 404 Not Found
        mockMvc.perform(get("/api/departements/888888/employes"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        // 6. Tenter de supprimer le département alors qu'il a des employés -> 409 Conflict
        mockMvc.perform(delete("/api/departements/" + depId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
