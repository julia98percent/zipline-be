package com.zipline.service.contract;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.dto.contract.ContractRequestDTO;
import com.zipline.dto.contract.ContractResponseDTO;
import com.zipline.entity.Customer;
import com.zipline.entity.User;
import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractDocument;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.global.config.S3Folder;
import com.zipline.global.exception.custom.PermissionDeniedException;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.exception.custom.contract.ContractNotFoundException;
import com.zipline.global.exception.custom.customer.CustomerNotFoundException;
import com.zipline.global.util.S3FileUploader;
import com.zipline.repository.CustomerRepository;
import com.zipline.repository.UserRepository;
import com.zipline.repository.contract.ContractDocumentRepository;
import com.zipline.repository.contract.ContractRepository;
import com.zipline.repository.contract.CustomerContractRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContractService {

	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final ContractRepository contractRepository;
	private final ContractDocumentRepository contractDocumentRepository;
	private final CustomerContractRepository customerContractRepository;
	private final S3FileUploader s3FileUploader;

	@Transactional(readOnly = true)
	public ContractResponseDTO getContract(Long contratUid, Long userUid) {
		Contract contract = contractRepository.findByUidAndIsDeletedFalse(contratUid)
			.orElseThrow(() -> new ContractNotFoundException("해당 계약을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		CustomerContract customerContract = customerContractRepository.findByContract(contract)
			.orElseThrow(() -> new CustomerNotFoundException("해당 계약에 연결된 고객 정보가 없습니다.", HttpStatus.BAD_REQUEST));

		if (!contract.getUser().getUid().equals(userUid))
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);

		List<ContractDocument> documents = contractDocumentRepository.findAllByContract(contract);
		List<String> documentUrls = documents.stream()
			.map(ContractDocument::getDocumentUrl)
			.toList();

		return ContractResponseDTO.of(contract, customerContract.getUid(), documentUrls);
	}

	@Transactional
	public ContractResponseDTO registerContract(ContractRequestDTO contractRequestDTO, List<MultipartFile> files,
		Long userUid) {
		User savedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Customer customer = customerRepository.findById(contractRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Contract contract = contractRequestDTO.toEntity(savedUser, false, LocalDateTime.now(), null, null);
		Contract savedContract = contractRepository.save(contract);

		CustomerContract customerContract = CustomerContract.builder()
			.customer(customer)
			.contract(contract)
			.build();
		customerContractRepository.save(customerContract);

		List<String> uploadUrls = s3FileUploader.uploadContractFiles(files, S3Folder.CONTRACTS);

		List<ContractDocument> documents = uploadUrls.stream()
			.map(url -> ContractDocument.builder()
				.contract(savedContract)
				.documentUrl(url)
				.build())
			.toList();
		contractDocumentRepository.saveAll(documents);

		return ContractResponseDTO.of(savedContract, customer.getUid(), uploadUrls);
	}

}
