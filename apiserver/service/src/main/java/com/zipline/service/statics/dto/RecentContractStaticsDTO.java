package com.zipline.service.statics.dto;

import com.zipline.entity.contract.Contract;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class RecentContractStaticsDTO {
  private Long uid;
  private String category;
  private LocalDate contractStartDate;
  private LocalDate contractEndDate;
  private String status;
  private String customerName;
  private List<String> documentUrls;

  public static RecentContractStaticsDTO of(Contract contract, String customerName, List<String> documentUrls) {
    return RecentContractStaticsDTO.builder()
        .uid(contract.getUid())
        .category(contract.getCategory())
        .contractStartDate(contract.getContractStartDate())
        .contractEndDate(contract.getContractEndDate())
        .status(contract.getStatus().name())
        .customerName(customerName)
        .documentUrls(documentUrls)
        .build();
  }
}
