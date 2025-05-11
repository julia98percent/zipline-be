package com.zipline.service.zigbang.crawler;

import com.zipline.domain.entity.enums.PropertyCategory;
import com.zipline.global.util.UrlEncodingUtil;

public class SharedZigbangCrawler {

    private UrlEncodingUtil utf8 = new UrlEncodingUtil();

    public String buildListUrl(PropertyCategory category, String geohash) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://apis.zigbang.com/v2/items/");
        sb.append(utf8.encode(category.getApiPath()));
        sb.append("?geohash=");
        sb.append(utf8.encode(geohash));
        sb.append("&depositMin=0&rentMin=0");
        sb.append("&salesTypes[0]=").append(utf8.encode("전세"));
        sb.append("&salesTypes[1]=").append(utf8.encode("월세"));

        if (category.supportsSaleType()) {
            sb.append("&salesTypes[2]=").append(utf8.encode("매매"));
        }

        sb.append("&domain=zigbang&checkAnyItemWithoutFilter=true");

        return sb.toString();
    }

    public String buildDetailUrl(Long itemId) {
        return "https://api.zigbang.com/v3/items?item_ids= " + itemId;
    }
}
