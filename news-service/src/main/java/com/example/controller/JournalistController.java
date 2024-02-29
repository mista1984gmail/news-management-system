package com.example.controller;

import com.example.LogMethodExecutionTime;
import com.example.controller.request.JournalistRequest;
import com.example.controller.response.JournalistResponse;
import com.example.controller.response.NewsResponse;
import com.example.mapper.JournalistMapper;
import com.example.mapper.NewsMapper;
import com.example.security.SecurityService;
import com.example.service.JournalistService;
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

import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/journalists")
@Tag(name = "JournalistController", description = "Documentation")
public class JournalistController {

    private final JournalistService journalistService;
    private final JournalistMapper journalistMapper;
    private final NewsService newsService;
    private final NewsMapper newsMapper;
    private final SecurityService securityService;

    @Operation(summary = "Find all Journalist with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Find Journalist with pagination", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JournalistResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public Page<JournalistResponse> findAll(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = ConstantsNews.DEFAULT_PAGE) Integer page,
            @RequestParam(value = "size", defaultValue = ConstantsNews.DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(value = "orderBy", defaultValue = ConstantsNews.DEFAULT_JOURNALIST_ORDER_BY) String orderBy,
            @RequestParam(value = "direction", defaultValue = ConstantsNews.DEFAULT_DIRECTION) String direction) {
        securityService.checkAccessForJournalistOperationCRUD(token);
        log.info("Find all journalists");
        return journalistService.findAll(page, size, orderBy, direction).map(journalistMapper::dtoToResponse);
    }

    @Operation(summary = "Save Journalist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save Journalist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JournalistResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PostMapping
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public JournalistResponse save(
            @RequestHeader("token") String token,
            @Valid @RequestBody JournalistRequest journalistRequest) {
        securityService.checkAccessForJournalistOperationCRUD(token);
        log.info("Save journalist {}", journalistRequest);
        return journalistMapper.dtoToResponse(journalistService.save(
                journalistMapper.requestToDto(journalistRequest)));
    }

    @Operation(summary = "Get Journalist by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Journalist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JournalistResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Journalist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping("/{id}")
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public JournalistResponse findById(
            @RequestHeader("token") String token,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForJournalistOperationCRUD(token);
        log.info("Find journalist with id: {}", id);
        return journalistMapper.dtoToResponse(journalistService.findById(id));
    }

    @Operation(summary = "Find Journalist news by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Find Journalist news by id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NewsResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Journalist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @GetMapping("/{id}/news")
    @ResponseStatus(HttpStatus.OK)
    public List<NewsResponse> findNewsJournalist(
            @PathVariable @NotNull @Positive Long id) {
        log.info("Find all journalists news with journalist id: {}", id);
        return newsMapper.toListResponse(newsService.findAllByJournalist(id));
    }

    @Operation(summary = "Delete Journalist by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the Journalist"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Journalist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(
            @RequestHeader("token") String token,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForJournalistOperationCRUD(token);
        log.info("Delete journalist with id: {}", id);
        journalistService.deleteById(id);
    }

    @Operation(summary = "Update Journalist by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the Journalist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JournalistResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Journalist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PutMapping("/{id}")
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public JournalistResponse update(
            @RequestHeader("token") String token,
            @Valid @RequestBody JournalistRequest journalistRequest,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForJournalistOperationCRUD(token);
        log.info("Update journalist: {}", id);
        return journalistMapper.dtoToResponse(journalistService.update(
                journalistMapper.requestToDto(id, journalistRequest)));
    }

    @Operation(summary = "Blocked Journalist by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Blocked the Journalist"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Journalist not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void blocked(
            @RequestHeader("token") String token,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForJournalistOperationCRUD(token);
        log.info("Blocked author with id {}", id);
        journalistService.blocked(id);
    }

}
