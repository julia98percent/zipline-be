package com.zipline.api;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.zipline.global.exception.external.ExternalException;
import com.zipline.global.exception.external.errorcode.ExternalErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoGeoClient {

	private static final String KAKAO_GEOCODE_URL = "https://dapi.kakao.com/v2/local/search/address.json";

	private final RestTemplate restTemplate;

	@Value("${kakao.api.key}")
	private String apiKey;

	public KakaoGeocodeResponseDTO getCoordinatesByAddress(String address) {
		URI uri = UriComponentsBuilder.fromUriString(KAKAO_GEOCODE_URL)
			.queryParam("query", address)
			.build()
			.encode(StandardCharsets.UTF_8)
			.toUri();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + apiKey);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		log.debug("[Kakao API URI] {}", uri);

		try {
			ResponseEntity<KakaoGeocodeResponseDTO> response = restTemplate.exchange(
				uri, HttpMethod.GET, entity, KakaoGeocodeResponseDTO.class);

			return response.getBody();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				log.error("[Kakao API 401 인증 오류] API Key 확인 필요. uri={}", uri);
				throw new ExternalException(ExternalErrorCode.KAKAO_INVALID_API_KEY);
			}
			log.error("[Kakao API 4xx 오류] status={}, uri={}", e.getStatusCode(), uri);
			throw new ExternalException(ExternalErrorCode.KAKAO_CLIENT_ERROR);
		} catch (HttpServerErrorException e) {
			log.error("[Kakao API 5xx 서버 오류] status={}, uri={}", e.getStatusCode(), uri);
			throw new ExternalException(ExternalErrorCode.KAKAO_SERVER_ERROR);
		} catch (ResourceAccessException e) {
			log.error("[Kakao API 연결 실패] {}", e.getMessage());
			throw new ExternalException(ExternalErrorCode.KAKAO_CONNECTION_FAIL);
		} catch (RestClientException e) {
			log.error("[Kakao API 기타 오류] {}", e.getMessage(), e);
			throw new ExternalException(ExternalErrorCode.KAKAO_UNKNOWN_ERROR);
		}
	}
}
