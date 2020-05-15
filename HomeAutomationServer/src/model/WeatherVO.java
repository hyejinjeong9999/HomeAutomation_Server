package model;

import java.io.Serializable;

public class WeatherVO implements Serializable {
	String temp;
	String light;
	String onOff;
	String dustDensity;

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getLight() {
		return light;
	}

	public void setLight(String light) {
		this.light = light;
	}

	public String getOnOff() {
		return onOff;
	}

	public void setOnOff(String onOff) {
		this.onOff = onOff;
	}

	public String getDustDensity() {
		return dustDensity;
	}

	public void setDustDensity(String dustDensity) {
		this.dustDensity = dustDensity;
	}

}
