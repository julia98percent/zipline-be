package com.zipline.service.contract;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.zipline.dto.PageRequestDTO;
import com.zipline.dto.contract.ContractListResponseDTO;
import com.zipline.dto.contract.ContractRequestDTO;
import com.zipline.dto.contract.ContractResponseDTO;

public interface ContractService {

	ContractResponseDTO getContract(Long contratUid, Long userUid);

	ContractResponseDTO registerContract(ContractRequestDTO contractRequestDTO, List<MultipartFile> files,
		Long userUid);

	ContractListResponseDTO getContractList(PageRequestDTO pageRequestDTO, Long userUid);
}
