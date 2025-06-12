package com.zipline.global.config;

import com.zipline.global.util.SmsSignatureGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class SmsWebClientConfig {

  private final SmsSignatureGenerator smsSignatureGenerator;
  @Value("${sms.api-key}")
  String apiKey;

  public SmsWebClientConfig(SmsSignatureGenerator smsSignatureGenerator) {
    this.smsSignatureGenerator = smsSignatureGenerator;
  }

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .baseUrl("https://api.solapi.com/messages/v4")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
          return smsSignatureGenerator.generateSignature()
              .map(signatureResult -> {
                String authorizationHeader = String.format(
                    "HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                    apiKey,
                    signatureResult.get("time"),
                    signatureResult.get("salt"),
                    signatureResult.get("hash")
                );

                return ClientRequest.from(clientRequest)
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .build();
              });
        }))
        .build();
  }
}