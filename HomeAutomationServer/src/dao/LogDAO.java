package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.AirconditionerVO;
import model.AirpurifierVO;
import model.DoorVO;
import model.LightVO;
import model.LogVO;
import model.WindowVO;

public class LogDAO {
	String user;
	String pwd;
	String driver;
	String url;
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;


	


	LogVO logVO;

	ArrayList<AirconditionerVO> airconditionerList = new ArrayList<AirconditionerVO>();
	ArrayList<AirpurifierVO> airpurifierList = new ArrayList<AirpurifierVO>();
	ArrayList<DoorVO> doorList = new ArrayList<DoorVO>();
	ArrayList<WindowVO> windowList = new ArrayList<WindowVO>();
	ArrayList<LightVO> lightList = new ArrayList<LightVO>();

	public LogDAO(LogVO logVO) {
		this.logVO = logVO;
	}

	public void dbConn() {
		// DB 연결하는 메서드

		System.out.println("======== JDBC CONN START ==========");
		user = "home";
		pwd = "home";
		driver = "oracle.jdbc.OracleDriver";
		url = "jdbc:oracle:thin:@127.0.0.1:1521:XE";

		try {
			// 1. Driver Loading
			// 객체생성해서 메모리에 띄운다. new를 사용하면 Compile타이밍에 결정되고 여러개의 객체가 올라갈 수 있기 때문에
			// Class.forName()을 사용.
			Class.forName(driver);

			// 2. Connection 요청
			con = DriverManager.getConnection(url, user, pwd);
		} catch (Exception e) {
			// 6. SQLException
			System.out.println(e);

		}
		System.out.println("=========JDBC CONN SUCCESS ========");
	}

	public void dbUpdate(String mail, String module, String onOff) {
		// DB 값 추가 하는 메서드
		System.out.println("======== DB UPDATE START ==========");

		String sql = "INSERT INTO " + module + " VALUES ('" + mail + "','" + onOff + "', SYSDATE)";

		// "INSERT AIRCONDITIONER VALUES('123456@naver.com','ON',SYSDATE)";
		System.out.println("sql check=======>" + sql);

		try {
			// 3. Statement 생성
			ps = con.prepareStatement(sql);

			// 4. SQL 수행
			rs = ps.executeQuery();
			con.commit();

		} catch (Exception e) {
			// 6. SQLException
			System.out.println(e);

		}

		System.out.println("======== DB UPDATE SUCCESS ==========");
	}

