package com.zipline.repository.contract;

import static com.zipline.entity.contract.QContract.*;
import static com.zipline.entity.contract.QCustomerContract.*;
import static com.zipline.entity.customer.QCustomer.*;

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

		if (
			filter.getCategory() != null && !filter.getCategory().isBlank() ||
				filter.getCustomerName() != null && !filter.getCustomerName().isBlank() ||
				filter.getAddress() != null && !filter.getAddress().isBlank()
		) {
			String keyword = null;

			if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
				keyword = filter.getCategory();
			} else if (filter.getCustomerName() != null && !filter.getCustomerName().isBlank()) {
				keyword = filter.getCustomerName();
			} else if (filter.getAddress() != null && !filter.getAddress().isBlank()) {
				keyword = filter.getAddress();
			}

			if (keyword != null) {
				List<Long> matchingContractUids = queryFactory
					.select(customerContract.contract.uid)
					.from(customerContract)
					.join(customerContract.customer, customer)
					.where(customer.name.containsIgnoreCase(keyword))
					.fetch();

				BooleanBuilder keywordBuilder = new BooleanBuilder();
				keywordBuilder.or(contract.agentProperty.address.containsIgnoreCase(keyword));
				keywordBuilder.or(contract.category.stringValue().containsIgnoreCase(keyword));
				keywordBuilder.or(contract.uid.in(matchingContractUids));

				builder.and(keywordBuilder);
			}
		}

		var query = queryFactory
			.selectFrom(contract)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		if (filter.getSort() != null && !filter.getSort().isBlank()) {
			switch (filter.getSort()) {
				case "LATEST" -> query.orderBy(contract.uid.desc());
				case "OLDEST" -> query.orderBy(contract.uid.asc());
				case "EXPIRING" -> query.orderBy(contract.contractEndDate.asc());
				default -> query.orderBy(contract.uid.desc());
			}
		} else {
			query.orderBy(contract.uid.desc());
		}

		List<Contract> result = query.fetch();
		
		return PageableExecutionUtils.getPage(result, pageable, () ->
			queryFactory
				.select(contract.count())
				.from(contract)
				.where(builder)
				.fetchOne());

	}
}
