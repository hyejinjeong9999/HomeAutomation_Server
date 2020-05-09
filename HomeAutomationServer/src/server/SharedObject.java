package server;

import java.util.ArrayList;
import java.util.LinkedList;

/// ------------공유객체
class SharedObject {
	String TAG = "SharedObject";
	Object monitor = new Object();
	private LinkedList<String> dataList = new LinkedList<>();
	ArrayList<MultiThreadRunnable> clientList = new ArrayList<>();
	ArrayList<MultiThreadRunnable> moduleList = new ArrayList<>();

	public void add(String msg, MultiThreadRunnable list) {
		if (msg.contains("/Android")) {
			clientList.add(list);
			System.out.println(clientList.get(0));
		}
		
		else {
			moduleList.add(list);
		}
	}

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