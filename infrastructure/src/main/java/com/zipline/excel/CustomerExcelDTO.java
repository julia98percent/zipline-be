package com.zipline.excel;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.util.StringUtils;

import com.zipline.global.exception.excel.ExcelException;
import com.zipline.global.exception.excel.errorcode.ExcelErrorCode;

import lombok.Getter;

@Getter
public class CustomerExcelDTO {

  private Integer rowNum;
  private String name;
  private String phoneNo;
  private String telProvider;
  private String preferredRegion;
  private boolean landlord;
  private boolean tenant;
  private boolean buyer;
  private boolean seller;
  private BigInteger minRent;
  private BigInteger maxRent;
  private BigInteger maxPrice;
  private BigInteger minPrice;
  private BigInteger minDeposit;
  private BigInteger maxDeposit;
  private String birthday;

  public CustomerExcelDTO(Integer rowNum, String name, String phoneNo, String telProvider,
      String preferredRegion,
      boolean landlord, boolean tenant, boolean buyer, boolean seller, BigInteger minRent,
      BigInteger maxRent,
      BigInteger maxPrice, BigInteger minPrice, BigInteger minDeposit, BigInteger maxDeposit,
      String birthday) {
    this.rowNum = rowNum;
    this.name = name;
    this.phoneNo = phoneNo;
    this.telProvider = telProvider;
    this.preferredRegion = preferredRegion;
    this.landlord = landlord;
    this.tenant = tenant;
    this.buyer = buyer;
    this.seller = seller;
    this.minRent = minRent;
    this.maxRent = maxRent;
    this.maxPrice = maxPrice;
    this.minPrice = minPrice;
    this.minDeposit = minDeposit;
    this.maxDeposit = maxDeposit;
    this.birthday = birthday;
  }

	public void validate() {
		if (!StringUtils.hasText(name)) {
			throwValidation("name", name, "이름은 필수입니다.");
		}
    this.birthday = birthday;

		if (!phoneNo.matches("^(\\d{3})-(\\d{3,4})-(\\d{4})$")) {
			throwValidation("phoneNo", phoneNo, "전화번호 형식이 올바르지 않습니다.");
		}

		if (minRent != null && maxRent != null && minRent.compareTo(maxRent) > 0) {
			throwValidation("minRent/maxRent", minRent + "/" + maxRent, "최소 월세는 최대 월세보다 작거나 같아야 합니다.");
		}

		if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
			throwValidation("minPrice/maxPrice", minPrice + "/" + maxPrice, "최소 매매가는 최대 매매가보다 작거나 같아야 합니다.");
		}

		if (minDeposit != null && maxDeposit != null && minDeposit.compareTo(maxDeposit) > 0) {
			throwValidation("minDeposit/maxDeposit", minDeposit + "/" + maxDeposit, "최소 보증금은 최대 보증금보다 작거나 같아야 합니다.");
		}

    if (StringUtils.hasText(birthday)) {
      try {
        LocalDate birth = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyyMMdd"));
        if (birth.isAfter(LocalDate.now())) {
          throwValidation("birthday", birthday, "생년월일은 미래일 수 없습니다.");
        }
      } catch (DateTimeParseException e) {
        throwValidation("birthday", birthday, "생년월일 형식은 yyyyMMdd 입니다.");
      }
    }
  }

	private void throwValidation(String field, Object value, String message) {
		throw new ExcelException(ExcelErrorCode.INVALID_INPUT_VALUE, rowNum, field, value, message);
	}
}
