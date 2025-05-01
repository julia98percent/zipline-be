package com.zipline.service.proxy.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.zipline.infrastructure.proxy.dto.ProxyInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyStatusDTO {
    private int availableProxyCount;
    private int inUseProxyCount;
    private List<ProxyInfoDTO> activeProxies;
    
    /**
     * ProxyPool에서 ProxyStatus 객체를 생성합니다.
     * 
     * @param availableCount 사용 가능한 프록시 수
     * @param inUseCount 사용 중인 프록시 수
     * @param activeProxies 활성 프록시 목록
     * @return ProxyStatus 객체
     */
    public static ProxyStatusDTO of(int availableCount, int inUseCount, List<ProxyInfoDTO> activeProxies) {
        return ProxyStatusDTO.builder()
            .availableProxyCount(availableCount)
            .inUseProxyCount(inUseCount)
            .activeProxies(activeProxies)
            .build();
    }
    
    /**
     * 프록시 풀의 총 프록시 수를 반환합니다.
     * 
     * @return 총 프록시 수
     */
    public int getTotalProxyCount() {
        return availableProxyCount + inUseProxyCount;
    }
    
    /**
     * 프록시 풀의 사용률을 계산합니다.
     * 
     * @return 사용률 (%)
     */
    public double getUsageRate() {
        if (getTotalProxyCount() == 0) {
            return 0.0;
        }
        return (double) inUseProxyCount / getTotalProxyCount() * 100;
    }
    
    /**
     * 품질 점수가 높은 순으로 정렬된 활성 프록시 목록을 반환합니다.
     * 
     * @return 정렬된 활성 프록시 목록
     */
    public List<ProxyInfoDTO> getSortedActiveProxiesByQuality() {
        return activeProxies.stream()
            .sorted((p1, p2) -> Double.compare(p2.calculateQualityScore(), p1.calculateQualityScore()))
            .collect(Collectors.toList());
    }
    
    /**
     * 프록시 풀이 비어있는지 확인합니다.
     * 
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return getTotalProxyCount() == 0;
    }
    
    /**
     * 프록시 풀이 가득 찼는지 확인합니다.
     * 
     * @return 가득 찼으면 true
     */
    public boolean isFull() {
        return inUseProxyCount == getTotalProxyCount();
    }
} 