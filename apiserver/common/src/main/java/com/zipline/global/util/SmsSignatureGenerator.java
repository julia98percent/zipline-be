package com.zipline.global.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SmsSignatureGenerator {

  @Value("${sms.secret-key}")
  String secretKey;

  @Value("${sms.auth-method}")
  String authMethod;

  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return HexUtils.toHexString(salt);
  }

  public Mono<Map<String, String>> generateSignature() {
    return Mono.fromCallable(() -> {
      Map<String, String> result = new HashMap<>();

      String now = Instant.now().toString();
      String salt = generateSalt();
      String authMessage = now + salt;

      Mac mac = Mac.getInstance(authMethod);
      SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), authMethod);
      mac.init(keySpec);

      byte[] hmacBytes = mac.doFinal(authMessage.getBytes());

      String hash = HexUtils.toHexString(hmacBytes);

      result.put("time", now);
      result.put("salt", salt);
      result.put("hash", hash);

      return result;

    }).onErrorMap(e -> new RuntimeException("signature를 생성 중 오류가 발생했습니다.", e));
  }
}