package ru.clevertec.news.service;


import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;

public interface NewsService {

    NewsResponse get(Long id);

    NewsResponse getFromArchive(Long id);

    PaginationResponse<NewsResponse> getAll(int pageSize, int numberPage);

    PaginationResponse<NewsResponse> getAllFromArchive(int pageSize, int numberPage);

    NewsResponse create(NewsRequest newsDto);

    NewsResponse update(Long id, NewsRequest newsDto);

    void archive(Long id);

    List<NewsResponse> search(String searchValue, Integer offset, Integer limit);
}
