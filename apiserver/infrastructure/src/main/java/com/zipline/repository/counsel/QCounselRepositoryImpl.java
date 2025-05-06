package com.zipline.repository.counsel;

import static com.zipline.entity.counsel.QCounsel.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipline.entity.counsel.Counsel;
import com.zipline.entity.enums.CounselType;
import com.zipline.global.request.CounselFilterRequestDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class QCounselRepositoryImpl implements QCounselRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Counsel> findByUserUidAndDeletedAtIsNullWithFiltering(Long userUid, Pageable pageable,
		CounselFilterRequestDTO filterRequestDTO) {

		List<Counsel> results = queryFactory.select(counsel)
			.from(counsel)
			.where(isNotDeleted(),
				isUserUidEqualTo(userUid),
				searchNameOrPhone(filterRequestDTO.getSearch()),
				betweenCounselDate(filterRequestDTO.getStartDate(), filterRequestDTO.getEndDate()),
				filterByType(filterRequestDTO.getType()),
				filterByCompleted(filterRequestDTO.getCompleted())
			)
			.orderBy(counsel.counselDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(counsel.count())
			.from(counsel)
			.where(isNotDeleted(),
				isUserUidEqualTo(userUid),
				searchNameOrPhone(filterRequestDTO.getSearch()),
				betweenCounselDate(filterRequestDTO.getStartDate(), filterRequestDTO.getEndDate()),
				filterByType(filterRequestDTO.getType()),
				filterByCompleted(filterRequestDTO.getCompleted())
			);

		return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<Counsel> findByUserUidAndDeletedAtIsNullWithSortType(Long userUid, Pageable pageable, String sortType) {
		List<Counsel> results = queryFactory.select(counsel)
			.from(counsel)
			.where(isUserUidEqualTo(userUid),
				isNotDeleted(),
				filterByDueDate(sortType)
			)
			.orderBy(getSortOrder(sortType))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(counsel.count())
			.from(counsel)
			.where(
				isUserUidEqualTo(userUid),
				isNotDeleted(),
				filterByDueDate(sortType)
			);

		return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
	}

	private OrderSpecifier<?>[] getSortOrder(String sortType) {
		if ("DUE_DATE".equalsIgnoreCase(sortType)) {
			return new OrderSpecifier[] {
				counsel.dueDate.asc().nullsLast(),
				counsel.createdAt.desc()
			};
		}
		return new OrderSpecifier[] {
			counsel.counselDate.desc(),
			counsel.createdAt.desc()
		};
	}

	private LocalDate[] getDueDateRange() {
		LocalDate today = LocalDate.now();
		LocalDate limit = today.plusDays(14);
		return new LocalDate[] {today, limit};
	}

	private BooleanExpression filterByDueDate(String sortType) {
		if ("DUE_DATE".equalsIgnoreCase(sortType)) {
			LocalDate[] range = getDueDateRange();
			return counsel.dueDate.isNotNull()
				.and(filterByCompleted(false))
				.and(counsel.dueDate.between(range[0], range[1]));
		}
		return null;
	}

	private BooleanExpression isUserUidEqualTo(Long userUid) {
		return counsel.user.uid.eq(userUid);
	}

	private BooleanExpression isNotDeleted() {
		return counsel.deletedAt.isNull();
	}

	private BooleanExpression searchNameOrPhone(String search) {
		log.info("search nameOrPhone : {}", search);
		if (!StringUtils.hasText(search)) {
			return null;
		}
		return counsel.customer.name.startsWithIgnoreCase(search)
			.or(counsel.customer.phoneNo.contains(search));
	}

	private BooleanExpression betweenCounselDate(LocalDate startDate, LocalDate endDate) {
		if (startDate == null && endDate == null) {
			return null;
		}
		if (startDate != null && endDate != null) {
			return counsel.counselDate.goe(startDate.atStartOfDay())
				.and(counsel.counselDate.lt(endDate.plusDays(1).atStartOfDay()));
		}
		if (startDate != null) {
			return counsel.counselDate.goe(startDate.atStartOfDay());
		}
		return counsel.counselDate.lt(endDate.plusDays(1).atStartOfDay());
	}

	private BooleanExpression filterByType(String type) {
		CounselType counselType = CounselType.getCounselType(type);
		if (counselType == null) {
			return null;
		}
		return counsel.type.eq(CounselType.getCounselType(type));
	}

	private BooleanExpression filterByCompleted(Boolean completed) {
		if (completed == null) {
			return null;
		}
		return counsel.completed.eq(completed);
	}
}
