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
public class ProxyListDTO {
    private ProxyInfoWrapper proxy;
    
    /**
     * 프록시 목록을 생성합니다.
     * 
     * @param proxies 프록시 목록
     * @return ProxyList 객체
     */
    public static ProxyListDTO of(List<ProxyInfoDTO> proxies) {
        return ProxyListDTO.builder()
            .proxy(ProxyInfoWrapper.builder()
                .proxies(proxies)
                .build())
            .build();
    }
    
    /**
     * 프록시 목록을 가져옵니다.
     * 
     * @return 프록시 목록
     */
    public List<ProxyInfoDTO> getProxies() {
        return proxy != null ? proxy.getProxies() : List.of();
    }
    
    /**
     * 프록시 목록의 크기를 반환합니다.
     * 
     * @return 프록시 목록의 크기
     */
    public int size() {
        return getProxies().size();
    }
    
    /**
     * 품질 점수가 높은 순으로 정렬된 프록시 목록을 반환합니다.
     * 
     * @return 정렬된 프록시 목록
     */
    public List<ProxyInfoDTO> getSortedProxiesByQuality() {
        return getProxies().stream()
            .sorted((p1, p2) -> Double.compare(p2.calculateQualityScore(), p1.calculateQualityScore()))
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 국가의 프록시만 필터링하여 반환합니다.
     * 
     * @param country 국가 코드
     * @return 필터링된 프록시 목록
     */
    public List<ProxyInfoDTO> getProxiesByCountry(String country) {
        return getProxies().stream()
            .filter(p -> country.equals(p.getCountry()))
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 도시의 프록시만 필터링하여 반환합니다.
     * 
     * @param city 도시 이름
     * @return 필터링된 프록시 목록
     */
    public List<ProxyInfoDTO> getProxiesByCity(String city) {
        return getProxies().stream()
            .filter(p -> city.equals(p.getCity()))
            .collect(Collectors.toList());
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProxyInfoWrapper {
        private List<ProxyInfoDTO> proxies;
    }
} 