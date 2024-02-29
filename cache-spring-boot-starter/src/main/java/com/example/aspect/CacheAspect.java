package com.example.aspect;

import com.example.cache.Cache;
import com.example.dto.CommentDto;
import com.example.dto.NewsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@RequiredArgsConstructor
public class CacheAspect {

    private final Cache cache;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(com.example.aspect.annotation.SaveObjectToCache)")
    public void saveMethod() {
    }

    @Pointcut("@annotation(com.example.aspect.annotation.DeleteObjectFromCache)")
    public void deleteMethod() {
    }

    @Pointcut("@annotation(com.example.aspect.annotation.GetObjectFromCache)")
    public void getMethod() {
    }

    @Pointcut("@annotation(com.example.aspect.annotation.UpdateObjectInCache)")
    public void updateMethod() {
    }

    /**
     * Вызывает метод сохранения полученного объекта в кэш.
     */
    @Around(value = "saveMethod()")
    public Object doSaveProfiling(ProceedingJoinPoint pjp) throws Throwable {
        return profilingAndSaveToCache(pjp);
    }

    /**
     * Вызывает метод удаления объекта из кэша по id.
     */
    @Around(value = "deleteMethod()")
    public Object doDeleteProfiling(ProceedingJoinPoint pjp) throws Throwable {
        Long idForDelete = (Long) pjp.getArgs()[0];
        pjp.proceed();
        cache.delete(idForDelete);
        return idForDelete;
    }

    /**
     * Получает объект из кеша по id, если такой там есть.
     * Если такого объекта по id в кэше нет - вызывает метод
     * получения объекта из базы данных.
     */
    @Around(value = "getMethod()")
    public Object doGetProfiling(ProceedingJoinPoint pjp) throws Throwable {
        Long idForGet = (Long) pjp.getArgs()[0];
        Object o;
        o = cache.getById(idForGet);
        if (o == null) {
            return profilingAndSaveToCache(pjp);
        }
        return o;
    }

    /**
     * Вызывает метод обновления переданного объекта в кэше.
     */
    @Around(value = "updateMethod()")
    public Object doUpdateProfiling(ProceedingJoinPoint pjp) throws Throwable {
        return profilingAndSaveToCache(pjp);
    }

    private Object profilingAndSaveToCache(ProceedingJoinPoint pjp) throws Throwable {
        final Signature signature = pjp.getSignature();
        final Class<?> clazz = ((MethodSignature)signature).getReturnType();
        if(clazz.getSimpleName().equals("CommentDto")){
            Object comment = pjp.proceed();
            CommentDto commentDto = objectMapper.convertValue(comment, CommentDto.class);
            cache.save(commentDto.getId(), comment);
            return comment;
        }
        else if(clazz.getSimpleName().equals("NewsDto")){
            Object news = pjp.proceed();
            NewsDto personDto = objectMapper.convertValue(news, NewsDto.class);
            cache.save(personDto.getId(), news);
            return news;
        }
        else {
            Object o = pjp.proceed();
            return o;
        }
    }

}