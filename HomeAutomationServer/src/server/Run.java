package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;




public class Run {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread t = new Thread(new MulticlientRunnable());
		t.start();

	}

}

class MulticlientRunnable implements Runnable { // runnable

	ServerSocket server;
	Socket socket;

	PrintWriter printWriter;
	BufferedReader bufferedReader;

	ExecutorService executorService;
	String msg;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			server = new ServerSocket(5555);

			while (true) {
				Socket s = server.accept();
				System.out.println("연결 성공");
				try {
					bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
					String str = bufferedReader.readLine();
					System.out.println(str);

					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),
							true);
					out.println("Server Received " + str);
					
					while (true) {
					    if ((msg = bufferedReader.readLine()) != null) {
					       System.out.println("Andrid Data==" +msg);
					    }
					 }

				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					s.close();
					System.out.println("S: Done.");
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
}
