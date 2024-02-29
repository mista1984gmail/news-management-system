package com.example.service.impl;

import com.example.entity.Author;
import com.example.entity.dto.AuthorDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.AuthorMapper;
import com.example.repository.AuthorRepository;
import com.example.service.AuthorService;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "authorsCache")
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    /**
     * Возвращает все существующие Author
     *
     * @param page номер страницы
     * @param size количество AuthorDto на странице (по умолчанию 15)
     * @param orderBy по какому полю сортировать (по умолчанию "username")
     * @param direction как сотрировать (по умолчению "ASC")
     * @return лист с информацией о AuthorDto
     */
    @Override
    public Page<AuthorDto> findAll(Integer page, Integer size, String orderBy, String direction) {
        log.debug("Find all authors on the page {}", page);
        if (orderBy == null || orderBy.isEmpty()) {
            orderBy = Constants.DEFAULT_AUTHORS_ORDER_BY;
        }
        if (direction == null || direction.isEmpty()) {
            direction = Constants.DEFAULT_DIRECTION;
        }
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(orderBy).ascending());
        if (direction.equals("DESC")){
            pageRequest = PageRequest.of(page, size, Sort.by(orderBy).descending());
        }
        Page<Author> foundAuthors = authorRepository.findAll(pageRequest);
        return foundAuthors.map(authorMapper::entityToDto);
    }

    /**
     * Создаёт новоый Author из AuthorDto
     * задает время создания
     *
     * @param author AuthorDto
     * @return новый AuthorDto
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "authors", allEntries = true)
    public AuthorDto save(AuthorDto author) {
        author.setRegistrationDate(LocalDateTime.now());
        log.debug("Save author: {}", author);
        return authorMapper.entityToDto(authorRepository.save(
                authorMapper.dtoToEntity(author)));
    }

    /**
     * Удаляет существующий Author
     * Используется для уделения soft delete
     *
     * @param id идентификатор Author для удаления
     * @throws EntityNotFoundException если Author не найден
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "authors", key="#id")
    public void deleteById(Long id) {
        Author author = getById(id);
        log.debug("Delete author with id: {}", id);
        authorRepository.delete(author);
    }

    /**
     * Обновляет уже существующий Author из информации полученной в AuthorDto
     *
     * @param author AuthorDto с информацией об обновлении
     * @throws EntityNotFoundException если Author не найден
     * @return обновленный AuthorDto
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "authors", key = "#author.id")
    public AuthorDto update(AuthorDto author) {
        log.debug("Update author: {}", author);
        Author foundAuthor = getById(author.getId());
        author.setRegistrationDate(foundAuthor.getRegistrationDate());
        authorMapper.updateEntity(foundAuthor, author);
        return authorMapper.entityToDto(authorRepository.save(foundAuthor));
    }

    /**
     * Ищет Author по идентификатору
     *
     * @param id идентификатор Author
     * @return найденный AuthorDto
     * @throws EntityNotFoundException если Author не найден
     */
    @Override
    @Cacheable(cacheNames = "authors", key = "#id")
    public AuthorDto findById(Long id) {
        log.debug("Find author with id: {}", id);
        return Optional.of(getById(id))
                .map(authorMapper::entityToDto)
                .get();
    }

    /**
     * Ищет Author по username
     *
     * @param username Author
     * @return найденный Author
     * @throws EntityNotFoundException если Author не найден
     */
    @Override
    public Author findByUsername(String username) {
        log.debug("Find author with username: {}", username);
        Author author = authorRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Author.class, username));
        return author;
    }

    /**
     * Блокирует существующий Author
     *
     * @param id идентификатор Author для блокировки
     * @throws EntityNotFoundException если Author не найден
     */
    @Override
    @Transactional
    public void blocked(Long id) {
        Author foundAuthor = getById(id);
        foundAuthor.setBlocked(true);
        authorRepository.save(foundAuthor);
    }

    /**
     * Ищет Author по идентификатору
     *
     * @param id идентификатор Author
     * @return найденный Author
     * @throws EntityNotFoundException если Author не найден
     */
    private Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Author.class, id));
    }
}
