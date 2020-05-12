package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import model.TestVO;

public class MultiThreadRunnable implements Runnable {
	Socket socket;
	BufferedReader bufferedReader;
	PrintWriter printWriter;
	SharedObject sharedObject;
	TestVO vo;
	ObjectOutputStream objectOutputStream;
	StringTokenizer st;
	String moduleID;

	// client, module list 생성

	public String getModuleID() {
		return moduleID;
	}

	// Construction injection
	// Constructor - Socket과 공용객체를 답아와 초기화 해준다
	public MultiThreadRunnable(Socket socket, SharedObject sharedObject, TestVO vo,
			ObjectOutputStream objectOutputStream) {
		this.socket = socket;
		this.sharedObject = sharedObject;
		this.vo = vo;
		this.objectOutputStream = objectOutputStream;
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			this.printWriter = new PrintWriter(socket.getOutputStream());
			this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Client 로부터 넘어온 'MSG' 를 판별하여 기능 실행
		String msg = "";
		try {
			
			//안드로이드가 먼저 접속할땐 에러 x
			//안드로이드 list가 null인 상태에서 값을 넣어주면 에러가 뜸
			while ((msg = bufferedReader.readLine()) != null) {
				st = new StringTokenizer(msg, " ");

				System.out.println("test==" + msg);

				if (msg.contains("/ID:")) {
					System.out.println(msg);
					//msg = msg.replace("/ID:", "");
					
					//ID가 IN 이면
					if(msg.contains("IN")) {
						System.out.println("IN");
						this.moduleID = msg;
						System.out.println(msg);
						sharedObject.add(msg, this);
						continue;
						
					}
					//IT가 OUT이면 
					else {
						sharedObject.disconn(moduleID, MultiThreadRunnable.this);

						continue;
						
						
					}
				
					
					
				
				}else{
					vo.setTemp(st.nextToken().replace("/TEMPRATURE:", ""));
					vo.setLight(st.nextToken().replace("/LIGHT:", ""));
					vo.setOnOff(st.nextToken().replace("/ONOFF:", ""));
				
					sharedObject.send(socket,vo);
						
				}

				
				
//				vo.setTemp(msg.replaceFirst("/TEMPRATURE:", ""));
//				vo.setLight(msg.replaceFirst("/LIGHT:", ""));
//				vo.setOnOff(msg.replaceFirst("/ONOFF:", ""));
				System.out.println(vo.getTemp() + " : " + vo.getLight() + " : " + vo.getOnOff());
				
				
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
				socket.close();
				objectOutputStream.close();
				bufferedReader.close();
				printWriter.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
