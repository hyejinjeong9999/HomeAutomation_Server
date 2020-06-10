package server;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.SensorDataVO;

/// ------------공유객체
class SharedObject {
	String TAG = "SharedObject";
	Map<String, MultiThreadRunnable> clientList = new HashMap<String, MultiThreadRunnable>();
	ArrayList<MultiThreadRunnable> matlabList = new ArrayList<>();
	ArrayList<MultiThreadRunnable> moduleList = new ArrayList<>(); // 모듈들 저장

	// 들어올때
	public void addClients(String msg, MultiThreadRunnable list) {
		if (msg.contains("ANDROID")) {
			// 안드로이드로 접속하면 안드로이드list에 저장
			clientList.put(msg, list);
			System.out.println("client접속 : " + clientList.get(msg));
			
			
			
		} else if (msg.contains("MATLAB")) {
			matlabList.add(list);
			System.out.println("matlab 저장");
			System.out.println(matlabList.get(0).getModuleID());
//			matlabList
		} else {
			// 모듈로 접속시 모듈list에 저장
			moduleList.add(list);
			System.out.println("모듈 접속 : " + moduleList.get(0));
		}
	}

	// 접속종료 : clientList의 해당 runnable 삭제
	public void removeClients(String moduleID, MultiThreadRunnable runnable) {
		System.out.println("나가려는 ID " + moduleID);
		clientList.remove(moduleID);
		System.out.println("나간 후 클라이언트 리스트 개수" +clientList.size() );

	}

	public void saveImage(String ImageData) {
		System.out.println("ImaeData 값" + ImageData);

		byte getByte[] = Base64.decodeBase64(ImageData);

		try {

			FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\student\\Desktop\\images\\test.jpg");
			fileOutputStream.write(getByte);
			fileOutputStream.close();
			System.out.println("image 저장 완료");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("File_Exception==" + e);

		}
	}

	public synchronized void sendTOModule(String order) {
		
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
	public void sendJsonDataToAndroid(SensorDataVO sensorDataVO) {
		
			//System.out.println("-----------vo객체 전송 : ---------");
			if (clientList.size() > 0) {
				for (String key : clientList.keySet()) {
					
					ObjectMapper objectMapper = new ObjectMapper();
					String jsonData;
					try {
						jsonData = objectMapper.writeValueAsString(sensorDataVO);
						//System.out.println("JSON DATA==" + jsonData);
						
						clientList.get(key).getPrintWriter().println(jsonData);
						clientList.get(key).getPrintWriter().flush();
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		}
	}

}
