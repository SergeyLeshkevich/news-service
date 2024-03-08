package ru.clevertec.news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.news.config.PostgresSQLContainerInitializer;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.util.NewsRequestTestBuilder;
import ru.clevertec.news.util.NewsResponseTestBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class
NewsControllerIntegrationTest extends PostgresSQLContainerInitializer {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldRetrieveNews() throws Exception {
        //given
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse()
                .withTitle("Test news title2")
                .withText("Test news text2").build();

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/news/2")
                        .contentType("application/json"))
                .andReturn();
        NewsResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), NewsResponse.class);

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(actual.text()).isEqualTo(newsResponse.text());
        assertThat(actual.title()).isEqualTo(newsResponse.title());
        assertThat(actual.time()).isEqualTo(newsResponse.time());
        assertThat(actual.id()).isEqualTo(2L);
    }


    @Test
    void shouldRetrieveNewsFromArchive() throws Exception {
        //given
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().withId(3L).build();
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/news/archive/3")
                        .contentType("application/json"))
                .andReturn();
        NewsResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), NewsResponse.class);

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(actual.text()).isEqualTo(newsResponse.text());
        assertThat(actual.title()).isEqualTo(newsResponse.title());
        assertThat(actual.time()).isEqualTo(newsResponse.time());
        assertThat(actual.id()).isEqualTo(newsResponse.id());
    }

    @Test
    void shouldRetrieveAllNews() throws Exception {
        //given
        String expected ="{\"pageNumber\":1,\"countPage\":1,\"content\":[{\"id\":1,\"time\":" +
                "\"2024-01-01T14:18:08.537\",\"title\":\"Test news title\",\"text\":\"Test news text\",\"user\":" +
                "{\"id\":1,\"uuid\":\"0bdc4d34-af90-4b42-bba6-f588323c87d7\",\"userName\":" +
                "\"Test userName comment\"}},{\"id\":2,\"time\":\"2024-01-01T14:18:08.537\",\"title\":" +
                "\"Test news title2\",\"text\":\"Test news text2\",\"user\":{\"id\":1,\"uuid\":" +
                "\"0bdc4d34-af90-4b42-bba6-f588323c87d7\",\"userName\":\"Test userName comment\"}}]}";

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/news")
                        .param("pageSize","5")
                        .param("numberPage","1")
                        .contentType("application/json"))
                .andReturn();
        String actual = result.getResponse().getContentAsString();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldRetrieveAllFromArchiveNews() throws Exception {
        //given
        String expected = "{\"pageNumber\":1,\"countPage\":1,\"content\":[{\"id\":3,\"time\":" +
                "\"2024-01-01T14:18:08.537\",\"title\":\"Test news title\",\"text\":" +
                "\"Test news text\",\"user\":{\"id\":1,\"uuid\":\"0bdc4d34-af90-4b42-bba6-f588323c87d7\"," +
                "\"userName\":\"Test userName comment\"}},{\"id\":4,\"time\":\"2024-01-01T14:18:08.537\"," +
                "\"title\":\"Test news title2\",\"text\":\"Test news text\",\"user\":{\"id\":1,\"uuid\":" +
                "\"0bdc4d34-af90-4b42-bba6-f588323c87d7\",\"userName\":\"Test userName comment\"}}]}";

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/news/archive")
                        .param("pageSize","5")
                        .param("numberPage","1")
                        .contentType("application/json"))
                .andReturn();
        String actual = result.getResponse().getContentAsString();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldCreateNewNews() throws Exception {
        //given
        NewsRequest newsRequest = NewsRequestTestBuilder.aNewsRequest().build();
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().withId(5L).build();
        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/news")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newsRequest)))
                .andReturn();
        NewsResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), NewsResponse.class);

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(201);
        assertThat(actual.text()).isEqualTo(newsResponse.text());
        assertThat(actual.title()).isEqualTo(newsResponse.title());
        assertThat(actual.time()).isNotNull();
        assertThat(actual.id()).isEqualTo(newsResponse.id());
    }

    @Test
    void shouldUpdateNews() throws Exception {
        //given
        NewsRequest newsRequest = NewsRequestTestBuilder.aNewsRequest().withText("Update text").build();
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().withText("Update text").build();
        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/news/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newsRequest)))
                .andReturn();
        NewsResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), NewsResponse.class);

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(actual.text()).isEqualTo(newsResponse.text());
        assertThat(actual.title()).isEqualTo(newsResponse.title());
        assertThat(actual.time()).isEqualTo(newsResponse.time());
        assertThat(actual.id()).isEqualTo(newsResponse.id());
    }

    @Test
    void shouldMoveToArchiveNews() throws Exception {
        //given
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().build();

        //when
        mockMvc.perform(MockMvcRequestBuilders.patch("/news/1")
                        .contentType("application/json"))
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/news/archive/1")
                        .contentType("application/json"))
                .andReturn();
        NewsResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), NewsResponse.class);

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(actual.text()).isEqualTo(newsResponse.text());
        assertThat(actual.title()).isEqualTo(newsResponse.title());
        assertThat(actual.time()).isEqualTo(newsResponse.time());
        assertThat(actual.id()).isEqualTo(newsResponse.id());
    }

    @Test
    void shouldRetrieveAllFoundNews() throws Exception {
        //given
        String expected = "[{\"id\":1,\"time\":\"2024-01-01T14:18:08.537\",\"title\":\"Test news title\",\"text\":" +
                "\"Test news text\",\"user\":{\"id\":1,\"uuid\":\"0bdc4d34-af90-4b42-bba6-f588323c87d7\"," +
                "\"userName\":\"Test userName comment\"}},{\"id\":2,\"time\":\"2024-01-01T14:18:08.537\",\"title\":" +
                "\"Test news title2\",\"text\":\"Test news text2\",\"user\":{\"id\":1,\"uuid\":" +
                "\"0bdc4d34-af90-4b42-bba6-f588323c87d7\",\"userName\":\"Test userName comment\"}}]";

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/news/search")
                        .param("search","Test news text")
                        .param("offset","0")
                        .param("limit","10")
                        .contentType("application/json"))
                .andReturn();
        String actual = result.getResponse().getContentAsString();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(actual).isEqualTo(expected);
    }
}
