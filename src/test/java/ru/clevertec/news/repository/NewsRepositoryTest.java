package ru.clevertec.news.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.clevertec.news.config.PostgresSQLContainerInitializer;
import ru.clevertec.news.entity.News;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NewsRepositoryTest extends PostgresSQLContainerInitializer {

    private final TestEntityManager testEntityManager;
    private final NewsRepository newsRepository;

    @Autowired
    public NewsRepositoryTest(TestEntityManager testEntityManager, NewsRepository newsRepository) {
        this.testEntityManager = testEntityManager;
        this.newsRepository = newsRepository;
    }

    @Test
    void shouldReturnedAllNewsWhereNewsIsNotArchived() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<News> expectedList = new ArrayList<>();
        expectedList.add(testEntityManager.find(News.class, 1));
        expectedList.add(testEntityManager.find(News.class, 2));

        //when
        Page<News> actual = newsRepository.findAllByIsArchivedIsFalse(pageRequest);

        //then
        assertThat(actual.getContent()).isEqualTo(expectedList);
    }

    @Test
    void shouldReturnedAllNewsWhereNewsIsArchived() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<News> expectedList = new ArrayList<>();
        expectedList.add(testEntityManager.find(News.class, 3));
        expectedList.add(testEntityManager.find(News.class, 4));

        //when
        Page<News> actual = newsRepository.findAllByIsArchivedIsTrue(pageRequest);

        //then
        assertThat(actual.getContent()).isEqualTo(expectedList);
    }
}
