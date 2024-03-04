package ru.clevertec.news.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;

import java.util.List;

/**
 * Mapper interface for converting between News entities and corresponding DTOs.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Mapper(config = MappersConfig.class)
public interface NewsMapper {

    /**
     * Converts News entity to NewsResponse DTO.
     *
     * @param news News entity to be converted.
     * @return Corresponding NewsResponse DTO.
     */
    NewsResponse toViewModel(News news);

    /**
     * Converts NewsRequest DTO to News entity.
     *
     * @param dto NewsRequest DTO to be converted.
     * @return Corresponding News entity.
     */
    News toEntity(NewsRequest dto);

    /**
     * Converts a list of News entities to a list of NewsResponse DTOs.
     *
     * @param list List of News entities to be converted.
     * @return List of corresponding NewsResponse DTOs.
     */
    List<NewsResponse> toViewModelList(List<News> list);

    /**
     * Merges data from NewsRequest DTO into an existing News entity.
     *
     * @param news         Existing News entity to be updated.
     * @param newsRequest  NewsRequest DTO containing updated data.
     * @return Updated News entity.
     */
    News merge(@MappingTarget News news, NewsRequest newsRequest);
}
