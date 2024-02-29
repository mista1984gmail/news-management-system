package com.example.service.impl;

import com.example.controller.request.CommentRequest;
import com.example.entity.Author;
import com.example.entity.Comment;
import com.example.entity.dto.CommentDto;
import com.example.exception.AuthorDoesNotMatchException;
import com.example.exception.AuthorIsBlockedException;
import com.example.exception.CommentNotCorrespondsToNewException;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.CommentMapper;
import com.example.newsservice.NewsResponse;
import com.example.newsservice.NewsServiceClient;
import com.example.repository.CommentRepository;
import com.example.service.AuthorService;
import com.example.service.CommentService;
import com.example.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "commentsCache")
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AuthorService authorService;
    private final NewsServiceClient newsServiceClient;

    /**
     * Создаёт новоый Comment из CommentRequest
     * задает время создания и обновления.
     * Проверяет есть ли такая новость по newsId новости,
     * если новости нет - выбрасывается соответствующее исключение.
     * Проверяет есть ли такой автор комментария и не заблоктрован ли он,
     * если заблокирован - выбрасывается исключение AuthorIsBlockedException
     *
     * @param newsId id News, которой он принадлежит
     * @param commentRequest CommentRequest
     * @return новый CommentDto
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "comments", allEntries = true)
    public CommentDto save(Long newsId, CommentRequest commentRequest) {
        NewsResponse news = newsServiceClient.getNewsById(newsId);
        Author author = authorService.findByUsername(commentRequest.getUsername());
        if(author.isBlocked()){
            throw new AuthorIsBlockedException(author.getId());
        }
        LocalDateTime createDate = LocalDateTime.now();
        Comment comment = Comment.builder()
                .createDate(createDate)
                .updateDate(createDate)
                .text(commentRequest.getText())
                .author(author)
                .newsId(news.getId())
                .build();
        return commentMapper.entityToDto(commentRepository.save(comment));
    }

    /**
     * Удаляет существующий Comment
     * Используется для уделения soft delete
     * Проверяет есть ли такая новость по newsId новости,
     * если новости нет - выбрасывается соответствующее исключение.
     *
     * @param commentId идентификатор Comment для удаления
     * @param newsId идентификатор News
     * @throws EntityNotFoundException если Comment не найден
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "comments", key="#commentId")
    public void deleteById(Long newsId, Long commentId) {
        NewsResponse news = newsServiceClient.getNewsById(newsId);
        Comment comment = getById(commentId);
        log.debug("Delete comment with id: {}", commentId);
        if(news.getId() != comment.getNewsId()){
            throw new CommentNotCorrespondsToNewException(commentId, newsId);
        } else {
            commentRepository.delete(comment);
        }
    }

    /**
     * Обновляет уже существующий Comment из информации полученной в CommentRequest
     * Меняет дату обновления.
     * Проверяет есть ли такая новость по newsId новости,
     * если новости нет - выбрасывается соответствующее исключение.
     * Проверяет есть ли такой автор комментария и не заблоктрован ли он,
     * если заблокирован - выбрасывается исключение AuthorIsBlockedException
     * Также проверяется, чтобы автор менял только свой комментарий.
     *
     * @param commentId     идентификатор Comment для обновления
     * @param commentRequest CommentRequest с информацией об обновлении
     * @param newsId идентификатор News
     * @throws EntityNotFoundException если Comment не найден
     * @return обновленный CommentDto
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "comments", key = "#commentId")
    public CommentDto update(Long newsId, Long commentId, CommentRequest commentRequest) {
        log.debug("Update comment with id: {}", commentId);
        NewsResponse news = newsServiceClient.getNewsById(newsId);
        Author author = authorService.findByUsername(commentRequest.getUsername());
        Comment comment = getById(commentId);
        if(author.isBlocked()){
            throw new AuthorIsBlockedException(author.getId());
        }
        if(comment.getAuthor().getId() != author.getId()){
            throw new AuthorDoesNotMatchException();
        }
        if(news.getId() != comment.getNewsId()){
            throw new CommentNotCorrespondsToNewException(commentId, newsId);
        } else {
            comment.setUpdateDate(LocalDateTime.now());
            comment.setText(commentRequest.getText());
            return commentMapper.entityToDto(commentRepository.save(comment));
        }
    }

    /**
     * Ищет Comment по идентификатору
     * Проверяет есть ли такая новость по newsId новости,
     * если новости нет - выбрасывается соответствующее исключение.
     *
     * @param commentId идентификатор Comment
     * @param newsId идентификатор News
     * @throws EntityNotFoundException если Comment не найден
     *
     * @return найденный CommentDto
     */
    @Override
    @Cacheable(cacheNames = "comments", key = "#commentId")
    public CommentDto findById(Long newsId, Long commentId) {
        log.debug("Find comment with id: {}", commentId);
        NewsResponse news = newsServiceClient.getNewsById(newsId);
        Comment comment = getById(commentId);
        if(news.getId() != comment.getNewsId()){
            throw new CommentNotCorrespondsToNewException(commentId, newsId);
        } else {
            return commentMapper.entityToDto(comment);
        }
    }

    /**
     * Возвращает все существующие Comment
     *
     * @param page номер страницы
     * @param size количество Comment на странице (по умолчанию 15)
     * @param orderBy по какому полю сортировать (по умолчанию "updateDate")
     * @param direction как сотрировать (по умолчению "ASC")
     * @return лист с информацией о CommentDto
     */
    @Override
    public Page<CommentDto> findAllCommentsByNewsId(Long newsId, Integer page, Integer size, String orderBy, String direction) {
        NewsResponse news = newsServiceClient.getNewsById(newsId);
        log.info("Show comment on the page {}", page );
        if (orderBy == null || orderBy.isEmpty()) {
            orderBy = Constants.DEFAULT_COMMENTS_ORDER_BY;
        }
        if (direction == null || direction.isEmpty()) {
            direction = Constants.DEFAULT_DIRECTION;
        }
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(orderBy).ascending());
        if (direction.equals("DESC")){
            pageRequest = PageRequest.of(page, size, Sort.by(orderBy).descending());
        }
        Page<Comment> foundComments = commentRepository.findAllByNewsId(news.getId(), pageRequest);
        return foundComments.map(commentMapper::entityToDto);
    }

    /**
     * Ищет Comment по идентификатору
     *
     * @param id идентификатор Comment
     * @return найденный Comment
     * @throws EntityNotFoundException если Comment не найден
     */
    private Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Comment.class, id));
    }
}
