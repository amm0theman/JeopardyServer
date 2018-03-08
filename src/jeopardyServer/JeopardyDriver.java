package jeopardyServer;

import java.io.IOException;

public class JeopardyDriver {
	public static void main(String args[]) {
		try {
			JeopardyServer gameServer = new JeopardyServer(3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
