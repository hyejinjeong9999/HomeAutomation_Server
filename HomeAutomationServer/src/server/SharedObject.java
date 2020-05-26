package server;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.SensorDataVO;

/// ------------공유객체
class SharedObject {
	String TAG = "SharedObject";

	ArrayList<MultiThreadRunnable> clientList = new ArrayList<>(); // 안드로이드 저장
	ArrayList<MultiThreadRunnable> moduleList = new ArrayList<>(); // 모듈들 저장

	// 들어올때
	public void add(String msg, MultiThreadRunnable list) {
		if (msg.contains("ANDROID")) {
			// 안드로이드로 접속하면 안드로이드list에 저장
			clientList.add(list);
			System.out.println(clientList.get(0));

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
					System.out.println("sednToModuleTest ---> runnable get ModuleID" + runnable.getModuleID());
					runnable.getPrintWriter().println(value);
					runnable.getPrintWriter().flush();
				} else
					System.out.println("sednToModuleTest ---> 전송못함");
			}

		}

	}

	// json 형태로 바꿔서 안드로이드로 보내는 send 함수
	public void send(SensorDataVO sensorDataVO) {
		System.out.println("-----------vo객체 전송 : ---------");
		if (clientList.size() > 0) {
			for (MultiThreadRunnable runnable : clientList) {
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonData;
				try {
					jsonData = objectMapper.writeValueAsString(sensorDataVO);
					System.out.println("JSON DATA==" + jsonData);
					runnable.getPrintWriter().println(jsonData);
					runnable.getPrintWriter().flush();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
