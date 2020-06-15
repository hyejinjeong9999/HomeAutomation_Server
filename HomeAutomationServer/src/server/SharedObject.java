package server;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dao.LogDAO;
import model.LogVO;
import model.SensorDataVO;

/// ------------공유객체
class SharedObject {
	String TAG = "SharedObject";
	Map<String, MultiThreadRunnable> clientList = new HashMap<String, MultiThreadRunnable>();
	ArrayList<MultiThreadRunnable> matlabList = new ArrayList<>();
	ArrayList<MultiThreadRunnable> moduleList = new ArrayList<>(); // 모듈들 저장
	double[] faceResult = new double[4];
	String email = "";
	LogDAO logDAO;
	LogVO logVO;

	SharedObject(LogDAO logDAO, LogVO logVO) {
		this.logDAO = logDAO;
		this.logVO = logVO;
	}

	// 들어올때
	public void addClients(String msg, MultiThreadRunnable list) {
		if (msg.contains("ANDROID")) {
			// 안드로이드로 접속하면 안드로이드list에 저장
			clientList.put(msg, list);
			System.out.println("client접속 : " + clientList.get(msg));
			email = msg;

		} else if (msg.contains("MATLAB")) {
			matlabList.clear();
			matlabList.add(list);
			System.out.println("matlab 저장");
			System.out.println(matlabList.get(0).getModuleID());
			System.out.println("addClients matlabSize" + matlabList.size());
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
		System.out.println("나간 후 클라이언트 리스트 개수" + clientList.size());

	}

	public void saveImage(String ImageData) {
		// System.out.println("ImaeData 값" + ImageData);

		byte getByte[] = Base64.decodeBase64(ImageData);

		try {

			FileOutputStream fileOutputStream = new FileOutputStream(
					"C:\\Users\\student\\Desktop\\project\\images\\test.jpg");
			// C:\Users\student\Desktop\project\images
			fileOutputStream.write(getByte);
			fileOutputStream.close();
			System.out.println("image 저장 완료");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("File_Exception==" + e);

		}
	}

	public void checkFace(String order) {
		System.out.println("checkFaceTest matlabSize" + matlabList.size());
		System.out.println(order);

		StringTokenizer st = new StringTokenizer(order, "     ");
		for (int i = 0; i < faceResult.length; i++) {
			faceResult[i] = Double.parseDouble(st.nextToken());
			System.out.println("checkFace" + faceResult[i]);

		}

		System.out.println("checkFace Test =====> " + faceResult[0]);

		for (int i = 0; i < faceResult.length; i++) {
			if (faceResult[i] >= 0.5) {

				System.out.println("checkFace Test 성공" + faceResult[i]);

				if (moduleList.size() > 0) {
					for (MultiThreadRunnable runnable : moduleList) {
						if (runnable.getModuleID().equals("WINDOW")) {
							runnable.getPrintWriter().println("ON");
							runnable.getPrintWriter().flush();

							logDAO.dbUpdate(email, "DOOR", Double.toString(faceResult[i]));
							///DB에 저장
						}
					}
				}

			} else
				// this.sendTOModule("FAIL", "WINDOW");
				System.out.println("checkFace Test 실패" + faceResult[i]);

		}

	}

	public void sendTOMatlab(String order, String moduleID) {

		System.out.println("sendTOMatlab order Test : " + order);

		StringTokenizer st = new StringTokenizer(order, " ");
		String keyword = st.nextToken().replace("/", "");
		String value = st.nextToken();
		System.out.println("sednTomatlabTest --->" + value + " : " + keyword);
		System.out.println("sednTomatlabTest --->모듈 개수 " + matlabList.size());
		if (matlabList.size() > 0) {
			System.out.println("sednTomatlabTest --->if문 진입 : size > 0");

			System.out.println("sednTomatlabTest ---> runnable get ModuleID" + matlabList.get(0).getModuleID());
			matlabList.get(0).getPrintWriter().println(value);
			matlabList.get(0).getPrintWriter().flush();

		}

	}

	public void sendTOModule(String order, String moduleID) {

		System.out.println("sendTOModule order Test : " + order);

		StringTokenizer st = new StringTokenizer(order, " ");
		String keyword = st.nextToken().replace("/", "");
		String value = st.nextToken();
		System.out.println("sednToModuleTest --->" + value + " : " + keyword);
		System.out.println("sednToModuleTest --->모듈 개수 " + moduleList.size());
		if (moduleList.size() > 0) {
			for (MultiThreadRunnable runnable : moduleList) {
				if (runnable.getModuleID().equals(keyword)) {
					System.out.println("sednToModuleTest ---> runnable get ModuleID" + runnable.getModuleID());
					runnable.getPrintWriter().println(value);
					runnable.getPrintWriter().flush();

					// 모듈 ON, OFF 일시 DB에 저장
					if (value.equals("ON") || value.equals("OFF")) {
						System.out.println("module if문");
						System.out.println("sendTOModule Test ModeulID = " + moduleID);
						for (String key : clientList.keySet()) {
							// System.out.println("sendTOModule Test" + key);
							if (key.equals(moduleID)) {

								logDAO.dbUpdate(moduleID, keyword, value);
								// String mail, String module, String onOff

							}

						}
					}

				}
			}
		}

	}

	// Weather json 형태로 바꿔서 안드로이드로 보내는 send 함수
	public void sendJsonDataToAndroid(SensorDataVO sensorDataVO) {

		// System.out.println("-----------vo객체 전송 : ---------");
		if (clientList.size() > 0) {
			for (String key : clientList.keySet()) {

				ObjectMapper objectMapper = new ObjectMapper();
				String jsonData;
				try {
					jsonData = objectMapper.writeValueAsString(sensorDataVO);
					System.out.println("JSON DATA==" + jsonData);

					clientList.get(key).getPrintWriter().println(jsonData);
					clientList.get(key).getPrintWriter().flush();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public void sendLogDataToAndroid() {

		System.out.println("-----------Log데이터 전송 ---------");
		System.out.println("sendLogDataToAndroid Test" + clientList.size());
		if (clientList.size() > 0) {
			for (String key : clientList.keySet()) {

				ObjectMapper objectMapper = new ObjectMapper();
				String jsonData;
				try {
					jsonData = objectMapper.writeValueAsString(logVO);
					System.out.println("sendLogDataToAndroid Test== " + jsonData);

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
