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

	AirconditionerVO airconditionerVO = new AirconditionerVO();
	AirpurifierVO airpurifierVO = new AirpurifierVO();
	DoorVO doorVO = new DoorVO();
	WindowVO windowVO = new WindowVO();
	LightVO lightVO = new LightVO();
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

		String airconditionerSql = "SELECT AIRCONDITIONER_STATUS, AIRCONDITIONER_TIME " + "FROM AIRCONDITIONER "
				+ "WHERE ROWNUM <=10 " + "ORDER BY AIRCONDITIONER_TIME DESC";

		String airpurifierSql = "SELECT AIRPURIFIER_STATUS, AIRPURIFIER_TIME " + "FROM AIRPURIFIER "
				+ "WHERE ROWNUM <=10 " + "ORDER BY AIRPURIFIER_TIME DESC";

		String doorSql = "SELECT DOOR_STATUS, DOOR_TIME " + "FROM DOOR " + "WHERE ROWNUM <=10 "
				+ "ORDER BY DOOR_TIME DESC";

		String windowSql = "SELECT WINDOW_STATUS, WINDOW_TIME " + "FROM WINDOW " + "WHERE ROWNUM <=10 "
				+ "ORDER BY WINDOW_TIME DESC";

		String lightSql = "SELECT LIGHT_STATUS, LIGHT_TIME " + "FROM LIGHT " + "WHERE ROWNUM <=10 "
				+ "ORDER BY LIGHT_STATUS DESC";
		System.out.println("sql check=======>" + airconditionerSql);
		System.out.println("sql check=======>" + airpurifierSql);
		System.out.println("sql check=======>" + doorSql);
		System.out.println("sql check=======>" + windowSql);
		System.out.println("sql check=======>" + lightSql);
		try {
			// 3. Statement 생성
			ps = con.prepareStatement(airconditionerSql);

			// 4. SQL 수행
			rs = ps.executeQuery();

			// 5. 결과 처리
			// select 구문의 결과값은 Result set, DML의 결과값은 int값으로 나온다.
			while (rs.next()) {

				airconditionerVO.setAirconditionerStatus(rs.getString("AIRCONDITIONER_STATUS"));
				airconditionerVO.setAirconditionerTime(rs.getString("AIRCONDITIONER_TIME"));
				airconditionerList.add(airconditionerVO);
				logVO.setAirconditionerList(airconditionerList);

			}

			ps = con.prepareStatement(airpurifierSql);
			rs = ps.executeQuery();
			while (rs.next()) {

				airpurifierVO.setAirpurifierStatus(rs.getString("AIRPURIFIER_STATUS"));
				airpurifierVO.setAirpurifierTime(rs.getString("AIRPURIFIER_TIME"));
				airpurifierList.add(airpurifierVO);
				logVO.setAirpurifierList(airpurifierList);

			}

			ps = con.prepareStatement(doorSql);
			rs = ps.executeQuery();
			while (rs.next()) {

				doorVO.setDoorStatus(rs.getString("DOOR_STATUS"));
				doorVO.setDoorTime(rs.getString("DOOR_TIME"));
				doorList.add(doorVO);
				logVO.setDoorList(doorList);

			}

			ps = con.prepareStatement(windowSql);
			rs = ps.executeQuery();
			while (rs.next()) {

				windowVO.setWindowStatus(rs.getString("WINDOW_STATUS"));
				windowVO.setWindowTime(rs.getString("WINDOW_TIME"));
				windowList.add(windowVO);
				logVO.setWindowList(windowList);

			}

			ps = con.prepareStatement(lightSql);
			rs = ps.executeQuery();
			while (rs.next()) {

				lightVO.setLightStatus(rs.getString("LIGHT_STATUS"));
				lightVO.setLightTime(rs.getString("LIGHT_TIME"));
				lightList.add(lightVO);
				logVO.setLightList(lightList);

			}

			// System.out.println(con);

			for (AirconditionerVO vo : logVO.getAirconditionerList()) {
				System.out.println("=====DBSELECT  Airconditioner TEST=====");
				System.out.print(vo.getAirconditionerStatus());
				System.out.print(vo.getAirconditionerTime());
				System.out.println();
			}

			for (AirpurifierVO vo : logVO.getAirpurifierList()) {
				System.out.println("=====DBSELECT Airpurifier TEST=====");
				System.out.print(vo.getAirpurifierStatus());
				System.out.print(vo.getAirpurifierTime());
				System.out.println();
			}
			
			for (DoorVO vo : logVO.getDoorList()) {
				System.out.println("=====DBSELECT Door TEST=====");
				System.out.print(vo.getDoorStatus());
				System.out.print(vo.getDoorTime());
				System.out.println();
			}
			
			for (LightVO vo : logVO.getLightList()) {
				System.out.println("=====DBSELECT LIGHT TEST=====");
				System.out.print(vo.getLightStatus());
				System.out.print(vo.getLightTime());
				System.out.println();
			}
			
			for (WindowVO vo : logVO.getWindowList()) {
				System.out.println("=====DBSELECT WINDOW TEST=====");
				System.out.print(vo.getWindowStatus());
				System.out.print(vo.getWindowTime());
				System.out.println();
			}

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
