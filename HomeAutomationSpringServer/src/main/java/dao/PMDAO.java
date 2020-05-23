package dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.codehaus.jackson.type.TypeReference;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.XML;

import vo.WeatherVO;

public class PMDAO {

	public static void main(String[] args) {

	}

	public void PM(WeatherVO vo) {

		try {
			String urlstr = "http://openapi.airkorea.or.kr/"
					+ "openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?"
					+ "serviceKey=ZPORsqYHYcLYLiUhda7AF57glbFasbIscQpUMuWawiKHehhm4s2gy5LdzKbHNzqpZpUUlXyEPOcFy%2F5uFHLZxQ%3D%3D&numOfRows=2&pageNo=1&"
					+ "stationName=강남구&dataTerm=DAILY&ver=1.3&_returnType=json";

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
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(result);

			JSONArray pmArray = (JSONArray) jsonObj.get("list");
			JSONObject obj = (JSONObject) pmArray.get(1);
			// pm10Value
			vo.setPm10Value(obj.get("pm10Value").toString());
			System.out.println("값" + obj.get("pm10Value").toString());

			// pm10Value24
			vo.setPm10Value24(obj.get("pm10Value24").toString());
			System.out.println("값" + obj.get("pm10Value24").toString());

			// pm25Value
			vo.setPm25Value(obj.get("pm25Value").toString());
			System.out.println("값" + obj.get("pm25Value").toString());

			// m25Value24
			vo.setPm25Value24(obj.get("pm25Value24").toString());
			System.out.println("값" + obj.get("pm25Value24").toString());

		} catch (Exception e) {

			System.out.println("오류" + e.getMessage());
		}

	}

}
