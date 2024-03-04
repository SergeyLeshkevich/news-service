package ru.clevertec.news.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.clevertec.exceptionhandlerstarter.exception.EntityNotFoundException;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.entity.User;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.mapper.NewsMapper;
import ru.clevertec.news.repository.NewsRepository;
import ru.clevertec.news.util.NewsRequestTestBuilder;
import ru.clevertec.news.util.NewsResponseTestBuilder;
import ru.clevertec.news.util.NewsTestBuilder;
import ru.clevertec.news.util.PaginationResponse;
import ru.clevertec.news.util.UserTestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private NewsServiceImpl newsService;

    @Captor
    ArgumentCaptor<News> newsTestCaptor;

    @Test
    void testGetShouldGetNewsByIdWhenNewsExistsAndIsNotArchived() {
        // given
        Long id = 1L;
        News news = NewsTestBuilder.aNews().build();
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().build();

        when(newsRepository.findById(id)).thenReturn(Optional.of(news));
        when(newsMapper.toViewModel(news)).thenReturn(newsResponse);

        // when
        NewsResponse result = newsService.get(id);

        // then
        assertThat(result).isNotNull();
        verify(newsRepository).findById(id);
        verify(newsMapper).toViewModel(news);
    }

    @Test
    void testGetShouldThrowEntityNotFoundExceptionWhenNewsIsArchived() {
        // given
        Long id = 1L;
        News news = NewsTestBuilder.aNews().withArchived(true).build();
        when(newsRepository.findById(id)).thenReturn(Optional.of(news));

        // when, then
        assertThatThrownBy(() -> newsService.get(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("News with 1 not found");
        verify(newsRepository).findById(id);
        verifyNoInteractions(newsMapper);
    }

    @Test
    void testGetShouldThrowEntityNotFoundExceptionWhenNewsDoesNotExist() {
        // given
        Long id = 1L;
        when(newsRepository.findById(id)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> newsService.get(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("News with 1 not found");
        verify(newsRepository).findById(id);
        verifyNoInteractions(newsMapper);
    }

    @Test
    void testGetFromArchiveShouldGetNewsFromArchiveByIdWhenNewsExistsAndIsArchived() {
        // given
        Long id = 1L;
        News news = NewsTestBuilder.aNews().withArchived(true).build();
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().build();

        when(newsRepository.findById(id)).thenReturn(Optional.of(news));
        when(newsMapper.toViewModel(news)).thenReturn(newsResponse);

        // when
        NewsResponse result = newsService.getFromArchive(id);

        // then
        assertThat(result).isNotNull();
        verify(newsRepository).findById(id);
        verify(newsMapper).toViewModel(news);
    }

    @Test
    void testGetFromArchiveShouldThrowEntityNotFoundExceptionWhenNewsDoesNotExistInArchive() {
        // given
        Long id = 1L;
        when(newsRepository.findById(id)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> newsService.getFromArchive(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("News with 1 not found");
        verify(newsRepository).findById(id);
        verifyNoInteractions(newsMapper);
    }

    @Test
    void testGetFromArchiveWithIsNotArchiveEntityShouldThrowsNotFoundException() {
        //given
        Long id = 1L;
        News news = NewsTestBuilder.aNews().build();
        when(newsRepository.findById(id)).thenReturn(Optional.of(news));

        // when, then
        assertThatThrownBy(() -> newsService.getFromArchive(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("News with 1 not found");
        verify(newsRepository).findById(id);
        verifyNoInteractions(newsMapper);
    }

    @Test
    void testGetAllShouldGetAllNewsByPageWhenNewsExistAndAreNotArchived() {
        // given
        int pageSize = 10;
        int numberPage = 1;
        PageRequest pageRequest = PageRequest.of(numberPage - 1, pageSize);
        List<News> newsList = Arrays.asList(NewsTestBuilder.aNews().build(), NewsTestBuilder.aNews().withId(2L).build());
        List<NewsResponse> newsResponseList = Arrays.asList(NewsResponseTestBuilder.aNewsResponse().build(),
                NewsResponseTestBuilder.aNewsResponse().build());
        Page<News> pageNews = new PageImpl<>(newsList);

        when(newsRepository.findAllByIsArchivedIsFalse(pageRequest)).thenReturn(pageNews);
        when(newsMapper.toViewModelList(pageNews.getContent())).thenReturn(newsResponseList);

        // when
        PaginationResponse<NewsResponse> result = newsService.getAll(pageSize, numberPage);

        // then
        assertThat(result.getCountPage()).isEqualTo(1);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(newsResponseList);
        verify(newsRepository).findAllByIsArchivedIsFalse(pageRequest);
        verify(newsMapper).toViewModelList(newsList);
    }

    @Test
    void testGetAllShouldGetEmptyPaginationResponseWhenNoNewsExistOrAreNotArchived() {
        // given
        int pageSize = 10;
        int numberPage = 1;
        PageRequest pageRequest = PageRequest.of(numberPage - 1, pageSize);
        Page<News> newsPage = Page.empty(pageRequest);
        PaginationResponse<NewsResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setCountPage(0);
        paginationResponse.setPageNumber(1);
        paginationResponse.setContent(List.of());
        when(newsRepository.findAllByIsArchivedIsFalse(pageRequest)).thenReturn(newsPage);

        // when
        PaginationResponse<NewsResponse> result = newsService.getAll(pageSize, numberPage);

        // then
        assertThat(result).isEqualTo(paginationResponse);
    }

    @Test
    void testGetAllFromArchiveShouldGetAllNewsFromArchiveByPageWhenNewsExistAndAreArchived() {
        // given
        int pageSize = 10;
        int numberPage = 1;
        PageRequest pageRequest = PageRequest.of(numberPage - 1, pageSize);
        List<News> newsList = Arrays.asList(NewsTestBuilder.aNews().withArchived(true).build(),
                NewsTestBuilder.aNews().withArchived(true).withId(2L).build());
        List<NewsResponse> newsResponseList = Arrays.asList(NewsResponseTestBuilder.aNewsResponse().build(),
                NewsResponseTestBuilder.aNewsResponse().build());
        Page<News> pageNews = new PageImpl<>(newsList);

        when(newsRepository.findAllByIsArchivedIsTrue(pageRequest)).thenReturn(pageNews);
        when(newsMapper.toViewModelList(pageNews.getContent())).thenReturn(newsResponseList);

        // when
        PaginationResponse<NewsResponse> result = newsService.getAllFromArchive(pageSize, numberPage);

        // then
        assertThat(result.getCountPage()).isEqualTo(1);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(newsResponseList);
        verify(newsRepository).findAllByIsArchivedIsTrue(pageRequest);
        verify(newsMapper).toViewModelList(newsList);
    }

    @Test
    void testGetAllFromArchiveShouldGetEmptyPaginationResponseWhenNoNewsExistOrAreArchived() {
        // given
        int pageSize = 10;
        int numberPage = 1;
        PageRequest pageRequest = PageRequest.of(numberPage - 1, pageSize);
        Page<News> commentPage = Page.empty(pageRequest);
        PaginationResponse<NewsResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setCountPage(0);
        paginationResponse.setPageNumber(1);
        paginationResponse.setContent(List.of());
        when(newsRepository.findAllByIsArchivedIsTrue(pageRequest)).thenReturn(commentPage);

        // when
        PaginationResponse<NewsResponse> result = newsService.getAllFromArchive(pageSize, numberPage);
        // then
        assertThat(result).isEqualTo(paginationResponse);
    }


    @Test
    void shouldCreateNewUserAndCommentWhenUserDoesNotExist() {
        // given
        News news = NewsTestBuilder.aNews().build();
        NewsRequest newsRequest = NewsRequestTestBuilder.aNewsRequest().build();
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().build();
        User user = UserTestBuilder.aUser().build();
        when(userService.getByUuiD(newsRequest.user().uuid())).thenReturn(Optional.empty());
        when(userService.create(newsRequest.user())).thenReturn(user);
        when(newsMapper.toEntity(newsRequest)).thenReturn(news);
        when(newsMapper.toViewModel(news)).thenReturn(newsResponse);
        when(newsRepository.save(news)).thenReturn(news);

        // when
        NewsResponse result = newsService.create(newsRequest);

        // then
        assertThat(result).isNotNull();
        verify(userService).create(newsRequest.user());
        verify(newsRepository).save(news);
    }

    @Test
    void shouldLinkExistingUserAndCommentWhenUserExists() {
        // given
        NewsRequest newsRequest = NewsRequestTestBuilder.aNewsRequest().build();
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().build();
        User existingUser = UserTestBuilder.aUser().build();
        News news = NewsTestBuilder.aNews().build();
        when(userService.getByUuiD(newsRequest.user().uuid())).thenReturn(Optional.of(existingUser));
        when(newsRepository.save(news)).thenReturn(news);
        when(newsMapper.toEntity(newsRequest)).thenReturn(news);
        when(newsMapper.toViewModel(news)).thenReturn(newsResponse);

        // when
        NewsResponse result = newsService.create(newsRequest);

        // then
        assertThat(result).isNotNull();
        verify(userService, never()).create(newsRequest.user());
        verify(newsRepository).save(news);
    }

    @Test
    void testUpdateShouldUpdateNewsWhenNewsExistsAndIsNotArchivedAndNewsRequestIsValid() {
        // given
        Long id = 1L;
        NewsRequest newsRequest = NewsRequestTestBuilder.aNewsRequest().build();
        NewsResponse newsResponse = NewsResponseTestBuilder.aNewsResponse().withText("Update").build();
        News updatedNews = NewsTestBuilder.aNews().withText("Update").build();
        when(newsRepository.findById(id)).thenReturn(Optional.of(updatedNews));
        when(newsMapper.merge(updatedNews, newsRequest)).thenReturn(updatedNews);
        when(newsRepository.save(updatedNews)).thenReturn(updatedNews);
        when(newsMapper.toViewModel(updatedNews)).thenReturn(newsResponse);

        // when
        NewsResponse result = newsService.update(id, newsRequest);

        // then
        assertThat(result).isEqualTo(newsResponse);
        verify(newsRepository).findById(id);
        verify(newsRepository).save(updatedNews);
        verify(newsMapper).merge(updatedNews, newsRequest);
        verify(newsMapper).toViewModel(updatedNews);
    }

    @Test
    void testUpdateWithIsArchiveEntityShouldThrowsNotFoundException() {
        // given
        Long id = 1L;
        News news = NewsTestBuilder.aNews().withArchived(true).build();
        NewsRequest newsRequest = NewsRequestTestBuilder.aNewsRequest().build();
        when(newsRepository.findById(id)).thenReturn(Optional.of(news));

        // when, then
        assertThatThrownBy(() -> newsService.update(id,newsRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("News with 1 not found");
        verify(newsRepository).findById(id);
        verifyNoInteractions(newsMapper);
    }

    @Test
    void testUpdateShouldThrowEntityNotFoundExceptionWhenNewsDoesNotExist() {
        // given
        Long id = 1L;
        NewsRequest newsRequest = NewsRequestTestBuilder.aNewsRequest().build();
        when(newsRepository.findById(id)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> newsService.update(id,newsRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("News with 1 not found");
        verify(newsRepository).findById(id);
        verifyNoInteractions(newsMapper);
    }

    @Test
    void testArchiveShouldUpdatedEntity() {
        // given
        Long id = 1L;
        when(newsRepository.findById(id)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> newsService.archive(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("News with 1 not found");
        verify(newsRepository).findById(id);
    }

    @Test
    void testArchiveWithIsEmptyEntityShouldThrowsNotFoundException() {
        // given
        Long id = 1L;
        News news = NewsTestBuilder.aNews().build();
        News expected = NewsTestBuilder.aNews().withArchived(true).build();
        when(newsRepository.findById(id)).thenReturn(Optional.of(news));

        // when
         newsService.archive(id);

        // then
        verify(newsRepository).findById(id);
        verify(newsRepository).save(newsTestCaptor.capture());
        News actual = newsTestCaptor.getValue();
        assertThat(actual).isEqualTo(expected);
    }
}
