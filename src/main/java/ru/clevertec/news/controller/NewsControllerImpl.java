package ru.clevertec.news.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.service.NewsService;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsControllerImpl implements NewsController {

    private final NewsService service;

    @Override
    public ResponseEntity<NewsResponse> getById(Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.get(id));
    }

    @Override
    public ResponseEntity<NewsResponse> getFromArchive(Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getFromArchive(id));
    }

    @Override
    public ResponseEntity<PaginationResponse<NewsResponse>> getAll(int pageSize, int numberPage) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAll(pageSize, numberPage));
    }

    @Override
    public ResponseEntity<PaginationResponse<NewsResponse>> getAllFromArchive(int pageSize, int numberPage) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAllFromArchive(pageSize, numberPage));
    }

    @Override
    public ResponseEntity<NewsResponse> create(NewsRequest newsDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(newsDto));
    }

    @Override
    public ResponseEntity<NewsResponse> update(Long id, NewsRequest newsDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.update(id, newsDto));
    }

    @Override
    public ResponseEntity<Void> moveToArchive(Long id) {
        service.archive(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    public ResponseEntity<List<NewsResponse>> search(String searchValue, Integer offset, Integer limit) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.search(searchValue,offset,limit));
    }
}
