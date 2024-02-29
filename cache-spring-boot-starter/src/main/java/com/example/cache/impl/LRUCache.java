package com.example.cache.impl;

import com.example.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class LRUCache implements Cache {

    private Map<Long, Object> CACHE;
    private Map<Long, LocalDateTime> LAST_ACCESS_CACHE;
    private Integer SIZE_CACHE;

    public LRUCache() {
        CACHE = new HashMap<>();
        LAST_ACCESS_CACHE = new HashMap<>();
    }

    /**
     * Устанавливает размер кэша.
     */
    @Override
    public void setSizeCache(Integer sizeCache) {
        SIZE_CACHE = sizeCache;
    }

    /**
     * Сохраняет переданный id объекта и сам объект в кэш.
     * Сохраняет время, передачи объекта в кэш.
     */
    @Override
    public Object save(Long id, Object object) {
        checkCacheSize();
        if (id != null) {
            log.info("Save object with uuid {} saved to cache", id);
            CACHE.put(id, object);
            LAST_ACCESS_CACHE.put(id, LocalDateTime.now());
        }
        return object;
    }

    /**
     * Возращает объект из кэша по переданному id,
     * если объекта нет с таким id - возвращает null.
     * <p>
     * Сохраняет время, обращения к объекту из кэша.
     *
     * @param id объекта для отображения
     * @return объект по id
     */
    @Override
    public Object getById(Long id) {
        Object object = null;
        if (CACHE.containsKey(id)) {
            log.info("Get object from cache");
            System.out.println("Get object from cache");
            object = CACHE.get(id);
            LAST_ACCESS_CACHE.put(id, LocalDateTime.now());
        }
        return object;
    }

    /**
     * Удаляет объект из кэша по переданному id,
     *
     * @param id объекта для удаления
     */
    @Override
    public void delete(Long id) {
        if (CACHE.containsKey(id)) {
            CACHE.remove(id);
            LAST_ACCESS_CACHE.remove(id);
        }
    }

    /**
     * Проверяет существующий размер кэша с установленным.
     * Если существующий размер кэша равен или больше
     * установленного значения - вызывает метод для удаления
     * элементов из кэша.
     */
    private void checkCacheSize() {
        if (CACHE.size() >= SIZE_CACHE) {
            deleteFromCache();
        }
    }

    /**
     * Удаляет элемент из кэша.
     * <p>
     * Удаляет элемент с самым старым временем
     * добавления в кэш.
     */
    private void deleteFromCache() {
        Long idForDelete = LAST_ACCESS_CACHE.entrySet()
                                            .stream()
                                            .sorted(Map.Entry.comparingByValue())
                                            .findFirst()
                                            .get()
                                            .getKey();
        delete(idForDelete);
    }

}