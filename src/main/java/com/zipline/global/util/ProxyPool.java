package com.zipline.global.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.zipline.dto.publicItem.ProxyInfoDTO;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProxyPool {
    private final ConcurrentLinkedQueue<ProxyInfoDTO> availableProxies = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, ProxyInfoDTO> inUseProxies = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> proxyFailureCount = new ConcurrentHashMap<>();
    private final ExecutorService validationExecutor = Executors.newFixedThreadPool(10);
    
    @Value("${proxy.max-failures:3}")
    private int maxFailures;
    
    @Value("${proxy.validation-timeout:5000}")
    private int validationTimeout;
    
    @Value("${proxy.validation-url:https://www.google.com}")
    private String validationUrl;
    
    @PostConstruct
    public void init() {
        try {
            loadProxies();  // 기본 프록시 목록만 로드
            
            // 검증은 별도 스레드에서 비동기로 실행
            CompletableFuture.runAsync(this::validateProxies)
                .exceptionally(throwable -> {
                    log.error("프록시 검증 중 오류 발생: {}", throwable.getMessage());
                    return null;
                });
            
            // 초기 프록시가 없어도 애플리케이션은 계속 실행
            if (getAvailableProxyCount() == 0) {
                log.warn("초기 사용 가능한 프록시가 없습니다. 백그라운드에서 계속 검증을 시도합니다.");
            }
        } catch (Exception e) {
            log.error("프록시 풀 초기화 중 오류 발생: {}. 애플리케이션은 계속 실행됩니다.", e.getMessage());
        }
    }
    
    @PreDestroy
    public void shutdown() {
        validationExecutor.shutdown();
        try {
            if (!validationExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                validationExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            validationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private void loadProxies() {
        try {
            ClassPathResource resource = new ClassPathResource("proxy-list.txt");
            List<String> lines = new BufferedReader(
                new InputStreamReader(resource.getInputStream())
            ).lines()
             .filter(line -> !line.trim().isEmpty())
             .collect(Collectors.toList());
            
            for (String line : lines) {
                try {
                    ProxyInfoDTO proxy = ProxyInfoDTO.fromString(line.trim());
                    availableProxies.offer(proxy);
                    proxyFailureCount.put(proxy.getKey(), new AtomicInteger(0));
                    log.debug("프록시 추가됨: {}", proxy.getKey());
                } catch (Exception e) {
                    log.warn("잘못된 프록시 형식: {}", line, e);
                }
            }
            
            log.info("프록시 풀 초기화 완료. 총 {}개의 프록시 로드됨", availableProxies.size());
        } catch (Exception e) {
            log.error("프록시 목록 로드 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    private void validateProxies() {
        log.info("프록시 검증 시작");
        List<ProxyInfoDTO> proxiesToValidate = new ArrayList<>(availableProxies);
        availableProxies.clear();
        
        for (ProxyInfoDTO proxy : proxiesToValidate) {
            validationExecutor.submit(() -> validateProxy(proxy));
        }
        
        // 검증이 완료될 때까지 대기
        validationExecutor.shutdown();
        try {
            if (!validationExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("프록시 검증 시간 초과");
            }
        } catch (InterruptedException e) {
            log.error("프록시 검증 중 인터럽트 발생", e);
            Thread.currentThread().interrupt();
        }
        
        log.info("프록시 검증 완료. 사용 가능한 프록시: {}", availableProxies.size());
    }
    
    private void validateProxy(ProxyInfoDTO proxy) {
        log.info("프록시 검증 시작: {}", proxy.getKey());
        
        try {
            // 소켓 연결 테스트
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(proxy.getHost(), proxy.getPort()), validationTimeout);
            socket.close();
            
            // HTTP 연결 테스트
            Proxy proxyObj = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
            URL url = new URL(validationUrl);
            URLConnection conn = url.openConnection(proxyObj);
            conn.setConnectTimeout(validationTimeout);
            conn.setReadTimeout(validationTimeout);
            conn.connect();
            
            // 연결 성공 시 사용 가능한 프록시로 추가
            availableProxies.offer(proxy);
            log.info("프록시 검증 성공: {} (연결 시간: {}ms)", proxy.getKey(), validationTimeout);
        } catch (Exception e) {
            log.warn("프록시 검증 실패: {} - {}", proxy.getKey(), e.getMessage());
            proxyFailureCount.get(proxy.getKey()).incrementAndGet();
        }
    }
    
    public ProxyInfoDTO getNextAvailableProxy() {
        ProxyInfoDTO proxy = availableProxies.poll();
        if (proxy != null) {
            inUseProxies.put(proxy.getKey(), proxy);
            log.info("프록시 할당: {} (사용 가능: {}, 사용 중: {})", 
                proxy.getKey(), 
                getAvailableProxyCount(), 
                getInUseProxyCount());
            return proxy;
        }
        
        // 사용 가능한 프록시가 없으면 풀 새로고침
        if (availableProxies.isEmpty()) {
            log.warn("사용 가능한 프록시가 없습니다. 프록시 풀을 새로고침합니다.");
            refreshProxyPool();
            proxy = availableProxies.poll();
            if (proxy != null) {
                inUseProxies.put(proxy.getKey(), proxy);
                log.info("새로고침 후 프록시 할당: {} (사용 가능: {}, 사용 중: {})", 
                    proxy.getKey(), 
                    getAvailableProxyCount(), 
                    getInUseProxyCount());
                return proxy;
            }
        }
        
        log.error("프록시를 할당할 수 없습니다. (사용 가능: {}, 사용 중: {})", 
            getAvailableProxyCount(), 
            getInUseProxyCount());
        return null;
    }
    
    public void releaseProxy(ProxyInfoDTO proxy) {
        if (proxy != null) {
            inUseProxies.remove(proxy.getKey());
            availableProxies.offer(proxy);
            log.info("프록시 반환: {} (사용 가능: {}, 사용 중: {})", 
                proxy.getKey(), 
                getAvailableProxyCount(), 
                getInUseProxyCount());
        }
    }
    
    public void markProxyAsFailed(ProxyInfoDTO proxy) {
        if (proxy != null) {
            AtomicInteger failures = proxyFailureCount.get(proxy.getKey());
            if (failures != null) {
                int currentFailures = failures.incrementAndGet();
                if (currentFailures >= maxFailures) {
                    log.warn("프록시 {} 실패 횟수 초과 ({}회). 풀에서 제거됨", proxy.getKey(), currentFailures);
                    proxyFailureCount.remove(proxy.getKey());
                    inUseProxies.remove(proxy.getKey());
                } else {
                    availableProxies.offer(proxy);
                    log.info("프록시 {} 실패 ({}회). 재시도 가능 목록에 추가", proxy.getKey(), currentFailures);
                }
            }
        }
    }
    
    public int getAvailableProxyCount() {
        return availableProxies.size();
    }
    
    public int getInUseProxyCount() {
        return inUseProxies.size();
    }
    
    public List<ProxyInfoDTO> getActiveProxies() {
        return new ArrayList<>(inUseProxies.values());
    }
    
    public void refreshProxyPool() {
        log.info("프록시 풀 새로고침 시작");
        availableProxies.clear();
        inUseProxies.clear();
        proxyFailureCount.clear();
        loadProxies();
        validateProxies();
        log.info("프록시 풀 새로고침 완료");
    }
} 