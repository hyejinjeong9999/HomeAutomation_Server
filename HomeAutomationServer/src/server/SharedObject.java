package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import model.TestVO;

/// ------------공유객체
class SharedObject {
	String TAG = "SharedObject";
	Object monitor = new Object();
	private LinkedList<String> dataList = new LinkedList<>();
	ArrayList<MultiThreadRunnable> clientList = new ArrayList<>();

	


	public void add(MultiThreadRunnable list) {
		clientList.add(list);
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