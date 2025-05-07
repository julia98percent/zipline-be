package com.zipline.service.contract;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractDocument;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.ContractCustomerRole;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.entity.enums.PropertyType;
import com.zipline.entity.user.User;
import com.zipline.global.config.S3Folder;
import com.zipline.global.exception.agentProperty.PropertyException;
import com.zipline.global.exception.agentProperty.errorcode.PropertyErrorCode;
import com.zipline.global.exception.contract.ContractException;
import com.zipline.global.exception.contract.errorcode.ContractErrorCode;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.request.ContractFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.util.S3FileUploader;
import com.zipline.repository.agentProperty.AgentPropertyRepository;
import com.zipline.repository.contract.ContractDocumentRepository;
import com.zipline.repository.contract.ContractQueryRepository;
import com.zipline.repository.contract.ContractRepository;
import com.zipline.repository.contract.CustomerContractRepository;
import com.zipline.repository.customer.CustomerRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.contract.dto.request.ContractRequestDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
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
	private final ContractQueryRepository contractQueryRepository;
	private final AgentPropertyRepository agentPropertyRepository;
	private final S3FileUploader s3FileUploader;
	private final ContractHistoryService contractHistoryService;

	@Transactional(readOnly = true)
	public ContractResponseDTO getContract(Long contractUid, Long userUid) {
		Contract contract = contractRepository.findByUidAndUserUidAndDeletedAtIsNull(contractUid, userUid)
			.orElseThrow(() -> new ContractException(ContractErrorCode.CONTRACT_NOT_FOUND));

		List<CustomerContract> customerContracts = customerContractRepository.findAllByContractUid(contractUid);

		String lessorOrSellerName = customerContracts.stream()
			.filter(cc -> cc.getRole() == ContractCustomerRole.LESSOR_OR_SELLER)
			.map(cc -> cc.getCustomer().getName())
			.findFirst()
			.orElse(null);

		String lesseeOrBuyerName = customerContracts.stream()
			.filter(cc -> cc.getRole() == ContractCustomerRole.LESSEE_OR_BUYER)
			.map(cc -> cc.getCustomer().getName())
			.findFirst()
			.orElse(null);

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
		PropertyType category = null;
		if (contractRequestDTO.getCategory() != null) {
			category = validateAndParseCategory(contractRequestDTO.getCategory());
		}
		contractRequestDTO.validateDateOrder();
		contractRequestDTO.validateProperty();
		contractRequestDTO.validateDistinctParties();
		AgentProperty agentProperty = agentPropertyRepository.findByUidAndUserUidAndDeletedAtIsNull(
				contractRequestDTO.getPropertyUid(), userUid)
			.orElseThrow(() -> new PropertyException(PropertyErrorCode.PROPERTY_NOT_FOUND));

		Contract contract = contractRequestDTO.toEntity(savedUser, agentProperty, status, category);
		Contract savedContract = contractRepository.save(contract);

		customerContractRepository.save(CustomerContract.builder()
			.customer(lessorOrSeller)
			.contract(savedContract)
			.role(ContractCustomerRole.LESSOR_OR_SELLER)
			.build());

		Customer lesseeOrBuyer = null;
		if (contractRequestDTO.getLesseeOrBuyerUid() != null) {
			lesseeOrBuyer = customerRepository.findById(contractRequestDTO.getLesseeOrBuyerUid())
				.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

			customerContractRepository.save(
				CustomerContract.builder()
					.customer(lesseeOrBuyer)
					.contract(savedContract)
					.role(ContractCustomerRole.LESSEE_OR_BUYER)
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

	@Transactional
	public void deleteContract(Long contractUid, Long userUid) {
		Contract contract = contractRepository.findByUidAndUserUidAndDeletedAtIsNull(contractUid, userUid)
			.orElseThrow(() -> new ContractException(ContractErrorCode.CONTRACT_NOT_FOUND));

		contract.delete(LocalDateTime.now());

		List<CustomerContract> customerContracts = customerContractRepository.findAllByContractUid(contractUid);
		customerContracts.forEach(cc -> cc.delete(LocalDateTime.now()));

		List<ContractDocument> documents = contractDocumentRepository.findAllByContractUid(contractUid);
		documents.forEach(doc -> doc.delete(LocalDateTime.now()));
	}

	@Transactional(readOnly = true)
	public ContractListResponseDTO getContractList(PageRequestDTO pageRequestDTO, Long userUid,
		ContractFilterRequestDTO filter) {

		Page<Contract> contractPage = contractQueryRepository.findFilteredContracts(
			userUid, filter, pageRequestDTO.toPageable()
		);

		List<Contract> contracts = contractPage.getContent();

		List<Long> contractIds = contracts.stream()
			.map(Contract::getUid)
			.toList();

		List<CustomerContract> customerContracts = customerContractRepository.findInContractUids(contractIds);

		Map<Long, List<CustomerContract>> contractIdToCustomerContracts = customerContracts.stream()
			.collect(Collectors.groupingBy(cc -> cc.getContract().getUid()));

		List<ContractListResponseDTO.ContractListDTO> contractListDTO = contracts.stream()
			.map(contract -> {
				List<CustomerContract> relatedCustomers = contractIdToCustomerContracts.getOrDefault(contract.getUid(),
					List.of());
				return new ContractListResponseDTO.ContractListDTO(contract, relatedCustomers);
			})
			.toList();

		return new ContractListResponseDTO(contractListDTO, contractPage);
	}

	@Transactional
	public ContractResponseDTO modifyContract(ContractRequestDTO contractRequestDTO, Long contractUid,
		List<MultipartFile> files, List<ContractResponseDTO.DocumentDTO> existingDocs, Long userUid) {
		Contract contract = contractRepository.findByUidAndUserUidAndDeletedAtIsNull(contractUid, userUid)
			.orElseThrow(() -> new ContractException(ContractErrorCode.CONTRACT_NOT_FOUND));

		contractRequestDTO.validateDateOrder();
		contractRequestDTO.validateProperty();
		contractRequestDTO.validateDistinctParties();
		AgentProperty agentProperty = agentPropertyRepository.findByUidAndUserUidAndDeletedAtIsNull(
				contractRequestDTO.getPropertyUid(), userUid)
			.orElseThrow(() -> new PropertyException(PropertyErrorCode.PROPERTY_NOT_FOUND));

		ContractStatus status = validateAndParseStatus(contractRequestDTO.getStatus());
		PropertyType category = null;
		if (contractRequestDTO.getCategory() != null) {
			category = validateAndParseCategory(String.valueOf(contractRequestDTO.getCategory()));
		}
		ContractStatus prevStatus = contract.getStatus();
		ContractStatus newStatus = validateAndParseStatus(contractRequestDTO.getStatus());
		contract.modifyContract(
			category,
			contractRequestDTO.getDeposit(),
			contractRequestDTO.getMonthlyRent(),
			contractRequestDTO.getPrice(),
			contractRequestDTO.getContractDate(),
			contractRequestDTO.getContractStartDate(),
			contractRequestDTO.getContractEndDate(),
			contractRequestDTO.getExpectedContractEndDate(),
			newStatus,
			agentProperty
		);

		if (!prevStatus.equals(newStatus)) {
			contractHistoryService.addContractHistory(contract, prevStatus, newStatus);
		}

		List<CustomerContract> customerContracts = customerContractRepository.findAllByContractUid(contractUid);
		if (customerContracts.isEmpty()) {
			throw new ContractException(ContractErrorCode.CONTRACT_CUSTOMER_NOT_FOUND);
		}

		updateCustomerContracts(contract, customerContracts, contractRequestDTO);

		List<ContractResponseDTO.DocumentDTO> updatedDocs = updateContractDocuments(contract, contractUid, files,
			existingDocs);

		String lesseeOrBuyerName = contractRequestDTO.getLesseeOrBuyerUid() != null
			? customerRepository.findById(contractRequestDTO.getLesseeOrBuyerUid()).get().getName()
			: null;

		return ContractResponseDTO.of(contract, customerContracts.get(0).getCustomer().getName(), lesseeOrBuyerName,
			updatedDocs);
	}

	private ContractStatus validateAndParseStatus(String status) {
		try {
			return ContractStatus.valueOf(status);
		} catch (IllegalArgumentException e) {
			throw new ContractException(ContractErrorCode.CONTRACT_STATUS_NOT_FOUND);
		}
	}

	private PropertyType validateAndParseCategory(String category) {
		try {
			return PropertyType.valueOf(category);
		} catch (IllegalArgumentException e) {
			throw new ContractException(ContractErrorCode.CONTRACT_CATEGORY_NOT_FOUND);
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
		ContractRequestDTO requestDTO) {

		for (CustomerContract cc : customerContracts) {
			if (cc.getRole() == ContractCustomerRole.LESSOR_OR_SELLER) {
				Customer lessorOrSeller = customerRepository.findById(requestDTO.getLessorOrSellerUid())
					.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
				cc.updateCustomerContract(lessorOrSeller);
			} else if (cc.getRole() == ContractCustomerRole.LESSEE_OR_BUYER) {
				if (requestDTO.getLesseeOrBuyerUid() != null) {
					Customer lesseeOrBuyer = customerRepository.findById(requestDTO.getLesseeOrBuyerUid())
						.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
					cc.updateCustomerContract(lesseeOrBuyer);
				}
			}
		}
	}

	private List<ContractResponseDTO.DocumentDTO> updateContractDocuments(
		Contract contract,
		Long contractUid,
		List<MultipartFile> newFiles,
		List<ContractResponseDTO.DocumentDTO> existingDocs
	) {
		List<ContractDocument> currentDocs = contractDocumentRepository.findAllByContractUid(contractUid);

		Set<String> urlsToKeep = existingDocs != null
			? existingDocs.stream().map(ContractResponseDTO.DocumentDTO::getFileUrl).collect(Collectors.toSet())
			: Collections.emptySet();

		List<ContractDocument> docsToDelete = currentDocs.stream()
			.filter(doc -> !urlsToKeep.contains(doc.getDocumentUrl()))
			.toList();

		contractDocumentRepository.deleteAll(docsToDelete);

		List<ContractDocument> savedNewDocs = new ArrayList<>();
		if (newFiles != null && !newFiles.isEmpty()) {
			List<String> uploadUrls = s3FileUploader.uploadContractFiles(newFiles, S3Folder.CONTRACTS);
			savedNewDocs = createContractDocuments(contract, newFiles, uploadUrls);
			contractDocumentRepository.saveAll(savedNewDocs);
		}
		List<ContractDocument> remainingDocs = currentDocs.stream()
			.filter(doc -> urlsToKeep.contains(doc.getDocumentUrl()))
			.toList();

		List<ContractDocument> allDocs = new ArrayList<>();
		allDocs.addAll(remainingDocs);
		allDocs.addAll(savedNewDocs);

		return allDocs.stream()
			.map(doc -> new ContractResponseDTO.DocumentDTO(doc.getDocumentName(), doc.getDocumentUrl()))
			.toList();
	}
}
