package com.zipline.service.contract;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractDocument;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.entity.user.User;
import com.zipline.global.config.S3Folder;
import com.zipline.global.exception.contract.ContractException;
import com.zipline.global.exception.contract.errorcode.ContractErrorCode;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
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
	private final ContractHistoryService contractHistoryService;

	@Transactional(readOnly = true)
	public ContractResponseDTO getContract(Long contractUid, Long userUid) {
		Contract contract = contractRepository.findByUidAndUserUidAndDeletedAtIsNull(contractUid, userUid)
			.orElseThrow(() -> new ContractException(ContractErrorCode.CONTRACT_NOT_FOUND));

		List<CustomerContract> customerContracts = customerContractRepository.findAllByContractUid(contractUid);

		if (customerContracts.isEmpty()) {
			throw new ContractException(ContractErrorCode.CONTRACT_CUSTOMER_NOT_FOUND);
		}

		String lessorOrSellerName = customerContracts.get(0).getCustomer().getName();

		String lesseeOrBuyerName = null;
		if (customerContracts.size() > 1) {
			lesseeOrBuyerName = customerContracts.get(1).getCustomer().getName();
		}

		List<ContractDocument> documents = contractDocumentRepository.findAllByContractUid(contractUid);
		List<ContractResponseDTO.DocumentDTO> documentDTO = documents.stream()
			.map(doc -> new ContractResponseDTO.DocumentDTO(
				doc.getDocumentName(),
				doc.getDocumentUrl()
			))
			.toList();

		return ContractResponseDTO.of(contract, lessorOrSellerName, lesseeOrBuyerName, documentDTO);
	}

	@Transactional
	public ContractResponseDTO registerContract(ContractRequestDTO contractRequestDTO, List<MultipartFile> files,
		Long userUid) {
		User savedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Customer lessorOrSeller = customerRepository.findById(contractRequestDTO.getLessorOrSellerUid())
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		ContractStatus status = validateAndParseStatus(contractRequestDTO.getStatus());
		contractRequestDTO.validateDateOrder();
		Contract contract = contractRequestDTO.toEntity(savedUser, status);
		Contract savedContract = contractRepository.save(contract);

		customerContractRepository.save(CustomerContract.builder()
			.customer(lessorOrSeller)
			.contract(savedContract)
			.build());

		Customer lesseeOrBuyer = null;
		if (contractRequestDTO.getLesseeOrBuyerUid() != null) {
			lesseeOrBuyer = customerRepository.findById(contractRequestDTO.getLesseeOrBuyerUid())
				.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

			customerContractRepository.save(
				CustomerContract.builder()
					.customer(lesseeOrBuyer)
					.contract(savedContract)
					.build()
			);
		}

		List<ContractResponseDTO.DocumentDTO> documentDTO = List.of();
		if (files != null && !files.isEmpty()) {
			List<String> uploadUrls = s3FileUploader.uploadContractFiles(files, S3Folder.CONTRACTS);

			List<ContractDocument> documents = createContractDocuments(savedContract, files, uploadUrls);
			contractDocumentRepository.saveAll(documents);
			documentDTO = documents.stream()
				.map(doc -> new ContractResponseDTO.DocumentDTO(doc.getDocumentName(), doc.getDocumentUrl()))
				.toList();
		}

		String lesseeOrBuyerName = (contractRequestDTO.getLesseeOrBuyerUid() != null) ?
			customerRepository.findById(contractRequestDTO.getLesseeOrBuyerUid()).get().getName() : null;

		return ContractResponseDTO.of(savedContract, lessorOrSeller.getName(), lesseeOrBuyerName, documentDTO);
	}

	@Transactional(readOnly = true)
	public ContractListResponseDTO getContractList(PageRequestDTO pageRequestDTO, Long userUid) {
		Page<Contract> contractPage = contractRepository.findByUserUidAndDeletedAtIsNull(userUid,
			pageRequestDTO.toPageable());
		List<Contract> contracts = contractPage.getContent();

		List<Long> contractIds = contracts.stream()
			.map(Contract::getUid)
			.toList();

		List<CustomerContract> customerContracts = customerContractRepository.findInContractUids(contractIds);

		Map<Long, List<CustomerContract>> contractIdToCustomerContracts = customerContracts.stream()
			.collect(Collectors.groupingBy(cc -> cc.getContract().getUid()));

		List<ContractListDTO> contractListDTO = contracts.stream()
			.map(contract -> {
				List<CustomerContract> relatedCustomers = contractIdToCustomerContracts.getOrDefault(contract.getUid(),
					List.of());
				return new ContractListDTO(contract, relatedCustomers);
			})
			.toList();

		return new ContractListResponseDTO(contractListDTO, contractPage);
	}

	@Transactional
	public ContractResponseDTO modifyContract(ContractRequestDTO contractRequestDTO, Long contractUid,
		List<MultipartFile> files, Long userUid) {
		Contract contract = contractRepository.findByUidAndUserUidAndDeletedAtIsNull(contractUid, userUid)
			.orElseThrow(() -> new ContractException(ContractErrorCode.CONTRACT_NOT_FOUND));

		contractRequestDTO.validateDateOrder();
		ContractStatus prevStatus = contract.getStatus();
		ContractStatus newStatus = validateAndParseStatus(contractRequestDTO.getStatus());
		contract.modifyContract(
			contractRequestDTO.getCategory(),
			contractRequestDTO.getContractDate(),
			contractRequestDTO.getContractStartDate(),
			contractRequestDTO.getContractEndDate(),
			contractRequestDTO.getExpectedContractEndDate(),
			newStatus
		);

		contractHistoryService.addContractHistory(contract, prevStatus, newStatus);

		List<CustomerContract> customerContracts = customerContractRepository.findAllByContractUid(contractUid);
		if (customerContracts.isEmpty()) {
			throw new ContractException(ContractErrorCode.CONTRACT_CUSTOMER_NOT_FOUND);
		}

		updateCustomerContracts(contract, customerContracts, contractRequestDTO);

		List<ContractResponseDTO.DocumentDTO> documentDTO = updateContractDocuments(contract, contractUid, files);

		String lesseeOrBuyerName = contractRequestDTO.getLesseeOrBuyerUid() != null
			? customerRepository.findById(contractRequestDTO.getLesseeOrBuyerUid()).get().getName()
			: null;

		return ContractResponseDTO.of(contract, customerContracts.get(0).getCustomer().getName(), lesseeOrBuyerName,
			documentDTO);
	}

	private ContractStatus validateAndParseStatus(String status) {
		try {
			return ContractStatus.valueOf(status);
		} catch (IllegalArgumentException e) {
			throw new ContractException(ContractErrorCode.CONTRACT_STATUS_NOT_FOUND);
		}
	}

	private List<ContractDocument> createContractDocuments(Contract contract, List<MultipartFile> files,
		List<String> urls) {
		return IntStream.range(0, files.size())
			.mapToObj(i -> ContractDocument.builder()
				.contract(contract)
				.documentName(files.get(i).getOriginalFilename())
				.documentUrl(urls.get(i))
				.build())
			.toList();
	}

	private void updateCustomerContracts(Contract contract, List<CustomerContract> customerContracts,
		ContractRequestDTO dto) {
		Customer lessorOrSeller = customerRepository.findById(dto.getLessorOrSellerUid())
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		customerContracts.get(0).updateCustomerContract(lessorOrSeller);

		if (dto.getLesseeOrBuyerUid() != null) {
			Customer lesseeOrBuyer = customerRepository.findById(dto.getLesseeOrBuyerUid())
				.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

			if (customerContracts.size() > 1) {
				customerContracts.get(1).updateCustomerContract(lesseeOrBuyer);
			} else {
				customerContractRepository.save(CustomerContract.builder()
					.customer(lesseeOrBuyer)
					.contract(contract)
					.build());
			}
		} else if (customerContracts.size() > 1) {
			customerContractRepository.delete(customerContracts.get(1));
		}
	}

	private List<ContractResponseDTO.DocumentDTO> updateContractDocuments(Contract contract, Long contractUid,
		List<MultipartFile> files) {
		List<ContractDocument> documents;

		if (files != null && !files.isEmpty()) {
			contractDocumentRepository.deleteAll(contractDocumentRepository.findAllByContractUid(contractUid));

			List<String> uploadUrls = s3FileUploader.uploadContractFiles(files, S3Folder.CONTRACTS);
			documents = createContractDocuments(contract, files, uploadUrls);
			contractDocumentRepository.saveAll(documents);
		} else {
			documents = contractDocumentRepository.findAllByContractUid(contractUid);
		}

		return documents.stream()
			.map(doc -> new ContractResponseDTO.DocumentDTO(doc.getDocumentName(), doc.getDocumentUrl()))
			.toList();
	}
}
