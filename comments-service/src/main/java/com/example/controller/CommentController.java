package com.example.controller;

import com.example.LogMethodExecutionTime;
import com.example.controller.request.CommentRequest;
import com.example.controller.response.CommentResponse;
import com.example.entity.dto.AuthorDto;
import com.example.entity.dto.CommentDto;
import com.example.exception.NoAccessException;
import com.example.exception.TokenIsNotValidException;
import com.example.exception.WrongAuthorException;
import com.example.mapper.CommentMapper;
import com.example.security.JwtUtil;
import com.example.security.SecurityService;
import com.example.security.User;
import com.example.service.CommentService;
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
@RequestMapping("/api/v1/news")
@Tag(name = "CommentController", description = "Documentation")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final SecurityService securityService;

    @Operation(summary = "Find all Comment with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Find Comment with pagination", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{newsId}/comments")
    public Page<CommentResponse> findAll(
            @PathVariable @NotNull @Positive Long newsId,
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE) Integer page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(value = "orderBy", defaultValue = Constants.DEFAULT_COMMENTS_ORDER_BY) String orderBy,
            @RequestParam(value = "direction", defaultValue = Constants.DEFAULT_DIRECTION) String direction) {
        log.info("Find all comments by news with id {}", newsId);
        return commentService.findAllCommentsByNewsId(newsId, page, size, orderBy, direction).map(commentMapper::dtoToResponse);
    }

    @Operation(summary = "Save Comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save Comment", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{newsId}/comments")
    public CommentResponse save(
            @RequestHeader("token") String token,
            @Valid @RequestBody CommentRequest commentRequest,
            @PathVariable @NotNull @Positive Long newsId) {
        securityService.checkAccessForSaveComments(token);
        log.info("Save comment {} to news id {}", commentRequest, newsId);
        return commentMapper.dtoToResponse(commentService.save(newsId, commentRequest));
    }

    @Operation(summary = "Get Comment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Comment", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{newsId}/comments/{commentId}")
    public CommentResponse findById(
            @PathVariable @NotNull @Positive Long newsId,
            @PathVariable @NotNull @Positive Long commentId) {
        log.info("Find comment with id: {}", commentId);
        return commentMapper.dtoToResponse(commentService.findById(newsId, commentId));
    }

    @Operation(summary = "Delete Comment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the Comment"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{newsId}/comments/{commentId}")
    public void deleteById(
            @RequestHeader("token") String token,
            @PathVariable @NotNull @Positive Long newsId,
            @PathVariable @NotNull @Positive Long commentId) {
        securityService.checkAccessForDeleteAndUpdateComments(token, newsId, commentId);
        log.info("Delete comment with id: {}", commentId);
        commentService.deleteById(newsId, commentId);
    }

    @Operation(summary = "Update Comment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the Comment", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @LogMethodExecutionTime
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{newsId}/comments/{commentId}")
    public CommentResponse update(
            @RequestHeader("token") String token,
            @Valid @RequestBody CommentRequest commentRequest,
            @PathVariable @NotNull @Positive Long newsId,
            @PathVariable @NotNull @Positive Long commentId) {
        securityService.checkAccessForDeleteAndUpdateComments(token, newsId, commentId);
        log.info("Update comment with id: {}", commentId);
        return commentMapper.dtoToResponse(commentService.update(newsId, commentId, commentRequest));
    }

}