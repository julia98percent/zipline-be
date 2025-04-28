package com.zipline.service.contract;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.zipline.global.request.ContractFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.contract.dto.request.ContractRequestDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.contract.dto.response.ContractResponseDTO;

public interface ContractService {

	ContractResponseDTO getContract(Long contractUid, Long userUid);

	ContractResponseDTO registerContract(ContractRequestDTO contractRequestDTO, List<MultipartFile> files,
		Long userUid);

	void deleteContract(Long contractUid, Long userUid);

	ContractResponseDTO modifyContract(ContractRequestDTO contractRequestDTO, Long contractUid,
		List<MultipartFile> files, Long userUid);

	ContractListResponseDTO getContractList(PageRequestDTO pageRequestDTO, Long userUid,
		ContractFilterRequestDTO filter);
}
