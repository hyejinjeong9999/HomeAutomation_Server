package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.StringTokenizer;

import model.TestVO;

public class MultiThreadRunnable implements Runnable {
	Socket socket;
	BufferedReader bufferedReader;
	PrintWriter printWriter;
	SharedObject sharedObject;
	TestVO vo;

	StringTokenizer st;
	String moduleID;

	// client, module list 생성

	public String getModuleID() {
		return moduleID;
	}

	// Construction injection
	// Constructor - Socket과 공용객체를 답아와 초기화 해준다

	// 안드로이드에 값 전달
	public MultiThreadRunnable(Socket socket, SharedObject sharedObject, TestVO vo,
			ObjectOutputStream objectOutputStream) {
		this.socket = socket;
		this.sharedObject = sharedObject;
		this.vo = vo;

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

					}
					// ID가 OUT이면 연결 끊기
					else {
						sharedObject.disconn(moduleID, MultiThreadRunnable.this);
						continue;
					}
					// 라떼판다 전송
				} else if (msg.contains("TEMPRATURE") || msg.contains("LIGHT") || msg.contains("ONOFF")||msg.contains("dustDensity")) {
					st = new StringTokenizer(msg, " ");
					vo.setTemp(st.nextToken().replace("/TEMPRATURE:", ""));
					vo.setLight(st.nextToken().replace("/LIGHT:", ""));
					vo.setOnOff(st.nextToken().replace("/ONOFF:", ""));
					vo.setDustDensity(st.nextToken().replace("/dustDensity:", ""));
					sharedObject.send(vo);

				}



//				ObjectMapper objectMapper = new ObjectMapper();
//				String jsonData = objectMapper.writeValueAsString(vo);
//				System.out.println("JSON DATA==" + jsonData);
//				printWriter.println(jsonData);
//				printWriter.flush();

			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				// socket.close();

				bufferedReader.close();
				printWriter.close();
//				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public PrintWriter getPrintWriter() {
		return printWriter;
	}

}
