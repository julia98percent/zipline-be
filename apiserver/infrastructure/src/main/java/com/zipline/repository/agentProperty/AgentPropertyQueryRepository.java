package com.zipline.repository.agentProperty;

import static com.zipline.entity.agentProperty.QAgentProperty.*;

import java.time.Year;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.enums.PropertyCategory;
import com.zipline.entity.enums.PropertyType;
import com.zipline.global.request.AgentPropertyFilterRequestDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgentPropertyQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<AgentProperty> findFilteredProperties(Long userUid, AgentPropertyFilterRequestDTO filter,
		Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(agentProperty.user.uid.eq(userUid))
			.and(agentProperty.deletedAt.isNull());

		if (filter.getType() != null && !filter.getType().isBlank()) {
			try {
				builder.and(agentProperty.type.eq(PropertyType.valueOf(filter.getType())));
			} catch (IllegalArgumentException ignored) {
			}
		}

		if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
			try {
				builder.and(agentProperty.realCategory.eq(PropertyCategory.valueOf(filter.getCategory())));
			} catch (IllegalArgumentException ignored) {
			}
		}

		if (filter.getLegalDistrictCode() != null && !filter.getLegalDistrictCode().isBlank()) {
			builder.and(agentProperty.legalDistrictCode.eq(filter.getLegalDistrictCode()));
		}

		if (filter.getMinDeposit() != null) {
			builder.and(agentProperty.deposit.goe(filter.getMinDeposit()));
		}
		if (filter.getMaxDeposit() != null) {
			builder.and(agentProperty.deposit.loe(filter.getMaxDeposit()));
		}

		if (filter.getMinMonthlyRent() != null) {
			builder.and(agentProperty.monthlyRent.goe(filter.getMinMonthlyRent()));
		}
		if (filter.getMaxMonthlyRent() != null) {
			builder.and(agentProperty.monthlyRent.loe(filter.getMaxMonthlyRent()));
		}

		if (filter.getMinPrice() != null) {
			builder.and(agentProperty.price.goe(filter.getMinPrice()));
		}
		if (filter.getMaxPrice() != null) {
			builder.and(agentProperty.price.loe(filter.getMaxPrice()));
		}

		if (filter.getMinMoveInDate() != null) {
			builder.and(agentProperty.moveInDate.goe(filter.getMinMoveInDate()));
		}
		if (filter.getMaxMoveInDate() != null) {
			builder.and(agentProperty.moveInDate.loe(filter.getMaxMoveInDate()));
		}

		if (filter.getPetsAllowed() != null) {
			builder.and(agentProperty.petsAllowed.eq(filter.getPetsAllowed()));
		}

		if (filter.getMinFloor() != null) {
			builder.and(agentProperty.floor.goe(filter.getMinFloor()));
		}
		if (filter.getMaxFloor() != null) {
			builder.and(agentProperty.floor.loe(filter.getMaxFloor()));
		}

		if (filter.getHasElevator() != null) {
			builder.and(agentProperty.hasElevator.eq(filter.getHasElevator()));
		}

		if (filter.getMinConstructionYear() != null) {
			builder.and(agentProperty.constructionYear.goe(Year.of(filter.getMinConstructionYear())));
		}
		if (filter.getMaxConstructionYear() != null) {
			builder.and(agentProperty.constructionYear.loe(Year.of(filter.getMaxConstructionYear())));
		}

		if (filter.getMinParkingCapacity() != null) {
			builder.and(agentProperty.parkingCapacity.goe(filter.getMinParkingCapacity()));
		}
		if (filter.getMaxParkingCapacity() != null) {
			builder.and(agentProperty.parkingCapacity.loe(filter.getMaxParkingCapacity()));
		}

		if (filter.getMinNetArea() != null) {
			builder.and(agentProperty.netArea.goe(filter.getMinNetArea()));
		}
		if (filter.getMaxNetArea() != null) {
			builder.and(agentProperty.netArea.loe(filter.getMaxNetArea()));
		}

		if (filter.getMinTotalArea() != null) {
			builder.and(agentProperty.totalArea.goe(filter.getMinTotalArea()));
		}
		if (filter.getMaxTotalArea() != null) {
			builder.and(agentProperty.totalArea.loe(filter.getMaxTotalArea()));
		}

		List<AgentProperty> result = queryFactory
			.selectFrom(agentProperty)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(agentProperty.uid.desc())
			.fetch();

		return PageableExecutionUtils.getPage(result, pageable, () -> queryFactory
			.select(agentProperty.count())
			.from(agentProperty)
			.where(builder)
			.fetchOne());
	}
}