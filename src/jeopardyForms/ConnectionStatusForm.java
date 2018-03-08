package jeopardyForms;

public class ConnectionStatusForm implements JeopardyForm {

	public boolean[] isConnected;
	
	public ConnectionStatusForm(Integer numPlayers) {
		isConnected = new boolean[numPlayers];
	}
	
}
