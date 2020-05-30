package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.SensorDataVO;

public class NetworkSocketServer extends Application {
	private TextArea ta;
	private Button btn;
	private ServerSocket serverSocket;
	Socket socket;
	private BufferedReader bufferedReader;
	MultiThreadRunnable runnable;
	SharedObject sharedObject = new SharedObject();
	SensorDataVO vo = new SensorDataVO();
	ObjectOutputStream objectOutputStream;
	Object obj = new Object();
	String ml = "MATLAB"; 

	// private BufferedWriter socketBW;

	ExecutorService executorService = Executors.newCachedThreadPool();

	private void printMSG(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}

	@SuppressWarnings("unchecked ")
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500); // 가로, 세로 px 단위

		ta = new TextArea(); // 글상자를 생성
		root.setCenter(ta); // borderPane 가운데에 TextArea 부착

		btn = new Button("서버 기동");
		btn.setPrefSize(250, 50);
		btn.setOnAction(e -> {
			Runnable r = new Runnable() {
				
				public void run() {
					try {
						serverSocket = new ServerSocket(1357);
						printMSG("연결성공");
						while (true) {
							synchronized (obj) {
								socket = serverSocket.accept();
								printMSG(socket.getInetAddress().toString());
								
								//matlab일 경우 
								if(socket.getInetAddress().toString().equals("/70.12.60.91")) {
									System.out.println("matlap runnable");
									runnable = new MultiThreadRunnable(socket, sharedObject, vo, objectOutputStream, ml);
								//아닌 경우
								}else {
									runnable = new MultiThreadRunnable(socket, sharedObject, vo, objectOutputStream);
								}
								executorService.execute(runnable);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("IOException=="+e);
					}
				}
			};
			executorService.execute(r);
		});

		FlowPane flowPane = new FlowPane();
		flowPane.setPrefSize(700, 50);
		flowPane.getChildren().add(btn); // FlowPane에 버튼을 부착

		root.setBottom(flowPane); // 전체화면에 아래부분에 FlowPane 부착

		Scene scene = new Scene(root); // borderPane을 포함하는 장명 생성
		primaryStage.setScene(scene); // windows의 화면을 scene으로 설정
		primaryStage.setTitle("자바 네트워크서버");
		primaryStage.setOnCloseRequest(e -> {
			System.exit(0);
		});
		primaryStage.show();

	}

	public static void main(String[] args) {

		launch();

	}

}
