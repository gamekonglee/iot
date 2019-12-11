/**
 * Copyright 2018 bejson.com
 */
package com.bean;
import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2018-08-06 15:28:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class WeatherBean {

    private int error;
    private String status;
    private Date date;
    private List<Results> results;
    public void setError(int error) {
        this.error = error;
    }
    public int getError() {
        return error;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Date getDate() {
        return date;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }
    public List<Results> getResults() {
        return results;
    }
/**
 * Copyright 2018 bejson.com
 */

    /**
     * Auto-generated: 2018-08-06 15:30:0
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class Index {

        private String des;
        private String tipt;
        private String title;
        private String zs;
        public void setDes(String des) {
            this.des = des;
        }
        public String getDes() {
            return des;
        }

        public void setTipt(String tipt) {
            this.tipt = tipt;
        }
        public String getTipt() {
            return tipt;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setZs(String zs) {
            this.zs = zs;
        }
        public String getZs() {
            return zs;
        }

    }
    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-08-06 15:30:0
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class Weather_data {

        private String date;
        private String dayPictureUrl;
        private String nightPictureUrl;
        private String weather;
        private String wind;
        private String temperature;
        public void setDate(String date) {
            this.date = date;
        }
        public String getDate() {
            return date;
        }

        public void setDayPictureUrl(String dayPictureUrl) {
            this.dayPictureUrl = dayPictureUrl;
        }
        public String getDayPictureUrl() {
            return dayPictureUrl;
        }

        public void setNightPictureUrl(String nightPictureUrl) {
            this.nightPictureUrl = nightPictureUrl;
        }
        public String getNightPictureUrl() {
            return nightPictureUrl;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }
        public String getWeather() {
            return weather;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }
        public String getWind() {
            return wind;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }
        public String getTemperature() {
            return temperature;
        }

    }
    /**
     * Copyright 2018 bejson.com
     */
    /**
     * Auto-generated: 2018-08-06 15:30:0
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class Results {

        private String currentCity;
        private String pm25;
        private List<Index> index;
        private List<Weather_data> weather_data;
        public void setCurrentCity(String currentCity) {
            this.currentCity = currentCity;
        }
        public String getCurrentCity() {
            return currentCity;
        }

        public void setPm25(String pm25) {
            this.pm25 = pm25;
        }
        public String getPm25() {
            return pm25;
        }

        public void setIndex(List<Index> index) {
            this.index = index;
        }
        public List<Index> getIndex() {
            return index;
        }

        public void setWeather_data(List<Weather_data> weather_data) {
            this.weather_data = weather_data;
        }
        public List<Weather_data> getWeather_data() {
            return weather_data;
        }

    }
}