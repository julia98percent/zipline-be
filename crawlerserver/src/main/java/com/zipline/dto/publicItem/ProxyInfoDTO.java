package com.zipline.dto.publicItem;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyInfoDTO {
    private String host;
    private int port;
    private String protocol;
    private String country;
    private String city;
    private String provider;
    
    @JsonProperty("response_time")
    private double responseTime;
    
    private int uptime;
    
    @JsonProperty("last_checked")
    private LocalDateTime lastChecked;
    
    /**
     * 문자열에서 프록시 정보를 생성합니다.
     * 
     * @param proxyString "호스트:포트" 형식의 문자열
     * @return ProxyInfo 객체
     * @throws IllegalArgumentException 잘못된 형식의 문자열이 제공된 경우
     */
    public static ProxyInfoDTO fromString(String proxyString) {
        String[] parts = proxyString.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 프록시 형식: " + proxyString);
        }
        
        return ProxyInfoDTO.builder()
            .host(parts[0].trim())
            .port(Integer.parseInt(parts[1].trim()))
            .build();
    }
    
    /**
     * 프록시의 고유 키를 반환합니다.
     * 
     * @return "호스트:포트" 형식의 문자열
     */
    public String getKey() {
        return host + ":" + port;
    }
    
    /**
     * 프록시의 응답 시간을 업데이트합니다.
     * 
     * @param responseTime 새로운 응답 시간 (밀리초)
     * @return 업데이트된 ProxyInfo 객체
     */
    public ProxyInfoDTO withUpdatedResponseTime(double responseTime) {
        return ProxyInfoDTO.builder()
            .host(this.host)
            .port(this.port)
            .protocol(this.protocol)
            .country(this.country)
            .city(this.city)
            .provider(this.provider)
            .responseTime(responseTime)
            .uptime(this.uptime)
            .lastChecked(LocalDateTime.now())
            .build();
    }
    
    /**
     * 프록시의 가용성을 업데이트합니다.
     * 
     * @param uptime 새로운 가용성 값 (%)
     * @return 업데이트된 ProxyInfo 객체
     */
    public ProxyInfoDTO withUpdatedUptime(int uptime) {
        return ProxyInfoDTO.builder()
            .host(this.host)
            .port(this.port)
            .protocol(this.protocol)
            .country(this.country)
            .city(this.city)
            .provider(this.provider)
            .responseTime(this.responseTime)
            .uptime(uptime)
            .lastChecked(LocalDateTime.now())
            .build();
    }
    
    /**
     * 프록시의 마지막 검사 시간을 업데이트합니다.
     * 
     * @return 업데이트된 ProxyInfo 객체
     */
    public ProxyInfoDTO withUpdatedLastChecked() {
        return ProxyInfoDTO.builder()
            .host(this.host)
            .port(this.port)
            .protocol(this.protocol)
            .country(this.country)
            .city(this.city)
            .provider(this.provider)
            .responseTime(this.responseTime)
            .uptime(this.uptime)
            .lastChecked(LocalDateTime.now())
            .build();
    }
    
    /**
     * 프록시의 품질 점수를 계산합니다.
     * 
     * @return 품질 점수 (높을수록 좋음)
     */
    public double calculateQualityScore() {
        // 응답 시간이 낮을수록, 가용성이 높을수록 좋음
        double responseTimeScore = Math.max(0, 1000 - responseTime) / 10; // 최대 100점
        double uptimeScore = uptime; // 0-100점
        
        // 가중치 적용 (응답 시간 60%, 가용성 40%)
        return (responseTimeScore * 0.6) + (uptimeScore * 0.4);
    }
    
    /**
     * 프록시가 사용 가능한지 확인합니다.
     * 
     * @param minResponseTime 최소 응답 시간 (밀리초)
     * @param minUptime 최소 가용성 (%)
     * @return 사용 가능 여부
     */
    public boolean isUsable(double minResponseTime, int minUptime) {
        return responseTime <= minResponseTime && uptime >= minUptime;
    }
} 