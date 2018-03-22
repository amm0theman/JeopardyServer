package jeopardyServer;

import java.io.IOException;

public class JeopardyDriver {
	public static void main(String args[]) {
		try {
			@SuppressWarnings("unused")
			JeopardyServer gameServer = new JeopardyServer(2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
