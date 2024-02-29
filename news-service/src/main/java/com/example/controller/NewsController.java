package com.example.controller;

import com.example.LogMethodExecutionTime;
import com.example.controller.request.NewsRequest;
import com.example.controller.response.NewsResponse;
import com.example.mapper.NewsMapper;
import com.example.security.SecurityService;
import com.example.service.NewsService;
import com.example.util.ConstantsNews;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/news")
@Tag(name = "NewsController", description = "Documentation")
public class NewsController {

    private final NewsService newsService;
    private final NewsMapper newsMapper;
    private final SecurityService securityService;

    @Operation(summary = "Save News")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save News", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NewsResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public Page<NewsResponse> findAll(
            @RequestParam(value = "page", defaultValue = ConstantsNews.DEFAULT_PAGE) Integer page,
            @RequestParam(value = "size", defaultValue = ConstantsNews.DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(value = "orderBy", defaultValue = ConstantsNews.DEFAULT_NEWS_ORDER_BY) String orderBy,
            @RequestParam(value = "direction", defaultValue = ConstantsNews.DEFAULT_DIRECTION) String direction
    ) {
        log.info("Find all news");
        return newsService.findAllWithPaginationAndSorting(page, size, orderBy, direction).map(newsMapper::dtoToResponse);
    }

    @Operation(summary = "Save News")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save News", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NewsResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PostMapping
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public NewsResponse save(
            @RequestHeader("token") String token,
            @Valid @RequestBody NewsRequest newsRequest) {
        securityService.checkAccessForSaveNews(token);
        log.info("Save news {}", newsRequest);
        return newsMapper.dtoToResponse(newsService.save(newsRequest));
    }

    @Operation(summary = "Get News by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the News", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NewsResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "News not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsResponse findById(
            @PathVariable @NotNull @Positive Long id) {
        log.info("Find news with id: {}", id);
        return newsMapper.dtoToResponse(newsService.findById(id));
    }

    @Operation(summary = "Delete News by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the News"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "News not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @DeleteMapping("/{id}")
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(
            @RequestHeader("token") String token,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForDeleteAndUpdateNews(token, id);
        log.info("Delete news with id: {}", id);
        newsService.deleteById(id);
    }

    @Operation(summary = "Update News by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the News", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NewsResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "News not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PutMapping("/{id}")
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public NewsResponse update(
            @RequestHeader("token") String token,
            @Valid @RequestBody NewsRequest newsRequest,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForDeleteAndUpdateNews(token, id);
        log.info("Update news: {}", id);
        return newsMapper.dtoToResponse(newsService.update(id, newsRequest));
    }

}