	public void dbSelect() {

		// DB 값 추가 하는 메서드
		System.out.println("======== DB SELECT START ==========");

		String airconditionerSql = "SELECT AIRCONDITIONER_STATUS, AIRCONDITIONER_TIME FROM "
				+ "(SELECT AIRCONDITIONER_STATUS, AIRCONDITIONER_TIME" + " FROM AIRCONDITIONER"
				+ " ORDER BY AIRCONDITIONER_TIME DESC)" + " WHERE ROWNUM <= 10";

		String airpurifierSql = "SELECT AIRPURIFIER_STATUS, AIRPURIFIER_TIME FROM "
				+ "(SELECT AIRPURIFIER_STATUS, AIRPURIFIER_TIME" + " FROM AIRPURIFIER"
				+ " ORDER BY AIRPURIFIER_TIME DESC)" + " WHERE ROWNUM <= 10";

		String doorSql = "SELECT DOOR_STATUS, DOOR_TIME FROM " + "(SELECT DOOR_STATUS, DOOR_TIME" + " FROM DOOR"
				+ " ORDER BY DOOR_TIME DESC)" + " WHERE ROWNUM <= 10";
		String windowSql = "SELECT WINDOW_STATUS, WINDOW_TIME FROM " + "(SELECT WINDOW_STATUS, WINDOW_TIME"
				+ " FROM WINDOW" + " ORDER BY WINDOW_TIME DESC)" + " WHERE ROWNUM <= 10";

		String lightSql = "SELECT LIGHT_STATUS, LIGHT_TIME FROM " + "(SELECT LIGHT_STATUS, LIGHT_TIME" + " FROM LIGHT"
				+ " ORDER BY LIGHT_TIME DESC)" + " WHERE ROWNUM <= 10";

//		System.out.println("sql check=======>" + airconditionerSql);
//		System.out.println("sql check=======>" + airpurifierSql);
//		System.out.println("sql check=======>" + doorSql);
//		System.out.println("sql check=======>" + windowSql);
//		System.out.println("sql check=======>" + lightSql);
		try {
			// 3. Statement 생성
			ps = con.prepareStatement(airconditionerSql);

			// 4. SQL 수행
			rs = ps.executeQuery();

			// 5. 결과 처리
			// select 구문의 결과값은 Result set, DML의 결과값은 int값으로 나온다.
			while (rs.next()) {
				AirconditionerVO airconditionerVO = new AirconditionerVO();

				airconditionerVO.setAirconditionerStatus(rs.getString("AIRCONDITIONER_STATUS"));
				airconditionerVO.setAirconditionerTime(rs.getString("AIRCONDITIONER_TIME"));
				airconditionerList.add(airconditionerVO);
				logVO.setAirconditionerList(airconditionerList);

			}

			ps = con.prepareStatement(airpurifierSql);
			rs = ps.executeQuery();

			while (rs.next()) {
				AirpurifierVO airpurifierVO = new AirpurifierVO();
				airpurifierVO.setAirpurifierStatus(rs.getString("AIRPURIFIER_STATUS"));
				airpurifierVO.setAirpurifierTime(rs.getString("AIRPURIFIER_TIME"));
				airpurifierList.add(airpurifierVO);
				logVO.setAirpurifierList(airpurifierList);

			}

			ps = con.prepareStatement(doorSql);
			rs = ps.executeQuery();
			while (rs.next()) {
				DoorVO doorVO = new DoorVO();
				doorVO.setDoorStatus(rs.getString("DOOR_STATUS"));
				doorVO.setDoorTime(rs.getString("DOOR_TIME"));
				doorList.add(doorVO);
				logVO.setDoorList(doorList);

			}

			ps = con.prepareStatement(windowSql);
			rs = ps.executeQuery();
			while (rs.next()) {
				WindowVO windowVO = new WindowVO();
				windowVO.setWindowStatus(rs.getString("WINDOW_STATUS"));
				windowVO.setWindowTime(rs.getString("WINDOW_TIME"));
				windowList.add(windowVO);
				logVO.setWindowList(windowList);
			}

			for (int i = 0; i < windowList.size(); i++) {
				System.out.println("windowList==  " + windowList.get(i).getWindowTime());
				System.out.println("logVO.windowList==  " + logVO.getWindowList().get(i).getWindowTime());
			}

			// logVO.setWindowList(windowList)

			ps = con.prepareStatement(lightSql);
			rs = ps.executeQuery();
			while (rs.next()) {

				LightVO lightVO = new LightVO();
				lightVO.setLightStatus(rs.getString("LIGHT_STATUS"));
				lightVO.setLightTime(rs.getString("LIGHT_TIME"));
				lightList.add(lightVO);
				logVO.setLightList(lightList);

			}

			// System.out.println(con);

//			System.out.println("=====DBSELECT  Airconditioner TEST=====");
//			for (AirconditionerVO vo : logVO.getAirconditionerList()) {
//				System.out.print(vo.getAirconditionerStatus());
//				System.out.print(vo.getAirconditionerTime());
//				System.out.println();
//			}
//
//			System.out.println("=====DBSELECT Airpurifier TEST=====");
//			for (AirpurifierVO vo : logVO.getAirpurifierList()) {
//				System.out.print(vo.getAirpurifierStatus());
//				System.out.print(vo.getAirpurifierTime());
//				System.out.println();
//			}
//
//			System.out.println("=====DBSELECT Door TEST=====");
//			for (DoorVO vo : logVO.getDoorList()) {
//				System.out.print(vo.getDoorStatus());
//				System.out.print(vo.getDoorTime());
//				System.out.println();
//			}
//
//			System.out.println("=====DBSELECT LIGHT TEST=====");
//			for (LightVO vo : logVO.getLightList()) {
//				System.out.print(vo.getLightStatus());
//				System.out.print(vo.getLightTime());
//				System.out.println();
//			}
//
//			System.out.println("=====DBSELECT WINDOW TEST=====");
//			for (WindowVO vo : logVO.getWindowList()) {
//				System.out.print(vo.getWindowStatus());
//				System.out.print(vo.getWindowTime());
//				System.out.println();
//			}

		} catch (Exception e) {
			// 6. SQLException
			System.out.println(e);
//		} finally {
//			// 7. 자원 정리
//			try {
//				if (rs != null)
//					rs.close();
//				if (ps != null)
//					ps.close();
//				if (con != null)
//					con.close();
//			} catch (Exception e2) {
//				System.out.println(e2);
//			}
		}

		System.out.println("======== DB SELECT SUCCESS ==========");

	}

}
