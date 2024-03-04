package ru.clevertec.news.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.exceptionhandlerstarter.model.IncorrectData;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;

@Tag(name = "News Controller", description = "Operations related to news")
@Validated
@RequestMapping(path = "/news")
public interface NewsController {

    @Operation(
            summary = "Get news by ID",
            tags = {"News"},
            description = "Get news. Returns a news by ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved news"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "News by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/{id}")
    ResponseEntity<NewsResponse> getById(@PathVariable("id") Long id);

    @Operation(
            summary = "Get archived news by ID",
            tags = {"News"},
            description = "Get news. Returns a archived news by ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved news"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "News by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/archive/{id}")
    ResponseEntity<NewsResponse> getFromArchive(@PathVariable("id") Long id);

    @Operation(
            summary = "Get all news",
            tags = {"News"},
            description = "Successfully retrieved news list.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved news list",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping
    ResponseEntity<PaginationResponse<NewsResponse>> getAll(
            @RequestParam(defaultValue = "15", name = "pageSize") int pageSize,
            @RequestParam(defaultValue = "1", name = "numberPage") int numberPage);

    @Operation(
            summary = "Get all archived news",
            tags = {"News"},
            description = "Successfully retrieved comment list related to news.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved archived comment list",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/archive")
    ResponseEntity<PaginationResponse<NewsResponse>> getAllFromArchive(
            @RequestParam(defaultValue = "15", name = "pageSize") int pageSize,
            @RequestParam(defaultValue = "1", name = "numberPage") int numberPage);


    @Operation(
            summary = "Create new news",
            tags = {"News"},
            description = "News creation. Returns new resource.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "News successfully created"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PostMapping
    ResponseEntity<NewsResponse> create(@Valid @RequestBody NewsRequest newsDto);

    @Operation(
            summary = "Update news by ID",
            tags = {"News"},
            description = "News update. Returns the updated resource.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "News successfully updated"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body or input parameter",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PutMapping("/{id}")
    ResponseEntity<NewsResponse> update(@PathVariable("id")Long id,
                                        @Valid @RequestBody NewsRequest newsDto);

    @Operation(
            summary = "Move news to archive by ID",
            tags = {"News"},
            description = "News successfully moved to archive.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "News by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PatchMapping("/{id}")
    ResponseEntity<Void> moveToArchive(@PathVariable("id")Long id);

    @Operation(
            summary = "Search for news",
            tags = {"News"},
            description = "Successfully retrieved search results.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved search results",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/search")
    ResponseEntity<List<NewsResponse>> search(@RequestParam(name = "search") String searchValue,
                                                 @RequestParam(name = "offset") Integer offset,
                                                 @RequestParam(name = "limit") Integer limit);

}
