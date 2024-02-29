package com.example.service.impl;

import com.example.exception.EntityNotFoundException;
import com.example.entity.Journalist;
import com.example.entity.dto.JournalistDto;
import com.example.mapper.JournalistMapper;
import com.example.repository.JournalistRepository;
import com.example.service.JournalistService;
import com.example.util.ConstantsNews;
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
@CacheConfig(cacheNames = "journalistsCache")
public class JournalistServiceImpl implements JournalistService {

    private final JournalistRepository journalistRepository;
    private final JournalistMapper journalistMapper;

    /**
     * Возвращает все существующие Journalis
     *
     * @param page номер страницы
     * @param size количество JournalistDto на странице (по умолчанию 15)
     * @param orderBy по какому полю сортировать (по умолчанию "username")
     * @param direction как сотрировать (по умолчению "ASC")
     * @return лист с информацией о JournalistDto
     */
    @Override
    public Page<JournalistDto> findAll(Integer page, Integer size, String orderBy, String direction) {
        log.debug("Find all journalists on the page {}", page);
        if (orderBy == null || orderBy.isEmpty()) {
            orderBy = ConstantsNews.DEFAULT_JOURNALIST_ORDER_BY;
        }
        if (direction == null || direction.isEmpty()) {
            direction = ConstantsNews.DEFAULT_DIRECTION;
        }
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(orderBy).ascending());
        if (direction.equals("DESC")){
            pageRequest = PageRequest.of(page, size, Sort.by(orderBy).descending());
        }
        Page<Journalist> foundAuthors = journalistRepository.findAll(pageRequest);
        return foundAuthors.map(journalistMapper::entityToDto);
    }

    /**
     * Создаёт новоый Journalist из JournalistDto
     * задает время создания
     *
     * @param journalist JournalistDto
     * @return новый JournalistDto
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "journalists", allEntries = true)
    public JournalistDto save(JournalistDto journalist) {
        journalist.setRegistrationDate(LocalDateTime.now());
        log.debug("Save journalist: {}", journalist);
        return journalistMapper.entityToDto(journalistRepository.save(
                journalistMapper.dtoToEntity(journalist)));
    }

    /**
     * Удаляет существующий Journalist
     * Используется для уделения soft delete
     *
     * @param id идентификатор Journalist для удаления
     * @throws EntityNotFoundException если Journalist не найден
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "journalists", key="#id")
    public void deleteById(Long id) {
        Journalist journalist = getById(id);
        log.debug("Delete journalist with id: {}", id);
        journalistRepository.delete(journalist);
    }

    /**
     * Обновляет уже существующий Journalist из информации полученной в JournalistDto
     *
     * @param journalist JournalistDto с информацией об обновлении
     * @throws EntityNotFoundException если Journalist не найден
     * @return обновленный JournalistDto
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "journalists", key = "#journalist.id")
    public JournalistDto update(JournalistDto journalist) {
        log.debug("Update journalist: {}", journalist);
        Journalist foundJournalist = getById(journalist.getId());
        journalist.setRegistrationDate(foundJournalist.getRegistrationDate());
        journalistMapper.updateEntity(foundJournalist, journalist);
        return journalistMapper.entityToDto(journalistRepository.save(foundJournalist));
    }

    /**
     * Ищет Journalist по идентификатору
     *
     * @param id идентификатор Journalist
     * @return найденный JournalistDto
     * @throws EntityNotFoundException если Journalist не найден
     */
    @Override
    @Cacheable(cacheNames = "journalists", key = "#id")
    public JournalistDto findById(Long id) {
        log.debug("Find journalist with id: {}", id);
        return Optional.of(getById(id))
                .map(journalistMapper::entityToDto)
                .get();
    }

    /**
     * Ищет Journalist по username
     *
     * @param username Journalist
     * @return найденный Journalist
     * @throws EntityNotFoundException если Journalist не найден
     */
    @Override
    public Journalist findByUsername(String username) {
        log.debug("Find journalist with username: {}", username);
        Journalist journalist = journalistRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Journalist.class, username));
        return journalist;
    }

    /**
     * Блокирует существующий Journalist
     *
     * @param id идентификатор Journalist для блокировки
     * @throws EntityNotFoundException если Journalist не найден
     */
    @Override
    @Transactional
    public void blocked(Long id) {
        Journalist foundJournalist = getById(id);
        foundJournalist.setBlocked(true);
        journalistRepository.save(foundJournalist);
    }

    /**
     * Ищет Journalist по идентификатору
     *
     * @param id идентификатор Journalist
     * @return найденный Journalist
     * @throws EntityNotFoundException если Journalist не найден
     */
    private Journalist getById(Long id) {
        return journalistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Journalist.class, id));
    }
}
