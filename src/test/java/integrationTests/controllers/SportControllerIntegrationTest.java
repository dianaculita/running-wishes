package integrationTests.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.ApplicationMain;
import com.project.dtos.SportDto;
import com.project.services.sport.SportService;
import integrationTests.utils.CastToJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = ApplicationMain.class)
@AutoConfigureMockMvc
@TestPropertySource(
      locations = "classpath:application-integrationTest.yml"
)
@ActiveProfiles(value = "integrationTest")
public class SportControllerIntegrationTest {
    // THIS IS AN INTEGRATION TEST BETWEEN THE CONTROLLER AND THE SERVICE LAYER

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SportService sportService;

    @Test
    public void testGetSportById_shouldReturnStatus200() throws Exception {
        SportDto sportMock = SportDto.builder().sportId(1L).name("football").build();
        when(sportService.getSportById(sportMock.getSportId())).thenReturn(sportMock);

        this.mockMvc.perform(get("/sport/1"))
              .andExpect(status().isOk())
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.jsonPath("$.sportId").value("1"))
              .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("football"));
    }

    @Test
    public void testCreateNewSport_shouldReturnStatus200() throws Exception {
        SportDto newSportMock = SportDto.builder().sportId(2L).name("tennis").build();
        when(sportService.createNewSport(newSportMock)).thenReturn(newSportMock.getSportId());

        this.mockMvc.perform(post("/sport").content(CastToJson.toJsonFormat(newSportMock))
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
    }


}
