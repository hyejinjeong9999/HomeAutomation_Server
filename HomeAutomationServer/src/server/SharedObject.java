package server;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.TestVO;

/// ------------공유객체
class SharedObject {
	String TAG = "SharedObject";
//	Object monitor = new Object();
//	private LinkedList<String> dataList = new LinkedList<>();
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

	// 접속종료 : clientList의 해당 runnable 삭제
	public void disconn(String moduleID, MultiThreadRunnable runnable) {

		clientList.remove(runnable);

	}

	public void sendTOModule(String order) {
		System.out.println("order Test : " + order);

		StringTokenizer st = new StringTokenizer(order, " ");
		String keyword = st.nextToken().replace("/", "");
		String value = st.nextToken();
		System.out.println("sednToModuleTest --->" + value + " : " + keyword);
		System.out.println(moduleList.size());
		if (moduleList.size() > 0) {

			for (MultiThreadRunnable runnable : moduleList) {

				if (runnable.getModuleID().equals(keyword)) {
					System.out.println("sednToModuleTest ---> if문 통과 ");
					System.out.println("sednToModuleTest ---> runnable get ModuleID"+ runnable.getModuleID());
					runnable.getPrintWriter().println(value);
					runnable.getPrintWriter().flush();
				}
				else System.out.println("sednToModuleTest ---> 전송못함");
			}

			////////////

			/*
			 * for (int i = 0; i < moduleList.size(); i++) { if
			 * (moduleList.get(i).equals(keyword)) { try { printWriter = new
			 * PrintWriter(moduleList.get(i).socket.getOutputStream());
			 * System.out.println(value + " : " + keyword);
			 * 
			 * printWriter.println(value); printWriter.flush(); } catch (IOException e) {
			 * 
			 * e.printStackTrace(); }
			 * 
			 * } }
			 */

		}

		/*
		 * if (moduleList != null) { try { for (int i = 0; i < clientList.size(); i++) {
		 * 
		 * printWriter = new PrintWriter(clientList.get(i).socket.getOutputStream());
		 * System.out.println(clientList.size()); ObjectMapper objectMapper = new
		 * ObjectMapper(); String jsonData = objectMapper.writeValueAsString(vo);
		 * System.out.println("JSON DATA==" + jsonData); printWriter.println(jsonData);
		 * printWriter.flush(); } } catch (SocketException e) {
		 * 
		 * System.out.println("ServerSocket 닫힘!");
		 * 
		 * }
		 */

	}

	// json 형태로 바꿔서 안드로이드로 보내는 send 함수
	public void send(TestVO vo) {
		System.out.println("send()---------");
		if (clientList.size() > 0) {
			for (MultiThreadRunnable runnable : clientList) {
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonData;
				try {
					jsonData = objectMapper.writeValueAsString(vo);
					System.out.println("JSON DATA==" + jsonData);
					runnable.getPrintWriter().println(jsonData);
					runnable.getPrintWriter().flush();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

//		PrintWriter printWriter;
//		if (clientList.size() < 0) {
//			try {
//
//				for (int i = 0; i < clientList.size(); i++) {
//
//					printWriter = new PrintWriter(clientList.get(i).socket.getOutputStream());
//					System.out.println(clientList.size());
//					ObjectMapper objectMapper = new ObjectMapper();
//					String jsonData = objectMapper.writeValueAsString(vo);
//					System.out.println("JSON DATA==" + jsonData);
//					printWriter.println(jsonData);
//					printWriter.flush();
//				}
//			}
////			catch (SocketException e) {
////				e.printStackTrace();
////				System.out.println("ServerSocket 닫힘!");
////
////			} 
//			catch (Exception e) {
//				System.out.println("접속안됭..");
//				e.printStackTrace();
//			}
//
//		}

	}

	//
//	public void put(String msg) {
//		synchronized (monitor) {
//
//			dataList.addLast(msg);
//			monitor.notify();
//
//		}
//	}
//
//	public String pop() {
//		String result = "";
//		synchronized (monitor) {
//			if (dataList.isEmpty()) {
//				try {
//					monitor.wait();
//					result = dataList.removeFirst();
//				} catch (InterruptedException e) {
//
//				}
//			} else {
//				result = dataList.removeFirst();
//			}
//		}
//		return result;
//	}
}
//------------공유객체 끝