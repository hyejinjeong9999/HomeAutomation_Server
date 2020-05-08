package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JTable.PrintMode;

import model.TestVO;

public class MultiThreadRunnable implements Runnable {
	Socket socket;
	BufferedReader bufferedReader;
	PrintWriter printWriter;
	SharedObject sharedObject;
	TestVO vo;
	ObjectOutputStream objectOutputStream;

	// Construction injection
	// Constructor - Socket과 공용객체를 답아와 초기화 해준다
	public MultiThreadRunnable(Socket socket, SharedObject sharedObject, TestVO vo, ObjectOutputStream objectOutputStream) {
		
		this.socket = socket;
		this.sharedObject = sharedObject;
		this.vo = vo;
		this.objectOutputStream = objectOutputStream;
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
			while ((msg = bufferedReader.readLine()) != null) {
//				msg = bufferedReader.readLine();
				System.out.println(msg);
//				if (msg.contains("1TEMPRATURE"))
//					vo.setTemp1(msg.replaceFirst("/1TEMPRATURE", ""));
//				if (msg.contains("2TEMPRATURE"))
//					vo.setTemp1(msg.replaceFirst("/2TEMPRATURE", ""));
//				if (msg.contains("3TEMPRATURE"))
//					vo.setTemp1(msg.replaceFirst("/3TEMPRATURE", ""));
//				
//				if(msg.contains("1TEMPRATURE")) {
//					System.out.println("넘어가는 객체 : " + vo.getTemp1());
//					try {
//						objectOutputStream.writeObject(vo);
//						objectOutputStream.flush();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
				
				
				vo.setTemp1(msg.replaceFirst("/1TEMPRATURE", ""));
				objectOutputStream.writeObject(vo);
				objectOutputStream.flush();

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
