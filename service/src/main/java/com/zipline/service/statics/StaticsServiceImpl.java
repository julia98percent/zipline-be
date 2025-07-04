package com.zipline.service.statics;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zipline.entity.enums.ContractStatus;
import com.zipline.repository.contract.ContractRepository;
import com.zipline.repository.survey.SurveyAnswerRepository;
import com.zipline.repository.survey.SurveyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticsServiceImpl implements StaticsService {

	private static final int RANGE_FOR_RECENT_DATE = 30;

	private final ContractRepository contractRepository;
	private final SurveyRepository surveyRepository;
	private final SurveyAnswerRepository surveyAnswerRepository;

	@Override
	public int getRecentContractCount(Long userId) {
		LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(RANGE_FOR_RECENT_DATE);
		return contractRepository.countByUserUidAndCreatedAtAfterAndDeletedAtIsNull(userId, oneMonthAgo);
	}

	@Override
	public int getOngoingContractCount(Long userId) {
		List<ContractStatus> ongoingStatuses = Arrays.asList(
				ContractStatus.NEGOTIATING,
				ContractStatus.INTENT_SIGNED,
				ContractStatus.CONTRACTED,
				ContractStatus.IN_PROGRESS,
				ContractStatus.PAID_COMPLETE,
				ContractStatus.REGISTERED,
				ContractStatus.MOVED_IN);

		return contractRepository.countByUserUidAndStatusInAndDeletedAtIsNull(userId, ongoingStatuses);
	}

	@Override
	public int getCompletedContractCount(Long userId) {
		List<ContractStatus> completedStatuses = Arrays.asList(
			ContractStatus.CANCELLED,
			ContractStatus.TERMINATED);
		return contractRepository.countByUserUidAndStatusInAndDeletedAtIsNull(userId, completedStatuses);
	}

	@Override
	public int getRecentCustomerCount(Long userId) {
		LocalDateTime oneMonthAgo = LocalDateTime.now().minus(RANGE_FOR_RECENT_DATE, ChronoUnit.DAYS);
		return surveyAnswerRepository.countRecentResponsesByUser(userId, oneMonthAgo);
	}

}
