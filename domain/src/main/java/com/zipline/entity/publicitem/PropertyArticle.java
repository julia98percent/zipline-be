package com.zipline.entity.publicitem;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zipline.entity.enums.Category;
import com.zipline.entity.enums.Platform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "property_articles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyArticle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "article_id")
  private String articleId;

  @Column(name = "region_code")
  private String regionCode;

  @Column(name = "category")
  @Enumerated(EnumType.STRING)
  private Category category;

  @Column(name = "building_name")
  private String buildingName;

  @Column(name = "description", columnDefinition = "LONGTEXT")
  private String description;

  @Column(name = "building_type")
  private String buildingType;

  @Column(name = "price")
  private Long price;

  @Column(name = "deposit")
  private Long deposit;

  @Column(name = "monthly_rent")
  private Long monthlyRent;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "total_area")
  private Double totalArea;

  @Column(name = "net_area")
  private Double netArea;

  @Column(name = "address")
  private String address;

  @Column(name = "platform")
  @Enumerated(EnumType.STRING)
  private Platform platform;

  @Column(name = "platform_url")
  private String platformUrl;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public static PropertyArticleBuilder builder() {
    return new PropertyArticleBuilder();
  }

  public static class PropertyArticleBuilder {

    private Long id;
    private String articleId;
    private String regionCode;
    private Category category;
    private String buildingName;
    private String description;
    private String buildingType;
    private Long price;
    private Long deposit;
    private Long monthlyRent;
    private Double longitude;
    private Double latitude;
    private Double netArea;
    private Double totalArea;
    private String address;
    private Platform platform;
    private String platformUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    PropertyArticleBuilder() {
    }

    public PropertyArticleBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public PropertyArticleBuilder articleId(String articleId) {
      this.articleId = articleId;
      return this;
    }

    public PropertyArticleBuilder regionCode(String regionCode) {
      this.regionCode = regionCode;
      return this;
    }

    public PropertyArticleBuilder category(Category category) {
      this.category = category;
      return this;
    }

    public PropertyArticleBuilder buildingName(String buildingName) {
      this.buildingName = buildingName;
      return this;
    }

    public PropertyArticleBuilder description(String description) {
      this.description = description;
      return this;
    }

    public PropertyArticleBuilder buildingType(String buildingType) {
      this.buildingType = buildingType;
      return this;
    }

    public PropertyArticleBuilder price(Long price) {
      this.price = price;
      return this;
    }

    public PropertyArticleBuilder deposit(Long deposit) {
      this.deposit = deposit;
      return this;
    }

    public PropertyArticleBuilder monthlyRent(Long monthlyRent) {
      this.monthlyRent = monthlyRent;
      return this;
    }

    public PropertyArticleBuilder longitude(Double longitude) {
      this.longitude = longitude;
      return this;
    }

    public PropertyArticleBuilder latitude(Double latitude) {
      this.latitude = latitude;
      return this;
    }

    public PropertyArticleBuilder totalArea(Double totalArea) {
      this.totalArea = totalArea;
      return this;
    }

    public PropertyArticleBuilder netArea(Double netArea) {
      this.netArea = netArea;
      return this;
    }

    public PropertyArticleBuilder address(String address) {
      this.address = address;
      return this;
    }

    public PropertyArticleBuilder platform(Platform platform) {
      this.platform = platform;
      return this;
    }

    public PropertyArticleBuilder platformUrl(String platformUrl) {
      this.platformUrl = platformUrl;
      return this;
    }

    public PropertyArticleBuilder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public PropertyArticleBuilder updatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public PropertyArticle build() {
      return new PropertyArticle(this.id, this.articleId, this.regionCode, this.category,
          this.buildingName,
          this.description, this.buildingType, this.price, this.deposit, this.monthlyRent,
          this.longitude,
          this.latitude, this.totalArea, this.netArea, this.address, this.platform,
          this.platformUrl,
          this.createdAt, this.updatedAt);
    }

    public String toString() {
      return "PropertyArticle.PropertyArticleBuilder(id=" + this.id + ", articleId="
          + this.articleId
          + ", regionCode=" + this.regionCode + ", category=" + this.category + ", buildingName="
          + this.buildingName + ", description=" + this.description + ", buildingType="
          + this.buildingType
          + ", price=" + this.price + ", deposit=" + this.deposit + ", monthlyRent="
          + this.monthlyRent
          + ", longitude=" + this.longitude + ", latitude=" + this.latitude + ", totalArea="
          + this.totalArea
          + ", netArea=" + this.netArea + ", platform=" + this.platform
          + ", platformUrl="
          + this.platformUrl + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt
          + ")";
    }
  }
}