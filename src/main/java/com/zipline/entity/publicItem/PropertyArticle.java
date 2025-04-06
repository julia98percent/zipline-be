package com.zipline.entity.publicItem;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import com.zipline.entity.publicItem.enums.Category;
import com.zipline.entity.publicItem.enums.Platform;

@Entity
@Table(name = "property_articles")
@Getter
@Setter
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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "building_type")
    private String buildingType;

    @Column(name = "address")
    private String address;

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

    @Column(name = "supply_area")
    private Double supplyArea;

    @Column(name = "exclusive_area")
    private Double exclusiveArea;

    @Column(name = "platform")
    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(name = "platform_url")
    private String platformUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 
