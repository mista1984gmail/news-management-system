package com.example.controller;

import com.example.LogMethodExecutionTime;
import com.example.controller.request.AuthorRequest;
import com.example.controller.response.AuthorResponse;
import com.example.mapper.AuthorMapper;
import com.example.security.SecurityService;
import com.example.service.AuthorService;
import com.example.util.Constants;
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
@RequestMapping("/api/v1/authors")
@Tag(name = "AuthorController", description = "Documentation")
public class AuthorController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final SecurityService securityService;

    @Operation(summary = "Find all Author with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Find Author with pagination", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public Page<AuthorResponse> findAll(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE) Integer page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(value = "orderBy", defaultValue = Constants.DEFAULT_AUTHORS_ORDER_BY) String orderBy,
            @RequestParam(value = "direction", defaultValue = Constants.DEFAULT_DIRECTION) String direction) {
        securityService.checkAccessForAuthorOperationCRUD(token);
        log.info("Find all authors");
        return authorService.findAll(page, size, orderBy, direction).map(authorMapper::dtoToResponse);
    }

    @Operation(summary = "Save Author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save Author", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PostMapping
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public AuthorResponse save(
            @RequestHeader("token") String token,
            @Valid @RequestBody AuthorRequest authorRequest) {
        securityService.checkAccessForAuthorOperationCRUD(token);
        log.info("Save author {}", authorRequest);
        return authorMapper.dtoToResponse(authorService.save(
                authorMapper.requestToDto(authorRequest)));
    }

    @Operation(summary = "Get Author by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Author", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping("/{id}")
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public AuthorResponse findById(
            @RequestHeader("token") String token,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForAuthorOperationCRUD(token);
        log.info("Find author with id: {}", id);
        return authorMapper.dtoToResponse(authorService.findById(id));
    }

    @Operation(summary = "Delete Author by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the Author"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(
            @RequestHeader("token") String token,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForAuthorOperationCRUD(token);
        log.info("Delete author with id: {}", id);
        authorService.deleteById(id);
    }

    @Operation(summary = "Update Author by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the Author", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PutMapping("/{id}")
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    public AuthorResponse update(
            @RequestHeader("token") String token,
            @Valid @RequestBody AuthorRequest authorRequest,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForAuthorOperationCRUD(token);
        log.info("Update author: {}", id);
        return authorMapper.dtoToResponse(authorService.update(
                authorMapper.requestToDto(id, authorRequest)));
    }

    @Operation(summary = "Blocked Author by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blocked the Author"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void blocked(
            @RequestHeader("token") String token,
            @PathVariable @NotNull @Positive Long id) {
        securityService.checkAccessForAuthorOperationCRUD(token);
        log.info("Blocked author with id {}", id);
        authorService.blocked(id);
    }

}
