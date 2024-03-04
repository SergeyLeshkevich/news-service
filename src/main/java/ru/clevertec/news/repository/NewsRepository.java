package ru.clevertec.news.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.news.entity.News;

/**
 * Repository interface for News entity.
 * This interface extends JpaRepository, providing CRUD operations and custom queries for News entities.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Retrieves a page of archived news.
     *
     * @param pageRequest the pagination information.
     * @return a page of archived news.
     */
    Page<News> findAllByIsArchivedIsTrue(PageRequest pageRequest);

    /**
     * Retrieves a page of non-archived news.
     *
     * @param pageRequest the pagination information.
     * @return a page of non-archived news.
     */
    Page<News> findAllByIsArchivedIsFalse(PageRequest pageRequest);
}
