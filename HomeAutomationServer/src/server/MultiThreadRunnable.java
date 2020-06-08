package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import dao.WeatherDAO;
import model.SensorDataVO;
import model.WeatherVO;

public class MultiThreadRunnable implements Runnable {
	Socket socket;
	BufferedReader bufferedReader;
	PrintWriter printWriter;
	SharedObject sharedObject;
	SensorDataVO sensorDataVO;
	StringTokenizer st;
	String moduleID;
	WeatherVO weatherVO = new WeatherVO();
	WeatherDAO weatherDAO = new WeatherDAO(weatherVO);

	long startTime;
	long endTime;
	String order;
	boolean testStatus = false;

	// client, module list 생성

	public String getModuleID() {
		return moduleID;
	}

	// Construction injection
	// Constructor - Socket과 공용객체를 답아와 초기화 해준다

	// 안드로이드에 값 전달
	public MultiThreadRunnable(Socket socket, SharedObject sharedObject, SensorDataVO vo,
			ObjectOutputStream objectOutputStream) {
		this.socket = socket;
		this.sharedObject = sharedObject;
		this.sensorDataVO = vo;

		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			this.printWriter = new PrintWriter(socket.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// matlab 생성자
	public MultiThreadRunnable(Socket socket, SharedObject sharedObject, SensorDataVO vo,
			ObjectOutputStream objectOutputStream, String ml) {
		this.socket = socket;
		this.sharedObject = sharedObject;
		this.sensorDataVO = vo;
		this.moduleID = ml;
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			this.printWriter = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
//		sharedObject.add(ml, this);
	}

	@Override
	public synchronized void run() {
		// Client 로부터 넘어온 'MSG' 를 판별하여 기능 실행

		if (socket.getInetAddress().toString().equals("/70.12.60.91")) {
			System.out.println("matlap runnable");
			sharedObject.addClients(moduleID, MultiThreadRunnable.this);
		}
		weatherDAO.getWeather();

		String msg = "";
		try {
			// 값들을
			while ((msg = bufferedReader.readLine()) != null) {
				 System.out.println("test==" + msg);
				if (msg.contains("ANDROID>")) {
					order = msg.replace("/ANDROID>", "");
					/// ANDROID>/MODE SMART -> /MODE SMART
					if (msg.contains("/MODE")) {
						// mode 뒤에 값을 알아낸담에 vo - mode 저장, 각 모드별로 알고리즘처리

						////////////////////
						// smart모드 모드일때
						if (msg.contains("SMART")) {
							System.out.println("SMART모드 시작");
							
							sensorDataVO.setMode("ON");
							// smart 모드
							// 온도가 높으면 에어컨 //실내 대기질 나쁨 -> 날씨, 실외대기질, window
						} else if (msg.contains("SLEEP")) {
							// 수면모드
							System.out.println("SLEEP모드 시작");

							order = "LIGHT OFF";
							sharedObject.sendTOModule(order);

							sensorDataVO.setMode("OFF");

						} else if (msg.contains("VENTILATION")) {
							// 환기모드
							System.out.println("VENTILATION모드 시작");
							sensorDataVO.setMode("OFF");

							order = "WINDOW OPEN";
							sharedObject.sendTOModule(order);

							order = "AIRPURIFIER OFF";
							sharedObject.sendTOModule(order);

							order = "AIRCONDITIONER OFF";
							sharedObject.sendTOModule(order);

						} else if (msg.contains("OUTING")) {
							// 외출
							System.out.println("OUTING모드 시작");
							sensorDataVO.setMode("OFF");

							order = "AIRCONDITIONER OFF";
							sharedObject.sendTOModule(order);

							order = "LIGHT OFF";
							sharedObject.sendTOModule(order);
						}
							sharedObject.sendJsonDataToAndroid(sensorDataVO);
							// TODO Auto-generated catch block
					}
					// 센서에게 값 전송
					else {
						if (!(order.equals("REFRESH"))) {
							sharedObject.sendTOModule(order);
						}
						sharedObject.sendJsonDataToAndroid(sensorDataVO);
					}
				//수동모드일때
				
				}

				// 스마트모드일 경우
				if (sensorDataVO.getMode().equals("ON")) {
					System.out.println("--------------스마트모드실행----------");
					
					weatherVO = weatherDAO.getWeather();
					
					
					System.out.println("weatherVO test"+ weatherVO.getFeelsLike());
					// 공기청정기 ON --> 기본으로 항상 켜져 있음
					if (!(sensorDataVO.getWindowStatus().equals("ON"))) {
						order = "AIRPURIFIER ON";
						sharedObject.sendTOModule(order);
					}

					// 온도가 높으면 에어컨 키기
					System.out.println("weatherVO.getTemp()==================" + weatherVO.getTemp());
					if (Double.parseDouble(weatherVO.getTemp()) > Integer.parseInt(sensorDataVO.getAirconditionerTemp())) {
						order = "AIRCONDITIONER ON";
						sharedObject.sendTOModule(order);
					}

					// 실내대기질 안좋을때
					if (Integer.parseInt(sensorDataVO.getDust10()) > 35) {
						// 바깥날씨 비, 눈, 안개 미세먼지 81이상 이 아닐때 창문열기 : 81기준 : 미세먼지 나쁨~매우나쁨

						if (!(testStatus)) { // false 일때 ->

							if (!((weatherVO.getWeather().equals("Rain") || weatherVO.getWeather().equals("Snow")
									|| weatherVO.getWeather().equals("Drizzle"))
									&& Integer.parseInt(weatherVO.getPm10Value()) > 81)) {

								order = "WINDOW ON";
								sharedObject.sendTOModule(order);

								order = "AIRPURIFIER OFF";
								sharedObject.sendTOModule(order);

								testStatus = true;
								startTime = System.currentTimeMillis();

							}
						} else {
							endTime = System.currentTimeMillis();

							if ((startTime - endTime) / 1000 == 600) {

								order = "WINDOW CLOSE";
								sharedObject.sendTOModule(order);

								testStatus = false;

							}

						}

					}
					sharedObject.sendJsonDataToAndroid(sensorDataVO);

				}

				if (msg.contains("/ID:")) {
					st = new StringTokenizer(msg, " ");

					// ID가 IN 이면 값 추가
					if (msg.contains("IN")) {
						this.moduleID = st.nextToken().replace("/ID:", "");
						System.out.println(this.moduleID);
						sharedObject.addClients(this.moduleID, this);
						sharedObject.sendJsonDataToAndroid(sensorDataVO);
						continue;

					} else {
						// ID가 OUT이면 연결 끊기
						sharedObject.removeClients(moduleID, this);
						continue;
					}
					// 라떼판다 값 받아오기 -> 10초에 한번씩 받아옴
//				} else if (msg.contains("TEMPRATURE") || msg.contains("WINDOWSTATUS") || msg.contains("DUSTDENSITY")
//						|| msg.contains("AIRPURIFIERSTATUS") || msg.contains("AIRCONDITIONERSTATUS")
//						|| msg.contains("LIGHT")) {
//					st = new StringTokenizer(msg, " ");
//					sensorDataVO.setTemp(st.nextToken().replace("/TEMPRATURE:", ""));
//					sensorDataVO.setLightStatus(st.nextToken().replace("/LIGHT:", ""));
//					sensorDataVO.setWindowStatus(st.nextToken().replace("/WINDOWSTATUS:", ""));
////					sensorDataVO.setDustDensity(st.nextToken().replace("/DUSTDENSITY:", ""));
////					sensorDataVO.setAirpurifierStatus(st.nextToken().replace("/AIRPURIFIERSTATUS:", ""));
////					sensorDataVO.setAirconditionerStatus(st.nextToken().replace("/AIRCONDITIONERSTATUS:", ""));
//					sharedObject.sendJsonDataToAndroid(sensorDataVO);

					// 미세먼지
				} else if (msg.startsWith("/AIRPURIFIER")) {
					st = new StringTokenizer(msg, " ");
					System.out.println(st.toString());
					st.nextToken();
					sensorDataVO.setAirpurifierStatus(st.nextToken().replace("/airpurifierStatus:", ""));
					sensorDataVO.setDust25(st.nextToken().replace("/dust25:", ""));
					sensorDataVO.setDust10(st.nextToken().replace("/dust10:", ""));
					sensorDataVO.setGasStatus(st.nextToken().replace("/gasStatus:", ""));
					

					// 에어컨
				} else if (msg.startsWith("/AIRCONDITIONER")) {
					st = new StringTokenizer(msg, " ");
					st.nextToken();
					sensorDataVO.setAirconditionerStatus(st.nextToken().replace("/airconditionerStatus:", ""));
					sensorDataVO.setTemp(st.nextToken().replace("/temp:", ""));
					sensorDataVO.setAirconditionerMode(st.nextToken().replace("/airconditionerMode:",""));
					sensorDataVO.setAirconditionerTemp(st.nextToken().replace("/airconditionerTemp:",""));
					sensorDataVO.setAirconditionerSpeed(st.nextToken().replace("/airconditionerSpeed:",""));
					

					// 창문
				} else if (msg.startsWith("/WINDOW")) {
					st = new StringTokenizer(msg, " ");
					st.nextToken();
					sensorDataVO.setWindowStatus(st.nextToken().replace("/windowStatus:", ""));

					// 카메라 이미지 저장
				} else if (msg.contains("CAMERA")) {

					String order = msg.replace("/CAMERA>IMAGE ", "");
					sharedObject.saveImage(order);

				}
				sharedObject.sendJsonDataToAndroid(sensorDataVO);
				System.out.println(sensorDataVO.toString());
			}
		} catch (

		IOException e) {
			e.printStackTrace();

		} finally {
			try {
				bufferedReader.close();
				printWriter.close();
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public PrintWriter getPrintWriter() {
		return printWriter;
	}

}
