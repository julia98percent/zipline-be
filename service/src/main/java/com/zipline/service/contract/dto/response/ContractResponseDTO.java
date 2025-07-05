package com.zipline.service.contract.dto.response;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.enums.ContractCustomerRole;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
public class ContractResponseDTO {

  private Long uid;
  private String category;
  private BigInteger deposit;
  private BigInteger monthlyRent;
  private BigInteger price;
  private LocalDate contractStartDate;
  private LocalDate contractEndDate;
  private LocalDate expectedContractEndDate;
  private LocalDate contractDate;
  private String status;
  private List<ContractCustomerInfo> lessorOrSellerInfo;
  private List<ContractCustomerInfo> lesseeOrBuyerInfo;

  private List<DocumentDTO> documents;
  private String propertyAddress;
  private Long propertyUid;

  private String other;

  @Getter
  @NoArgsConstructor
  @Setter
  public static class DocumentDTO {

    private String fileName;
    private String fileUrl;

    public DocumentDTO(String fileName, String fileUrl) {
      this.fileName = fileName;
      this.fileUrl = fileUrl;
    }
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ContractCustomerInfo {

    private String name;
    private Long uid;
    private String phoneNo;
  }

  public static ContractResponseDTO of(Contract contract, List<CustomerContract> customerContracts,
      List<DocumentDTO> documents) {

    List<ContractCustomerInfo> lessorOrSellerInfo = customerContracts.stream()
        .filter(cc -> cc.getRole() == ContractCustomerRole.LESSOR_OR_SELLER)
        .map(cc -> new ContractCustomerInfo(
            cc.getCustomer().getName(),
            cc.getCustomer().getUid(),
            cc.getCustomer().getPhoneNo()
        ))
        .toList();

    List<ContractCustomerInfo> lesseeOrBuyerInfo = customerContracts.stream()
        .filter(cc -> cc.getRole() == ContractCustomerRole.LESSEE_OR_BUYER)
        .map(cc -> new ContractCustomerInfo(
            cc.getCustomer().getName(),
            cc.getCustomer().getUid(),
            cc.getCustomer().getPhoneNo()
        ))
        .toList();

    return ContractResponseDTO.builder()
        .uid(contract.getUid())
        .category(contract.getCategory() != null ? String.valueOf(contract.getCategory()) : null)
        .price(contract.getPrice())
        .deposit(contract.getDeposit())
        .monthlyRent(contract.getMonthlyRent())
        .contractStartDate(contract.getContractStartDate())
        .contractEndDate(contract.getContractEndDate())
        .expectedContractEndDate(contract.getExpectedContractEndDate())
        .contractDate(contract.getContractDate())
        .status(contract.getStatus().name())
        .lessorOrSellerInfo(lessorOrSellerInfo)
        .lesseeOrBuyerInfo(lesseeOrBuyerInfo)
        .documents(documents)
        .propertyAddress(contract.getAgentProperty().getAddress())
        .propertyUid(contract.getAgentProperty().getUid())
        .other(contract.getOther())
        .build();
  }
}