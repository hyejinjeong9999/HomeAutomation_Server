package model;

import java.io.Serializable;

public  class TestVO implements Serializable {
	String temp;
	String light;
	String onOff;
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



}
