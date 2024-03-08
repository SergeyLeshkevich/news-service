package ru.clevertec.news.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.exceptionhandlerstarter.exception.EntityNotFoundException;
import ru.clevertec.loggingstarter.annotation.Loggable;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.entity.User;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.mapper.NewsMapper;
import ru.clevertec.news.repository.NewsRepository;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing news entities.
 * This class provides methods to retrieve, create, update, and archive news items.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Service
@Loggable
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private static final float TEXT_BOOST_FACTOR = 1.3f;
    private static final float TITLE_BOOST_FACTOR = 1.2f;
    private static final float USERNAME_BOOST_FACTOR = 1.1f;
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_USERNAME = "user.userName";

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final EntityManager entityManager;
    private final UserService userService;

    /**
     * Retrieves a news item by its ID.
     *
     * @param id The ID of the news item to retrieve.
     * @return A {@link NewsResponse} representing the retrieved news item.
     * @throws EntityNotFoundException if the news item is not found or is archived.
     */
    @Override
    @Cacheable(value = "api-cache",key = "#id")
    public NewsResponse get(Long id) {
        Optional<News> optionalNews = newsRepository.findById(id);
        if (optionalNews.isEmpty() || optionalNews.get().isArchived()) {
            throw EntityNotFoundException.of(News.class, id);
        }

        return newsMapper.toViewModel(optionalNews.get());
    }

    /**
     * Retrieves an archived news item by its ID.
     *
     * @param id The ID of the archived news item to retrieve.
     * @return A {@link NewsResponse} representing the retrieved archived news item.
     * @throws EntityNotFoundException if the archived news item is not found or is not archived.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "api-cache",key = "#id")
    public NewsResponse getFromArchive(Long id) {
        Optional<News> optionalNews = newsRepository.findById(id);

        if (optionalNews.isEmpty() || !optionalNews.get().isArchived()) {
            throw EntityNotFoundException.of(News.class, id);
        }

        return newsMapper.toViewModel(optionalNews.get());
    }

    /**
     * Retrieves a paginated list of active news items.
     *
     * @param pageSize   The number of news items per page.
     * @param numberPage The page number to retrieve.
     * @return A {@link PaginationResponse} containing a list of {@link NewsResponse} objects.
     */
    @Override
    public PaginationResponse<NewsResponse> getAll(int pageSize, int numberPage) {
        PageRequest pageRequest = PageRequest.of(numberPage - 1, pageSize);
        Page<News> pageNews = newsRepository.findAllByIsArchivedIsFalse(pageRequest);
        PaginationResponse<NewsResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setCountPage(pageNews.getTotalPages());
        paginationResponse.setPageNumber(numberPage);
        paginationResponse.setContent(newsMapper.toViewModelList(pageNews.getContent()));

        return paginationResponse;
    }

    /**
     * Retrieves a paginated list of archived news items.
     *
     * @param pageSize   The number of archived news items per page.
     * @param numberPage The page number to retrieve.
     * @return A {@link PaginationResponse} containing a list of {@link NewsResponse} objects.
     */
    @Override
    public PaginationResponse<NewsResponse> getAllFromArchive(int pageSize, int numberPage) {
        PageRequest pageRequest = PageRequest.of(numberPage - 1, pageSize);
        Page<News> pageNews = newsRepository.findAllByIsArchivedIsTrue(pageRequest);

        PaginationResponse<NewsResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setCountPage(pageNews.getTotalPages());
        paginationResponse.setPageNumber(numberPage);
        paginationResponse.setContent(newsMapper.toViewModelList(pageNews.getContent()));

        return paginationResponse;
    }

    /**
     * Creates a new news item.
     *
     * @param newsDto The {@link NewsRequest} containing the information for the new news item.
     * @return A {@link NewsResponse} representing the newly created news item.
     */
    @Override
    @Transactional
    @Cacheable(value = "api-cache",key = "#newsDto.title() + #newsDto.text")
    public NewsResponse create(NewsRequest newsDto) {

        News news = newsMapper.toEntity(newsDto);
        User user = userService.getByUuiD(newsDto.user().uuid()).orElse(null);

        if (user == null) {
            user = userService.create(newsDto.user());
            news.setUser(user);
        } else {
            news.setUser(user);
        }
        news.setArchived(false);
        return newsMapper.toViewModel(newsRepository.save(news));
    }

    /**
     * Updates an existing news item.
     *
     * @param id      The ID of the news item to update.
     * @param newsDto The {@link NewsRequest} containing the updated information for the news item.
     * @return A {@link NewsResponse} representing the updated news item.
     * @throws EntityNotFoundException if the news item is not found or is archived.
     */
    @Override
    @Transactional
    @CachePut(value = "api-cache",key = "#id")
    public NewsResponse update(Long id, NewsRequest newsDto) {
        Optional<News> optionalNews = newsRepository.findById(id);

        if (optionalNews.isEmpty() || optionalNews.get().isArchived()) {
            throw EntityNotFoundException.of(News.class, id);
        }
        News updatedNews = newsRepository.save(newsMapper.merge(optionalNews.get(), newsDto));

        return newsMapper.toViewModel(updatedNews);
    }

    /**
     * Archives a news item by setting its archived status to true.
     *
     * @param id The ID of the news item to archive.
     * @throws EntityNotFoundException if the news item is not found.
     */
    @Override
    @Transactional
    public void archive(Long id) {
        News news = newsRepository.findById(id).orElseThrow(
                () -> EntityNotFoundException.of(News.class, id)
        );
        news.setArchived(true);
        newsRepository.save(news);
    }

    /**
     * Searches for news based on a search value with boosted fields without using Stream.
     * Uses Hibernate Search to perform a search and maps the results to {@link NewsResponse} objects.
     *
     * @param searchValue The value to search for in the "title" and "text" fields.
     * @param offset      The offset for pagination.
     * @param limit       The maximum number of results to fetch.
     * @return A List of {@link NewsResponse} objects representing the search results.
     */
    @Override
    public List<NewsResponse> search(String searchValue, Integer offset, Integer limit) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<News> searchResult = searchSession.search(News.class)
                .where(comment -> comment
                        .bool()
                        .with(b -> {
                            b.must(comment.matchAll());
                            b.must(comment.match()
                                    .field(FIELD_TITLE)
                                    .boost(TITLE_BOOST_FACTOR)
                                    .field(FIELD_TEXT)
                                    .boost(TEXT_BOOST_FACTOR)
                                    .field(FIELD_USERNAME)
                                    .boost(USERNAME_BOOST_FACTOR)
                                    .matching(searchValue));
                        }))
                .sort(SearchSortFactory::score)
                .fetch(offset, limit);
        List<News> commentAllList = searchResult.hits();
        List<News> result = commentAllList.stream().filter(news -> !news.isArchived()).toList();

        return newsMapper.toViewModelList(result);
    }
}
