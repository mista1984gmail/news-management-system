package com.example.service.impl;

import com.example.exception.EntityNotFoundException;
import com.example.exception.JournalistDoesNotMatchException;
import com.example.exception.JournalistIsBlockedException;
import com.example.controller.request.NewsRequest;
import com.example.entity.Journalist;
import com.example.entity.News;
import com.example.entity.dto.NewsDto;
import com.example.mapper.JournalistMapper;
import com.example.mapper.NewsMapper;
import com.example.repository.NewsRepository;
import com.example.service.JournalistService;
import com.example.service.NewsService;
import com.example.util.ConstantsNews;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "newsCache")
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final JournalistService journalistService;
    private final NewsMapper newsMapper;
    private final JournalistMapper journalistMapper;

    /**
     * Создаёт новоый News из NewsRequest
     * задает время создания и время обновления
     * Проверяет не заблокирован ли Journalist,
     * ессли да - то выбрасывает соответствующее исключение - JournalistIsBlockedException
     *
     * @throws JournalistIsBlockedException если Journalist заблокирован
     * @param newsRequest NewsRequest
     * @return новый NewsDto
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "news", allEntries = true)
    public NewsDto save(NewsRequest newsRequest) {
        Journalist journalist = journalistService.findByUsername(newsRequest.getUsername());
        if(journalist.isBlocked()){
            throw new JournalistIsBlockedException(journalist.getId());
        }
        LocalDateTime createDate = LocalDateTime.now();
        News news = News.builder()
                .createDate(createDate)
                .updateDate(createDate)
                .title(newsRequest.getTitle())
                .text(newsRequest.getText())
                .deleted(false)
                .comments(new ArrayList<>())
                .journalist(journalist)
                .build();
        log.debug("Save news: {}", news);
        return newsMapper.entityToDto(newsRepository.save(news));
    }

    /**
     * Удаляет существующий News
     * Используется для уделения soft delete
     *
     * @param id идентификатор News для удаления
     * @throws EntityNotFoundException если News не найден
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "news", key="#id")
    public void deleteById(Long id) {
        News news = getById(id);
        log.debug("Delete news with id: {}", id);
        newsRepository.delete(news);
    }

    /**
     * Обновляет уже существующий News из информации полученной в NewsRequest
     * Проверяет не заблокирован ли Journalist,
     * ессли да - то выбрасывает соответствующее исключение - JournalistIsBlockedException
     * Проверяет принадлежит ли данная новость этому Journalist,
     * ессли нет - то выбрасывает соответствующее исключение - JournalistDoesNotMatchException
     *
     * @param newsRequest NewsRequest с информацией об обновлении
     * @param id News для обновлении
     * @throws EntityNotFoundException если News не найден
     * @throws JournalistDoesNotMatchException если Journalist не соответствует
     * @throws JournalistIsBlockedException если Journalist заблокирован
     * @return обновленный NewsDto
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "news", key = "#id")
    public NewsDto update(Long id, NewsRequest newsRequest) {
        log.debug("Update news with id {}", id);
        Journalist journalist = journalistService.findByUsername(newsRequest.getUsername());
        News foundNews = getById(id);
        if(journalist.isBlocked()){
            throw new JournalistIsBlockedException(journalist.getId());
        }
        if(foundNews.getJournalist().getId() != journalist.getId()){
            throw new JournalistDoesNotMatchException();
        }
        foundNews.setUpdateDate(LocalDateTime.now());
        foundNews.setTitle(newsRequest.getTitle());
        foundNews.setText(newsRequest.getText());
        return newsMapper.entityToDto(newsRepository.save(foundNews));
    }

    /**
     * Ищет News по идентификатору
     *
     * @param id идентификатор News
     * @return найденный NewsDto
     * @throws EntityNotFoundException если News не найден
     */
    @Override
    @Cacheable(cacheNames = "news", key="#id")
    public NewsDto findById(Long id) {
        log.debug("Find news with id: {}", id);
        return Optional.of(getById(id))
                .map(newsMapper::entityToDto)
                .get();
    }

    /**
     * Возвращает все существующие News
     *
     * @param page номер страницы
     * @param size количество NewsDto на странице (по умолчанию 15)
     * @param orderBy по какому полю сортировать (по умолчанию "updateDate")
     * @param direction как сотрировать (по умолчению "ASC")
     * @return лист с информацией о NewsDto
     */
    @Override
    public Page<NewsDto> findAllWithPaginationAndSorting(Integer page, Integer size, String orderBy, String direction) {
        log.info("Show news on the page {}", page );
        if (orderBy == null || orderBy.isEmpty()) {
            orderBy = ConstantsNews.DEFAULT_NEWS_ORDER_BY;
        }
        if (direction == null || direction.isEmpty()) {
            direction = ConstantsNews.DEFAULT_DIRECTION;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<News> foundNews = newsRepository.findAll(pageRequest);
        return foundNews.map(newsMapper::entityToDto);
    }

    /**
     * Возвращает все существующие News, принадлежащие данному Journalist
     *
     * @param journalistId Journalist идентификационный номер
     * @return лист с информацией о NewsDto
     */
    @Override
    public List<NewsDto> findAllByJournalist(Long journalistId) {
        Journalist journalist = journalistMapper.dtoToEntity(journalistService.findById(journalistId));
        return newsMapper.toListDto(newsRepository.findAllByJournalist(journalist));
    }

    /**
     * Ищет News по идентификатору
     *
     * @param id идентификатор News
     * @return найденный News
     * @throws EntityNotFoundException если News не найден
     */
    private News getById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(News.class, id));
    }

}
