package com.zipline.service.statics;

import com.zipline.entity.enums.ContractStatus;
import com.zipline.repository.contract.ContractRepository;
import com.zipline.repository.survey.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticsServiceImpl implements StaticsService {

  private static final int RANGE_FOR_RECENT_DATE = 30;

  private final ContractRepository contractRepository;
  private final SurveyRepository surveyRepository;

  @Override
  public int getRecentContractCount(Long userId) {
    LocalDateTime oneMonthAgo = LocalDateTime.now().minus(RANGE_FOR_RECENT_DATE, ChronoUnit.DAYS);
    return contractRepository.countByUserUidAndCreatedAtAfter(userId, oneMonthAgo);
  }

  @Override
  public int getOngoingContractCount(Long userId) {
    List<ContractStatus> ongoingStatuses = Arrays.asList(
        ContractStatus.LISTED,
        ContractStatus.NEGOTIATING,
        ContractStatus.INTENT_SIGNED,
        ContractStatus.CONTRACTED,
        ContractStatus.IN_PROGRESS);
    return contractRepository.countByUserUidAndStatusIn(userId, ongoingStatuses);
  }

  @Override
  public int getCompletedContractCount(Long userId) {
    List<ContractStatus> completedStatuses = Arrays.asList(
        ContractStatus.PAID_COMPLETE,
        ContractStatus.REGISTERED,
        ContractStatus.MOVED_IN,
        ContractStatus.TERMINATED);
    return contractRepository.countByUserUidAndStatusIn(userId, completedStatuses);
  }

  @Override
  public int getRecentCustomerCount(Long userId) {
    LocalDateTime oneMonthAgo = LocalDateTime.now().minus(RANGE_FOR_RECENT_DATE, ChronoUnit.DAYS);
    return surveyRepository.countByUserUidAndCreatedAtAfter(userId, oneMonthAgo);
  }
}
