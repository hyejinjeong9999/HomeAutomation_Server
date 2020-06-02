package dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import model.WeatherVO;

public class WeatherDAO {
	
		public ArrayList<WeatherVO> getWeather(WeatherVO vo) {

			String urlstr = "http://192.168.27.1:8090/homeAutomation/getWeather";

			try {
				URL url = new URL(urlstr);
				BufferedReader bf;
				String line;
				String result = "";

				// 날씨 정보를 받아온다.
				bf = new BufferedReader(new InputStreamReader(url.openStream()));

				// 버퍼에 있는 정보를 문자열로 변환 -> JSON 확인
				while ((line = bf.readLine()) != null) {
					result = result.concat(line);
					System.out.println(line);
				}

				// 문자열을 JSON으로 파싱
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObj = (JSONObject) jsonParser.parse(result);

				// 지역 저장
				String name = (String) jsonObj.get("name");
				vo.setName(name);
				
				//최고온도
				String tempMax = (String) jsonObj.get("tempMax");
				vo.setName(tempMax);

				//습도
				String humidity = (String) jsonObj.get("humidity");
				vo.setName(humidity);
				//현재온도
				String temp = (String) jsonObj.get("temp");
				vo.setName(temp);
				
				//체감온도
				String feelsLike = (String) jsonObj.get("feelsLike");
				vo.setName(feelsLike);
				
				//날씨
				String weather = (String) jsonObj.get("weather");
				vo.setName(weather);
				
				//pm10
				String pm10Value = (String) jsonObj.get("pm10Value");
				vo.setName(pm10Value);
				
				//pm25
				String pm25Value = (String) jsonObj.get("pm25Value");
				vo.setName(pm25Value);
				
				
			
				

				bf.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			//doa 값 LIST에 저장
			ArrayList<WeatherVO> result = new ArrayList<WeatherVO>();
			result.add(vo);
			return result;

		}
	}


