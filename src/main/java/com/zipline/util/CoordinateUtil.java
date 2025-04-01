package com.zipline.util;

public class CoordinateUtil {
    private static final double EARTH_RADIUS = 6371.0; // 지구 반경 (km)
    
    // 줌 레벨 12,14의 위도/경도 변화량
    private static final double BASE_LAT_DELTA = 0.13173545;
    private static final double BASE_LON_DELTA = 0.16033175;
    
    /**
     * 주어진 위도, 경도, 줌 레벨에 대한 지리적 범위를 계산합니다.
     * 
     * @param lat 중심 위도
     * @param lon 중심 경도
     * @param zoom 줌 레벨
     * @return [top, right, bottom, left] 순서의 좌표 배열
     */
    public static double[] calculateBounds(double lat, double lon, int zoom) {
        // 줌 레벨에 따른 스케일 조정
        double scale = Math.pow(2, 12 - zoom);
        double latDelta = BASE_LAT_DELTA * scale;
        
        // 경도 변화량 계산 (위도에 따른 보정)
        double latFactor = Math.cos(Math.toRadians(lat));
        double lonDelta = BASE_LON_DELTA * scale * latFactor;
        
        // 중심점 기준으로 상하좌우 범위 계산
        double top = lat + latDelta;
        double right = lon + lonDelta;
        double bottom = lat - latDelta;
        double left = lon - lonDelta;
        
        return new double[] { top, right, bottom, left };
    }
}