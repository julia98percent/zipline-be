package com.zipline.repository.contract;

import static com.zipline.entity.contract.QContract.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipline.entity.contract.Contract;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.global.exception.contract.ContractException;
import com.zipline.global.exception.contract.errorcode.ContractErrorCode;
import com.zipline.global.request.ContractFilterRequestDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ContractQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<Contract> findFilteredContracts(Long userUid, ContractFilterRequestDTO filter, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(contract.user.uid.eq(userUid))
			.and(contract.deletedAt.isNull());

		if (filter.getPeriod() != null && !filter.getPeriod().equalsIgnoreCase("ALL")) {
			LocalDate today = LocalDate.now();
			LocalDate targetDate = switch (filter.getPeriod()) {
				case "6개월 이내" -> today.plus(6, ChronoUnit.MONTHS);
				case "3개월 이내" -> today.plus(3, ChronoUnit.MONTHS);
				case "1개월 이내" -> today.plus(1, ChronoUnit.MONTHS);
				default -> null;
			};
			if (targetDate != null) {
				builder.and(contract.contractEndDate.loe(targetDate));
			}
		}

		if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
			try {
				builder.and(contract.status.eq(ContractStatus.valueOf(filter.getStatus())));
			} catch (IllegalArgumentException e) {
				throw new ContractException(ContractErrorCode.CONTRACT_STATUS_NOT_FOUND);
			}
		}

		List<Contract> result = queryFactory
			.selectFrom(contract)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(contract.uid.desc())
			.fetch();

		return PageableExecutionUtils.getPage(result, pageable, () -> queryFactory
			.select(contract.count())
			.from(contract)
			.where(builder)
			.fetchOne());
	}
}
