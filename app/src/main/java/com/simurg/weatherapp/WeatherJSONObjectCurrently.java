package com.simurg.weatherapp;

/**
 * Created by uyegen on 25.01.2017.
 */

public class WeatherJSONObjectCurrently {
    public int time;
    public String summary; //Özet
    public String icon; //Simge
    public float precipIntensity; //Yağış yoğunluğu
    public float precipProbability; //Yağış Olasılığı
    public String precipType; //Yağış Tipi
    public float temperature; //sıcaklık
    public float apparentTemperature; //Belirgin sıcaklık
    public float dewPoint; //Çiy noktası
    public float humidity; //nem
    public float windSpeed; //Rüzgar hızı
    public float windBearing; //Rüzgar türbanlı
    public float cloudCover; //Bulut örtüsü
    public float pressure; //basınç
    public float ozone; //ozon

    public String getClearedIcon() {
        return this.icon.replace("-", "_");
    }
    public String getBackgroundName() {
        return "_" + this.icon.replace("-", "_");
    }
}