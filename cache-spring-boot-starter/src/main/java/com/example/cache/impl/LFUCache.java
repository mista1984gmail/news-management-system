package com.example.cache.impl;

import com.example.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class LFUCache implements Cache {

    private Map<Long, Object> CACHE;
    private Map<Long, Long> COUNTER_CACHE;
    private Integer SIZE_CACHE;

    public LFUCache() {
        CACHE = new HashMap<>();
        COUNTER_CACHE = new LinkedHashMap<>();
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
     * Если id, данного объекта нет в кэше, устанавливает
     * счетчик обращения к этому обхъекту на 1, иниче
     * к счетчику прибавляется 1.
     */
    @Override
    public Object save(Long id, Object object) {
        checkCacheSize();
        if (id != null) {
            long count = (COUNTER_CACHE.containsKey(id)) ? COUNTER_CACHE.get(id) + 1L : 1L;
            log.info("Save object with uuid {} saved to cache", id);
            COUNTER_CACHE.put(id, count);
            CACHE.put(id, object);
        }
        return object;
    }

    /**
     * Возращает объект из кэша по переданному id,
     * если объекта нет с таким id - возвращает null.
     * <p>
     * Добавляет 1 к счетчику обращений к элементу.
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
            COUNTER_CACHE.put(id, COUNTER_CACHE.get(id) + 1L);
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
            COUNTER_CACHE.remove(id);
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
     * Удаляет элемент с самым маленьким количеством обращений к
     * этому элементу.
     * Если таких элементов несколько - удаляет самый последний
     * добавленный (правило FIFO)
     */
    private void deleteFromCache() {
        Long minCounter = COUNTER_CACHE.values()
                                       .stream()
                                       .mapToLong(v -> v)
                                       .min()
                                       .orElse(0L);
        if (minCounter != 0) {
            LinkedHashMap<Long, Long> arraysMinElements = COUNTER_CACHE.entrySet()
                                                                       .stream()
                                                                       .filter(a -> a.getValue()
                                                                                     .toString()
                                                                                     .equals(minCounter.toString()))
                                                                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                                               (o1, o2) -> o1, LinkedHashMap::new));
            long sizeArraysMinElements = arraysMinElements.entrySet()
                                                          .stream()
                                                          .count();
            Long idForDelete = arraysMinElements.entrySet()
                                                .stream()
                                                .skip(sizeArraysMinElements - 1)
                                                .findFirst()
                                                .get()
                                                .getKey();
            delete(idForDelete);
        }
    }

}