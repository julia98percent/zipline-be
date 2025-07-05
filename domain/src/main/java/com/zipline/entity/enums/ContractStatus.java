package com.zipline.entity.enums;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum ContractStatus {
  LISTED("매물 등록됨"),
  NEGOTIATING("협상 중"),
  INTENT_SIGNED("가계약"),
  CANCELLED("계약 취소"),
  CONTRACTED("계약 체결"),
  IN_PROGRESS("계약 진행 중"),
  PAID_COMPLETE("잔금 지급 완료"),
  REGISTERED("등기 완료"),
  MOVED_IN("입주 완료"),
  TERMINATED("계약 해지"),
  CLOSED("계약 종료");

  private final String description;

  ContractStatus(String description) {
    this.description = description;
  }

  public static List<ContractStatus> getContractedStatuses() {
    return List.of(CONTRACTED, IN_PROGRESS, PAID_COMPLETE, REGISTERED, MOVED_IN);
  }

  public static List<ContractStatus> getClosedStatuses() {
    return List.of(CANCELLED, TERMINATED, CLOSED);
  }

  public static List<ContractStatus> getInProgressStatuses() {
    return Arrays.stream(values())
        .filter(status -> status != LISTED && status != CANCELLED && status != TERMINATED
            && status != CLOSED)
        .toList();
  }
}