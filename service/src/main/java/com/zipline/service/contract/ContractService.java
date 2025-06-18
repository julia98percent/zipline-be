package com.zipline.service.contract;

import com.zipline.global.request.ContractFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.contract.dto.request.ContractRequestDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.contract.dto.response.ContractPropertyHistoryResponseDTO;
import com.zipline.service.contract.dto.response.ContractPropertyResponseDTO;
import com.zipline.service.contract.dto.response.ContractResponseDTO;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ContractService {

  @Timed
  ContractResponseDTO getContract(Long contractUid, Long userUid);

  @Timed
  ContractResponseDTO registerContract(ContractRequestDTO contractRequestDTO,
      List<MultipartFile> files,
      Long userUid);

  @Timed
  void deleteContract(Long contractUid, Long userUid);

  @Timed
  ContractResponseDTO modifyContract(ContractRequestDTO contractRequestDTO, Long contractUid,
      List<MultipartFile> files, List<ContractResponseDTO.DocumentDTO> existingDocs, Long userUid);

  @Timed
  ContractListResponseDTO getContractList(PageRequestDTO pageRequestDTO, Long userUid,
      ContractFilterRequestDTO filter);

  @Timed
  List<ContractPropertyHistoryResponseDTO> getPropertyContractHistories(Long propertyUid,
      Long userUid);

  @Timed
  ContractPropertyResponseDTO getPropertyContract(Long propertyUid, Long userUid);
}