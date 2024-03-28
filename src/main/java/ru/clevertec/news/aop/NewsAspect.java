package ru.clevertec.news.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.CacheFactory;
import ru.clevertec.news.entity.dto.NewsResponse;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Aspect class providing caching functionality for CommentServiceImpl methods.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Aspect
@Component
@Profile("dev")
public class NewsAspect {

    private final CacheFactory<Long, NewsResponse> cacheFactory;
    private final Cache<Long, NewsResponse> cache;
    private final Lock lock;

    /**
     * Constructor for CommentAspect.
     *
     * @param cacheFactory Factory for creating the cache.
     */
    public NewsAspect(CacheFactory<Long, NewsResponse> cacheFactory) {
        this.cacheFactory = cacheFactory;
        cache = cacheFactory.createCache();
        lock = new ReentrantLock();
    }

    /**
     * Implements cache via AOP for the 'get' method in NewsServiceImpl, providing caching.
     */
    @Around("ru.clevertec.news.aop.NewsPointcut.pointcutGetMethod()")
    public NewsResponse get(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Long id = (Long) joinPoint.getArgs()[0];
            NewsResponse newsResponse = cache.get(id);
            if (newsResponse == null) {
                newsResponse = (NewsResponse) joinPoint.proceed();
            }
            cache.put(newsResponse.id(), newsResponse);
            return newsResponse;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Implements cache via for the 'create' method in NewsServiceImpl, providing caching.
     */
    @Around("ru.clevertec.news.aop.NewsPointcut.pointcutCreateMethod()")
    public NewsResponse create(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            NewsResponse response = (NewsResponse) joinPoint.proceed();
            Long id = response.id();
            cache.put(id, response);
            return response;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Implements cache via for the 'archive' method in NewsServiceImpl, removing item from cache.
     */
    @Around("ru.clevertec.news.aop.NewsPointcut.pointcutArchiveMethod()")
    public void archived(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Long id = (Long) joinPoint.getArgs()[0];
            joinPoint.proceed();
            cache.removeByKey(id);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Implements cache via for the 'update' method in NewsServiceImpl, updating cache.
     */
    @Around("ru.clevertec.news.aop.NewsPointcut.pointcutUpdateMethod()")
    public NewsResponse patch(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Long id = (Long) joinPoint.getArgs()[0];
            NewsResponse response = (NewsResponse) joinPoint.proceed();
            cache.removeByKey(id);
            cache.put(id, response);
            return response;
        } finally {
            lock.unlock();
        }
    }
}
