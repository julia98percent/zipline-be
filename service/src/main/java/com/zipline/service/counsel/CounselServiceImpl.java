package com.zipline.service.counsel;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.counsel.Counsel;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.CounselType;
import com.zipline.entity.user.User;
import com.zipline.global.exception.agentProperty.PropertyException;
import com.zipline.global.exception.agentProperty.errorcode.PropertyErrorCode;
import com.zipline.global.exception.counsel.CounselException;
import com.zipline.global.exception.counsel.errorcode.CounselErrorCode;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.request.CounselFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.repository.agentProperty.AgentPropertyRepository;
import com.zipline.repository.counsel.CounselRepository;
import com.zipline.repository.customer.CustomerRepository;
import com.zipline.repository.region.RegionRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.counsel.dto.request.CounselCreateRequestDTO;
import com.zipline.service.counsel.dto.request.CounselModifyRequestDTO;
import com.zipline.service.counsel.dto.response.CounselHistoryResponseDTO;
import com.zipline.service.counsel.dto.response.CounselListResponseDTO;
import com.zipline.service.counsel.dto.response.CounselPageResponseDTO;
import com.zipline.service.counsel.dto.response.CounselResponseDTO;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class CounselServiceImpl implements CounselService {

  private final CounselRepository counselRepository;
  private final CustomerRepository customerRepository;
  private final UserRepository userRepository;
  private final AgentPropertyRepository agentPropertyRepository;
  private final RegionRepository regionRepository;

  @Transactional
  public Map<String, Long> createCounsel(Long customerUid, CounselCreateRequestDTO requestDTO,
      Long userUid) {
    Customer savedCustomer = customerRepository.findByUidAndUserUidAndDeletedAtIsNull(customerUid,
            userUid)
        .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
    User savedUser = userRepository.findById(userUid)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    AgentProperty savedProperty = null;
    if (requestDTO.getPropertyUid() != null) {
      savedProperty = agentPropertyRepository
          .findByUidAndUserUidAndDeletedAtIsNull(requestDTO.getPropertyUid(), userUid)
          .orElseThrow(() -> new PropertyException(PropertyErrorCode.PROPERTY_NOT_FOUND));
    }

    boolean completed = requestDTO.getDueDate() == null;

    Counsel counsel = new Counsel(requestDTO.getTitle(), requestDTO.getCounselDate(),
        CounselType.from(requestDTO.getType()),
        requestDTO.getDueDate(), savedUser, savedCustomer, savedProperty,
        completed, requestDTO.getContent());

    Counsel savedCounsel = counselRepository.save(counsel);
    return Collections.singletonMap("counselUid", savedCounsel.getUid());
  }

  @Transactional(readOnly = true)
  public CounselResponseDTO getCounsel(Long counselUid, Long userUid) {
    Counsel savedCounsel = counselRepository.findByUidAndUserUidAndDeletedAtIsNull(counselUid,
            userUid)
        .orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));

    String preferredRegion = null;
    if (StringUtils.hasText(savedCounsel.getCustomer().getLegalDistrictCode())) {
      preferredRegion = regionRepository.findWithParentsByDistrictCode(
          Long.valueOf(savedCounsel.getCustomer().getLegalDistrictCode()));
    }
    return new CounselResponseDTO(savedCounsel, preferredRegion);
  }

  @Transactional
  public Map<String, Long> modifyCounsel(Long counselUid, CounselModifyRequestDTO requestDTO,
      Long userUid) {
    Counsel savedCounsel = counselRepository.findByUidAndUserUidAndDeletedAtIsNull(counselUid,
            userUid)
        .orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));

    savedCounsel.update(requestDTO.getTitle(), requestDTO.getCounselDate(),
        CounselType.getCounselType(requestDTO.getType()),
        requestDTO.getDueDate(), requestDTO.isCompleted(), requestDTO.getContent());

    return Collections.singletonMap("counselUid", savedCounsel.getUid());
  }

  @Transactional
  public void deleteCounsel(Long counselUid, Long userUid) {
    Counsel savedCounsel = counselRepository.findByUidAndUserUidAndDeletedAtIsNull(counselUid,
            userUid)
        .orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));

    LocalDateTime deletedAt = LocalDateTime.now();
    savedCounsel.delete(deletedAt);
  }

  @Transactional(readOnly = true)
  public CounselPageResponseDTO getCounsels(PageRequestDTO pageRequestDTO,
      CounselFilterRequestDTO filterRequestDTO, Long userUid) {
    Page<Counsel> savedCounsels = counselRepository.findByUserUidAndDeletedAtIsNullWithFiltering(
        userUid,
        pageRequestDTO.toPageable(), filterRequestDTO);
    List<CounselListResponseDTO> data = savedCounsels.getContent().stream().map(
        CounselListResponseDTO::createWithCustomerName).toList();
    CounselPageResponseDTO result = new CounselPageResponseDTO(savedCounsels, data);
    return result;
  }

  @Override
  public CounselPageResponseDTO getDashBoardCounsels(PageRequestDTO pageRequestDTO, String sortType,
      Long userUid) {
    Page<Counsel> savedCounsels = counselRepository.findByUserUidAndDeletedAtIsNullWithSortType(
        userUid, pageRequestDTO.toPageable(), sortType);

    List<CounselListResponseDTO> data = savedCounsels.getContent()
        .stream()
        .map(CounselListResponseDTO::createWithCustomerName)
        .toList();
    CounselPageResponseDTO result = new CounselPageResponseDTO(savedCounsels, data);
    return result;
  }

  @Override
  public CounselPageResponseDTO getPropertyCounselHistories(PageRequestDTO pageRequestDTO,
      Long propertyUid,
      Long userUid) {
    Page<Counsel> savedCounsels = counselRepository.findByUserUidAndAgentPropertyUidAndDeletedAtIsNull(
        userUid, propertyUid, pageRequestDTO.toPageable());

    List<CounselHistoryResponseDTO> data = savedCounsels.stream()
        .map(CounselHistoryResponseDTO::new).toList();
    return new CounselPageResponseDTO(savedCounsels, data);
  }
}