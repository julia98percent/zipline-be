package com.zipline.service.contract;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.dto.contract.ContractRequestDTO;
import com.zipline.dto.contract.ContractResponseDTO;
import com.zipline.entity.Customer;
import com.zipline.entity.User;
import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractDocument;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.exception.custom.customer.CustomerNotFoundException;
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

	@Transactional
	public ContractResponseDTO registerContract(ContractRequestDTO contractRequestDTO, Long userUid) {
		User savedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Customer customer = customerRepository.findById(contractRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Contract contract = contractRequestDTO.toEntity(false, LocalDateTime.now(), null, null);
		Contract save = contractRepository.save(contract);

		CustomerContract customerContract = CustomerContract.builder()
			.customer(customer)
			.contract(contract)
			.build();
		customerContractRepository.save(customerContract);

		List<String> savedUrls = new ArrayList<>();
		if (contractRequestDTO.getDocumentUrls() != null) {
			for (String url : contractRequestDTO.getDocumentUrls()) {
				ContractDocument doc = ContractDocument.builder()
					.contract(contract)
					.documentUrl(url)
					.build();
				contractDocumentRepository.save(doc);
				savedUrls.add(url);
			}
		}
		return ContractResponseDTO.of(contract, customer.getUid(), savedUrls);
	}

}
