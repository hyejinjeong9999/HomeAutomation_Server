package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import dao.LogDAO;
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
				//System.out.println("test==" + msg);
				if (msg.contains("ANDROID>")) {
					order = msg.replace("/ANDROID>", "");

					// ANDROID>/LOG -> LOG
					if (msg.contains("/LOG")) {

						// "/ANDROID>/LOG"
						//// LogData 전달
						sharedObject.sendLogDataToAndroid();
						continue;

					}

					/// ANDROID>/MODE SMART -> /MODE SMART
					if (msg.contains("/MODE")) {
						// mode 뒤에 값을 알아낸담에 vo - mode 저장, 각 모드별로 알고리즘처리

						////////////////////
						// smart모드 모드일때
						if (msg.contains("SMART")) {
							System.out.println("SMART모드 시작");

							sensorDataVO.setMode("SMART");
							// smart 모드
							// 온도가 높으면 에어컨 //실내 대기질 나쁨 -> 날씨, 실외대기질, window
						} else if (msg.contains("OFF")) {
							/**
							 * 수면모드 : 전등 OFF
							 */
							System.out.println("SMART 모드 OFF");

							sensorDataVO.setMode("OFF");

						}

						else if (msg.contains("SLEEP")) {
							/**
							 * 수면모드 : 전등 OFF
							 */
							System.out.println("SLEEP모드 시작");

							order = "WINDOW LIGHTOFF";
							sharedObject.sendTOModule(order, moduleID);

							sensorDataVO.setMode("SLEEP");

						} else if (msg.contains("VENTILATION")) {
							/*
							 * 환기모드 창문 OPEN 공기청정기 OFF 에어컨 OFF
							 */
							System.out.println("VENTILATION모드 시작");
							sensorDataVO.setMode("VENTILATION");

							order = "WINDOW ON";
							sharedObject.sendTOModule(order, moduleID);

							order = "AIRPURIFIER OFF";
							sharedObject.sendTOModule(order, moduleID);

							order = "AIRCONDITIONER OFF";
							sharedObject.sendTOModule(order, moduleID);

						} else if (msg.contains("OUTING")) {
							/*
							 * 외출 모드 에어컨 OFF 불 OFF
							 */
							System.out.println("OUTING모드 시작");
							sensorDataVO.setMode("OUTING");

							order = "AIRCONDITIONER OFF";
							sharedObject.sendTOModule(order, moduleID);

							order = "WINDOW LIGHTOFF";
							sharedObject.sendTOModule(order, moduleID);
							
							order = "WINDOW OFF";
							sharedObject.sendTOModule(order, moduleID);
							
							//WINDOW LIGHTON  // WINDOW LIGHTOFF
						}
						sharedObject.sendJsonDataToAndroid(sensorDataVO);
					}
					// 센서에게 값 전송하는 부분
					else {
						if (!(order.equals("REFRESH"))) {
							// 새로고침 아닐 때 (REFRESH X) 모듈에게 값전송
							sharedObject.sendTOModule(order, moduleID);
						}
						// JSONDATA Android한테 보내기
						sharedObject.sendJsonDataToAndroid(sensorDataVO);
					}

				}
				//Start Smart Mode
				if (sensorDataVO.getMode().equals("SMART")) {
					
					System.out.println("--------------스마트모드실행중----------");
					weatherVO = weatherDAO.getWeather();

					System.out.println("===========order 보내기 전========" + sensorDataVO.getWindowStatus());

					System.out.println("weatherVO test" + weatherVO.getFeelsLike());

					if (!(sensorDataVO.getWindowStatus().equals("ON"))
							&& sensorDataVO.getAirpurifierStatus().equals("OFF")) {
						/*
						 * 공기청정기 ON --> 기본으로 항상 켜져 있음 창문이 열려있지 않으면 (ON이 아닐때) 공기청정기 실행
						 */
						System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~공기청정기 실행");

						order = "AIRPURIFIER ON";
						sharedObject.sendTOModule(order, moduleID);

						System.out.println("===========order 보내기 후========" + sensorDataVO.getWindowStatus());

					}

					// 온도가 높으면 에어컨 키기
					System.out.println("SMART MODE 기본 희망온도 " + weatherVO.getTemp());
					if (Double.parseDouble(weatherVO.getTemp()) > Double
							.parseDouble(sensorDataVO.getAirconditionerTemp())
							&& sensorDataVO.getAirconditionerStatus().equals("OFF")) {
						// 실내온도 (getTemp)가 희망온도 (getAirconditionerTemp)보다 놓으면 동작
						System.out.println("===SMART 모드 에어컨 작동 ===");
						order = "AIRCONDITIONER ON";
						sharedObject.sendTOModule(order, moduleID);
					}

					// 실내 대기질이 안좋을때(35이상일 때) 창문 열기
					if (Double.parseDouble(sensorDataVO.getDust10()) > 35) {

						if (sensorDataVO.getWindowStatus().equals("OFF")) {
							// false 일때 (창문이 닫혀있는 상태일때)
							// testStatue true로 변경(창문이 열려있음), startTime 설정 (10분뒤 닫기 위해)
							if (!((weatherVO.getWeather().equals("Rain") || weatherVO.getWeather().equals("Snow")
									|| weatherVO.getWeather().equals("Drizzle"))
									&& Integer.parseInt(weatherVO.getPm10Value()) > 81)) {
								// 바깥날씨 비, 눈, 안개 미세먼지 81이상 이 아닐때 창문열기 (81기준 : 미세먼지 나쁨~매우나쁨)
								System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
								order = "WINDOW ON";
								sharedObject.sendTOModule(order, moduleID);

								order = "AIRPURIFIER OFF";
								sharedObject.sendTOModule(order, moduleID);

								startTime = System.currentTimeMillis();

							}
						} else {
							// true일 때 (창문이 열려있는 상태일 때)
							// 열려있던 상태가 (startTime - endTime)10분 이상이면 창문 닫기
							endTime = System.currentTimeMillis();

							if ((startTime - endTime) / 1000 == 600) {

								order = "WINDOW OFF";
								sharedObject.sendTOModule(order, moduleID);

								testStatus = false;

							}

						}

					}
					sharedObject.sendJsonDataToAndroid(sensorDataVO);

				}///End SmartMode

				if (msg.contains("/ID:")) {
					st = new StringTokenizer(msg, " ");

					// ID가 IN 이면 값 추가
					if (msg.contains("IN")) {
						// 안드로이드일 경우 - /ID:ANDROID IN/ 이만큼 자르고
						// 아닐경우는 그대로 자르기

						this.moduleID = st.nextToken().replace("/ID:", "");

						// this.moduleID = msg.replace("/ID:ANDROID IN>", "");
						
						System.out.println("안드로이드 접속아이디" + this.moduleID);
						sharedObject.addClients(this.moduleID, this);
						sharedObject.sendJsonDataToAndroid(sensorDataVO);
						
						//안드로이드 접속할 때 불 켜기
						order = "WINDOW LIGHTON";
						sharedObject.sendTOModule(order, moduleID);
						
						continue;

					} else {
						// ID가 OUT이면 연결 끊기
						System.out.println("==========안드로이드 나간다======");
						sharedObject.removeClients(moduleID, this);
						continue;
					}

					// 센서 값 받아오기
					// 미세먼지
				} else if (msg.startsWith("/AIRPURIFIER")) {
					st = new StringTokenizer(msg, " ");
					System.out.println(st.toString());
					st.nextToken();
					sensorDataVO.setAirpurifierStatus(st.nextToken().replace("/airpurifierStatus:", ""));
					sensorDataVO.setDust25(st.nextToken().replace("/dust25:", ""));
					sensorDataVO.setDust10(st.nextToken().replace("/dust10:", ""));
					sensorDataVO.setGasStatus(st.nextToken().replace("/gasStatus:", ""));
					//가스 900 이상이면 창문 열기
					if(Integer.parseInt(sensorDataVO.getGasStatus()) >900) {
						System.out.println("AIRPURIFIER : ========가스 감지==========" + sensorDataVO.getGasStatus());
						order = "WINDOW ON";
						sharedObject.sendTOModule(order, moduleID);
					}

					// 에어컨
				} else if (msg.startsWith("/AIRCONDITIONER")) {
					System.out.println("============에어컨 MSG==========" + msg);
					st = new StringTokenizer(msg, " ");
					st.nextToken();
					sensorDataVO.setAirconditionerStatus(st.nextToken().replace("/airconditionerStatus:", ""));
					sensorDataVO.setTemp(st.nextToken().replace("/temp:", ""));
					sensorDataVO.setAirconditionerMode(st.nextToken().replace("/airconditionerMode:", ""));
					sensorDataVO.setAirconditionerTemp(st.nextToken().replace("/airconditionerTemp:", ""));
					sensorDataVO.setAirconditionerSpeed(st.nextToken().replace("/airconditionerSpeed:", ""));

					// 창문
				} else if (msg.startsWith("/WINDOW")) {
					st = new StringTokenizer(msg, " ");
					st.nextToken();
					sensorDataVO.setWindowStatus(st.nextToken().replace("/windowStatus:", ""));

					// 카메라 이미지 저장
				} else if (msg.contains("CAMERA")) {

					String order = msg.replace("/CAMERA>IMAGE ", "");
					sharedObject.saveImage(order);

					order = "MATLAB SEND";
					sharedObject.sendTOMatlab(order, moduleID);

				} else if (msg.contains("FACE")) {

					String order = msg.replace("/FACE ", "");
					sharedObject.checkFace(order);

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
