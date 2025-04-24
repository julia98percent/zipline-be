package com.zipline.service.contract;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractDocument;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.user.User;
import com.zipline.global.config.S3Folder;
import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.contract.ContractException;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.contract.errorcode.ContractErrorCode;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.util.S3FileUploader;
import com.zipline.repository.contract.ContractDocumentRepository;
import com.zipline.repository.contract.ContractRepository;
import com.zipline.repository.contract.CustomerContractRepository;
import com.zipline.repository.customer.CustomerRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.contract.dto.request.ContractRequestDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO.ContractListDTO;
import com.zipline.service.contract.dto.response.ContractResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final ContractRepository contractRepository;
	private final ContractDocumentRepository contractDocumentRepository;
	private final CustomerContractRepository customerContractRepository;
	private final S3FileUploader s3FileUploader;

	@Transactional(readOnly = true)
	public ContractResponseDTO getContract(Long contractUid, Long userUid) {
		Contract contract = contractRepository.findByUidAndDeletedAtIsNull(contractUid)
			.orElseThrow(() -> new ContractException(ContractErrorCode.CONTRACT_NOT_FOUND));

		CustomerContract customerContract = customerContractRepository.findByContract(contract)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		if (!contract.getUser().getUid().equals(userUid))
			throw new AuthException(AuthErrorCode.PERMISSION_DENIED);

		List<ContractDocument> documents = contractDocumentRepository.findAllByContract(contract);
		List<String> documentUrls = documents.stream()
			.map(ContractDocument::getDocumentUrl)
			.toList();

		return ContractResponseDTO.of(contract, customerContract.getCustomer().getName(), documentUrls);
	}

	@Transactional
	public ContractResponseDTO registerContract(ContractRequestDTO contractRequestDTO, List<MultipartFile> files,
		Long userUid) {
		User savedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Customer customer = customerRepository.findById(contractRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		Contract contract = contractRequestDTO.toEntity(savedUser);
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

		return ContractResponseDTO.of(savedContract, customer.getName(), uploadUrls);
	}

	@Transactional(readOnly = true)
	public ContractListResponseDTO getContractList(PageRequestDTO pageRequestDTO, Long userUid) {
		Page<Contract> contractPage = contractRepository.findByUserUidAndDeletedAtIsNull(userUid,
			pageRequestDTO.toPageable());
		List<Contract> content = contractPage.getContent();
		List<Long> contractIds = content.stream().map(c -> c.getUid()).collect(toList());
		List<CustomerContract> inContractUids = customerContractRepository.findInContractUids(contractIds);
		List<ContractListDTO> contractList = inContractUids.stream()
			.map(cc -> new ContractListDTO(cc))
			.toList();
		return new ContractListResponseDTO(contractList, contractPage);
	}
}
