package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.StringTokenizer;

import model.SensorDataVO;

public class MultiThreadRunnable implements Runnable {
	Socket socket;
	BufferedReader bufferedReader;
	PrintWriter printWriter;
	SharedObject sharedObject;
	SensorDataVO sensorDataVO;

	StringTokenizer st;
	String moduleID;

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

	@Override
	public synchronized void run() {
		// Client 로부터 넘어온 'MSG' 를 판별하여 기능 실행
		String msg = "";
		try {

			// 값들을
			while ((msg = bufferedReader.readLine()) != null) {

				System.out.println("test==" + msg);

				if (msg.contains("ANDROID>")) {
					String order = msg.replace("/ANDROID>", "");
					/// ANDROID>/MODE SMART -> /MODE SMART
					if (msg.contains("/MODE")) {

						// mode 뒤에 값을 알아낸담에 vo - mode 저장, 각 모드별로 알고리즘처리

						if (msg.contains("SMART")) {
							// smart 모드
							sensorDataVO.setMode("SMART");
						} else if (msg.contains("MENUAL")) {
							// 수동모드
							sensorDataVO.setMode("MENUAL");
						} else if (msg.contains("VENTILATION")) {
							// 공기청정기 모드
							sensorDataVO.setMode("VENTILATION");
						}

					}

					// 센서에게 값 전송
					else
						sharedObject.sendTOModule(order);
				}

				if (msg.contains("/ID:")) {
					st = new StringTokenizer(msg, " ");

					// ID가 IN 이면 값 추가
					if (msg.contains("IN")) {
						this.moduleID = st.nextToken().replace("/ID:", "");
						System.out.println(this.moduleID);
						sharedObject.add(msg, this);
						continue;

					} else {
						// ID가 OUT이면 연결 끊기
						sharedObject.disconn(moduleID, this);
						continue;
					}
					// 라떼판다 전송
				} else if (msg.contains("TEMPRATURE") || msg.contains("LIGHT") || msg.contains("ONOFF")
						|| msg.contains("dustDensity")) {
					st = new StringTokenizer(msg, " ");
					sensorDataVO.setTemp(st.nextToken().replace("/TEMPRATURE:", ""));
					sensorDataVO.setWindowStatus(st.nextToken().replace("/ONOFF:", ""));
					sensorDataVO.setDustDensity(st.nextToken().replace("/dustDensity:", ""));
					sharedObject.send(sensorDataVO);

				}

			}
		} catch (IOException e) {
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
