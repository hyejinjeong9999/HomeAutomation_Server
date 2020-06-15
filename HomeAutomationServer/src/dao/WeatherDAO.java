package dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.WeatherVO;

public class WeatherDAO {
	WeatherVO vo;
	
	public WeatherDAO(WeatherVO vo){
		 this.vo = vo;
	}

	public WeatherVO getWeather() {
		System.out.println("JAVA SERVER - DAO getWeather 실행");

		String urlstr = "http://70.12.60.98:8090/homeAutomation/getWeather";

		try {
			URL url = new URL(urlstr);
			BufferedReader bf;
			String line;
			String result = "";

			// 날씨 정보를 받아온다.
			bf = new BufferedReader(new InputStreamReader(url.openStream()));


			
			String line1;
            StringBuffer stringBuffer = new StringBuffer();
            while((line1 = bf.readLine()) != null){
                stringBuffer.append(line1);
            }
            
            //value값을 객체화 시킨다
            ObjectMapper mapper = new ObjectMapper();
            WeatherVO[] resultArr = mapper.readValue(stringBuffer.toString(), WeatherVO[].class);
            vo = resultArr[0]; 
 

			bf.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println(vo.toString());           
		
		return vo;

	}

}
