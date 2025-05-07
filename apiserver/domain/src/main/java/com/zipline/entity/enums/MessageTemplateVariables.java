package com.zipline.entity.enums;

public enum MessageTemplateVariables {
  NAME("이름"),
  BIRTH_DATE("생년월일"),
  INTEREST_AREA("관심지역");

  private final String koreanFieldName;

  MessageTemplateVariables(String koreanFieldName) {
    this.koreanFieldName = koreanFieldName;
  }

  public String getTemplateKey() {
    return "{{" + this.koreanFieldName + "}}";
  }
}