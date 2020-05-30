package model;

public class WeatherVO {
	String weather; // weather - main
	String temp; // main - temp
	String feelsLike; // main - feels_like
	String tempMin; // main - temp_min
	String tempMax; // main - temp_max
	String humidity; // main - humidity
	String name; // name, 지역명
	String pm10Value;
	String pm10Value24;
	String pm25Value;
	String pm25Value24;
	
	
	

	public String getPm10Value() {
		return pm10Value;
	}

	@Override
	public String toString() {
		return "WeatherVO [weather=" + weather + ", temp=" + temp + ", feelsLike=" + feelsLike + ", tempMin=" + tempMin
				+ ", tempMax=" + tempMax + ", humidity=" + humidity + ", name=" + name + ", pm10Value=" + pm10Value
				+ ", pm10Value24=" + pm10Value24 + ", pm25Value=" + pm25Value + ", pm25Value24=" + pm25Value24 + "]";
	}

	public void setPm10Value(String pm10Value) {
		this.pm10Value = pm10Value;
	}

	public String getPm10Value24() {
		return pm10Value24;
	}

	public void setPm10Value24(String pm10Value24) {
		this.pm10Value24 = pm10Value24;
	}

	public String getPm25Value() {
		return pm25Value;
	}

	public void setPm25Value(String pm25Value) {
		this.pm25Value = pm25Value;
	}

	public String getPm25Value24() {
		return pm25Value24;
	}

	public void setPm25Value24(String pm25Value24) {
		this.pm25Value24 = pm25Value24;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getFeelsLike() {
		return feelsLike;
	}

	public void setFeelsLike(String feelsLike) {
		this.feelsLike = feelsLike;
	}

	public String getTempMin() {
		return tempMin;
	}

	public void setTempMin(String tempMin) {
		this.tempMin = tempMin;
	}

	public String getTempMax() {
		return tempMax;
	}

	public void setTempMax(String tempMax) {
		this.tempMax = tempMax;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

}
