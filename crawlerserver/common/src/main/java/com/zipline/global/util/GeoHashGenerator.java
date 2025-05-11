package com.zipline.global.util;

import java.util.HashSet;
import java.util.Set;

public class GeoHashGenerator {

    // Base32 문자열 정의 (geohash 표준)
    private static final char[] BASE32 = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    // 비트 마스킹용 배열
    private static final int[] BITS = {16, 8, 4, 2, 1};
    /**
     * 지정된 위도/경도 범위 내에서 GeoHash 생성
     */
    public static Set<String> generateGeoHashs(
            int precision,
            double minLat, double maxLat,
            double minLon, double maxLon,
            double latStep, double lonStep) {

        Set<String> hashes = new HashSet<>();

        for (double lat = minLat; lat <= maxLat; lat += latStep) {
            for (double lon = minLon; lon <= maxLon; lon += lonStep) {
                String hash = encode(lat, lon, precision);
                hashes.add(hash);
            }
        }

        return hashes;
    }

    /**
     * 위도/경도 → GeoHash 인코딩 알고리즘 직접 구현
     */
    public static String encode(double latitude, double longitude, int precision) {
        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0, ch = 0;

        // 위도/경도 범위 초기화
        double[] lat = {-90.0, 90.0};
        double[] lon = {-180.0, 180.0};

        while (geohash.length() < precision) {
            if (isEven) {
                double mid = (lon[0] + lon[1]) / 2;
                if (longitude > mid) {
                    ch |= BITS[bit];
                    lon[0] = mid;
                } else {
                    lon[1] = mid;
                }
            } else {
                double mid = (lat[0] + lat[1]) / 2;
                if (latitude > mid) {
                    ch |= BITS[bit];
                    lat[0] = mid;
                } else {
                    lat[1] = mid;
                }
            }

            isEven = !isEven;
            bit++;

            if (bit == 5) {
                geohash.append(BASE32[ch]);
                bit = 0;
                ch = 0;
            }
        }

        return geohash.toString();
    }
}