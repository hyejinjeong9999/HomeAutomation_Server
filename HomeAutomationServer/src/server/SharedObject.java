package server;

import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.TestVO;

/// ------------공유객체
class SharedObject {
	String TAG = "SharedObject";
	Object monitor = new Object();
	private LinkedList<String> dataList = new LinkedList<>();
	ArrayList<MultiThreadRunnable> clientList = new ArrayList<>(); // 안드로이드 저장
	ArrayList<MultiThreadRunnable> moduleList = new ArrayList<>(); // 모듈들 저장

	// 들어올때
	public void add(String msg, MultiThreadRunnable list) {
		if (msg.contains("ANDROID")) {
			// 안드로이드로 접속하면 안드로이드list에 저장
			clientList.add(list);
			System.out.println(clientList.get(0));
			// System.out.println(clientList.get(0).getModuleID());

		}

		else {
			// 모듈로 접속시 모듈list에 저장
			moduleList.add(list);
			System.out.println(moduleList.get(0));
		}
	}

	// 나갈때
	public void disconn(String moduleID, MultiThreadRunnable runnable) {
		System.out.println("삭제하는것 확인");
		System.out.println("이전 사이즈 : " + clientList.size());
		clientList.remove(runnable);
		System.out.println("삭제 후 사이즈 :"+clientList.size());
	}

	// json 형태로 바꿔서 안드로이드로 보내는 send 함수
	public void send(Socket socket, TestVO vo) {
		PrintWriter printWriter;
		if (clientList != null) {
			try {
				for (int i = 0; i < clientList.size(); i++) {

					printWriter = new PrintWriter(clientList.get(i).socket.getOutputStream());
					System.out.println(clientList.size());
					ObjectMapper objectMapper = new ObjectMapper();
					String jsonData = objectMapper.writeValueAsString(vo);
					System.out.println("JSON DATA==" + jsonData);
					printWriter.println(jsonData);
					printWriter.flush();
				}
			} catch (SocketException e) {
//	                //ServerSocket가 종료되었을 때 실행됩니다.
	                System.out.println("ServerSocket 닫힘!");

			} catch (Exception e) {
				System.out.println("접속안됭..");
				e.printStackTrace();
			}

		}

	}

//		
//		try {
//			printWriter = new PrintWriter(socket.getOutputStream());
//			ObjectMapper objectMapper = new ObjectMapper();
//			String jsonData = objectMapper.writeValueAsString(vo);
//			System.out.println("JSON DATA==" + jsonData);
//			printWriter.println(jsonData);
//			printWriter.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//	}

	// send 함수 생성
	// json 형태로 바꿔서 보내기

	public void put(String msg) {
		synchronized (monitor) {

			dataList.addLast(msg);
			monitor.notify();

		}
	}

	public String pop() {
		String result = "";
		synchronized (monitor) {
			if (dataList.isEmpty()) {
				try {
					monitor.wait();
					result = dataList.removeFirst();
				} catch (InterruptedException e) {

				}
			} else {
				result = dataList.removeFirst();
			}
		}
		return result;
	}
}
//------------공유객체 끝