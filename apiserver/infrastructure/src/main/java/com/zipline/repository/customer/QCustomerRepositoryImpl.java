package com.zipline.repository.customer;

import static com.zipline.entity.customer.QCustomer.*;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipline.entity.customer.Customer;
import com.zipline.global.request.CustomerFilterRequestDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QCustomerRepositoryImpl implements QCustomerRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Customer> findByUserUidAndDeletedAtIsNullWithFilters(Long userUid,
		CustomerFilterRequestDTO customerFilterRequestDTO, Pageable pageable) {
		List<Customer> result = queryFactory
			.selectFrom(customer)
			.where(
				notDeleted(),
				searchNameOrPhoneNo(customerFilterRequestDTO.getSearch()),
				regionCode(customerFilterRequestDTO.getRegionCode()),
				customerType(
					customerFilterRequestDTO.getTenant(),
					customerFilterRequestDTO.getLandlord(),
					customerFilterRequestDTO.getBuyer(),
					customerFilterRequestDTO.getSeller(),
					customerFilterRequestDTO.getNoRole()
				),
				priceRange(customerFilterRequestDTO.getMinPrice(), customerFilterRequestDTO.getMaxPrice()),
				depositRange(customerFilterRequestDTO.getMinDeposit(), customerFilterRequestDTO.getMaxDeposit()),
				rentRange(customerFilterRequestDTO.getMinRent(), customerFilterRequestDTO.getMaxRent()),
				label(customerFilterRequestDTO.getLabelUids())
			)
			.orderBy(customer.name.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> totalCount = queryFactory
			.select(customer.count())
			.from(customer)
			.where(
				notDeleted(),
				searchNameOrPhoneNo(customerFilterRequestDTO.getSearch()),
				regionCode(customerFilterRequestDTO.getRegionCode()),
				customerType(
					customerFilterRequestDTO.getTenant(),
					customerFilterRequestDTO.getLandlord(),
					customerFilterRequestDTO.getBuyer(),
					customerFilterRequestDTO.getSeller(),
					customerFilterRequestDTO.getNoRole()
				),
				priceRange(customerFilterRequestDTO.getMinPrice(), customerFilterRequestDTO.getMaxPrice()),
				depositRange(customerFilterRequestDTO.getMinDeposit(), customerFilterRequestDTO.getMaxDeposit()),
				rentRange(customerFilterRequestDTO.getMinRent(), customerFilterRequestDTO.getMaxRent()),
				label(customerFilterRequestDTO.getLabelUids())
			);

		return PageableExecutionUtils.getPage(result, pageable, totalCount::fetchOne);
	}

	private BooleanExpression notDeleted() {
		return customer.deletedAt.isNull();
	}

	private BooleanExpression searchNameOrPhoneNo(String search) {
		if (!StringUtils.hasText(search)) {
			return null;
		}
		return customer.name.startsWithIgnoreCase(search).or(customer.phoneNo.startsWithIgnoreCase(search));
	}

	private BooleanExpression regionCode(String regionCode) {
		if (!StringUtils.hasText(regionCode)) {
			return null;
		}
		return customer.legalDistrictCode.eq(regionCode);
	}

	private BooleanExpression customerType(Boolean isTenant, Boolean isLandlord,
		Boolean isBuyer, Boolean isSeller, Boolean noRole) {
		BooleanExpression expression = null;

		if (noRole != null && noRole) {
			return customer.isTenant.eq(false)
				.and(customer.isLandlord.eq(false))
				.and(customer.isBuyer.eq(false))
				.and(customer.isSeller.eq(false));
		}

		if (isTenant != null) {
			expression = (expression == null) ?
				customer.isTenant.eq(isTenant) :
				expression.or(customer.isTenant.eq(isTenant));
		}
		if (isLandlord != null) {
			expression = (expression == null) ?
				customer.isLandlord.eq(isLandlord) :
				expression.or(customer.isLandlord.eq(isLandlord));
		}
		if (isBuyer != null) {
			expression = (expression == null) ?
				customer.isBuyer.eq(isBuyer) :
				expression.or(customer.isBuyer.eq(isBuyer));
		}
		if (isSeller != null) {
			expression = (expression == null) ?
				customer.isSeller.eq(isSeller) :
				expression.or(customer.isSeller.eq(isSeller));
		}

		return expression;
	}

	private BooleanExpression priceRange(BigInteger minPrice, BigInteger maxPrice) {
		BooleanExpression expression = null;

		if (minPrice != null) {
			expression = customer.minPrice.goe(minPrice);
		}
		if (maxPrice != null) {
			expression = (expression == null) ?
				customer.maxPrice.loe(maxPrice) :
				expression.and(customer.maxPrice.loe(maxPrice));
		}
		return expression;
	}

	private BooleanExpression depositRange(BigInteger minDeposit, BigInteger maxDeposit) {
		BooleanExpression expression = null;

		if (minDeposit != null) {
			expression = customer.minDeposit.goe(minDeposit);
		}
		if (maxDeposit != null) {
			expression = (expression == null) ?
				customer.maxDeposit.loe(maxDeposit) :
				expression.and(customer.maxDeposit.loe(maxDeposit));
		}
		return expression;
	}

	private BooleanExpression rentRange(BigInteger minRent, BigInteger maxRent) {
		BooleanExpression expression = null;

		if (minRent != null) {
			expression = customer.minRent.goe(minRent);
		}
		if (maxRent != null) {
			expression = (expression == null) ?
				customer.maxRent.loe(maxRent) :
				expression.and(customer.maxRent.loe(maxRent));
		}
		return expression;
	}

	private BooleanExpression label(List<Long> labelUids) {
		if (labelUids == null || labelUids.isEmpty()) {
			return null;
		}
		return customer.labelCustomers.any().label.uid.in(labelUids);
	}
}