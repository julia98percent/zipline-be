package com.zipline.service.message;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.zipline.global.exception.message.errorcode.MessageErrorCode;
import com.zipline.global.exception.message.MessageException;
import com.zipline.service.message.dto.message.request.SendMessageRequestDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

	private final WebClient webClient;

	public String sendMessage(List<SendMessageRequestDTO> request) {

		try {
			Map<String, Object> wrappedRequest = Map.of("messages", request);

			String response = webClient.post()
				.uri("/send-many/detail")
				.bodyValue(wrappedRequest)
				.retrieve()
				.bodyToMono(String.class)
				.block();

			log.info("메시지 전송 성공: {} 개의 메시지", request.size());
			return response;

		} catch (Exception e) {
			throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED);
		}
	}
}