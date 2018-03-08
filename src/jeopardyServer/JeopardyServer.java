package jeopardyServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import jeopardyForms.ConnectionStatusForm;

public class JeopardyServer {
	
	//number of players
	Integer numberOfPlayers;
	
	//Connection members
	ServerSocket[] playerServerSocket;
	Socket[] playerSocket;
	InputStream[] playerInput;
	OutputStream[] playerOutput;
	DataOutputStream[] playerDataOut;
	DataInputStream[] playerDataIn;
	ObjectOutputStream[] playerObjectOut;
	ObjectInputStream[] playerObjectIn;
	
	//Forms
	ConnectionStatusForm connectionStatuses;
	
	//Constructor, stage 1 initialization, connect to clients etc
	public JeopardyServer(Integer numPlayers) throws IOException {
		//number of players
		numberOfPlayers = numPlayers;
		
		//Connection member initialization
		playerServerSocket = new ServerSocket[numPlayers];
		playerSocket = new Socket[numPlayers];
		playerInput = new InputStream[numPlayers];
		playerOutput = new OutputStream[numPlayers];
		playerDataOut = new DataOutputStream[numPlayers];
		playerDataIn = new DataInputStream[numPlayers];
		playerObjectOut = new ObjectOutputStream[numPlayers];
		playerObjectIn = new ObjectInputStream[numPlayers];
		
		//Forms initialization
		connectionStatuses = new ConnectionStatusForm(numPlayers); 
		
		//ServerSocket initialization and binding
		playerServerSocket[0] = new ServerSocket(5557);
		//ServerSocket initialization, binding to port, binding to socket, binding to data stream, output stream, input stream
		for(int i = 0; i < numPlayers; i++) {
			System.out.println("Waiting on player: " + (i + 1));
			
			//Notify the players who they are waiting on
			for(int x = 0; x < i; x++) {
				playerDataOut[x].writeUTF("Waiting on player: " + (i + 1));
			}
			
			playerSocket[i] = playerServerSocket[0].accept();
			System.out.println("Player " + (i + 1) + " accepted");
		
			//In/out streams binding
			playerOutput[i] = playerSocket[i].getOutputStream();
			playerInput[i] = playerSocket[i].getInputStream();
			
			//Object streams
			playerObjectOut[i] = new ObjectOutputStream(playerSocket[i].getOutputStream());
			playerObjectIn[i] = new ObjectInputStream(playerSocket[i].getInputStream());
			
			//In/out data streams binding
			playerDataOut[i] = new DataOutputStream(playerOutput[i]);
			playerDataIn[i] = new DataInputStream(playerInput[i]);
			
			//Tell the player which one they are
			playerDataOut[i].writeUTF("Player" + (i + 1));
		}
		
		//Tell the players game has started
		for(int i = 0; i < numPlayers; i++) {
			playerDataOut[i].writeUTF("Game Started");
		}
		
		//STAGE TWO
		while(!gameOver) {
			//if no questions send gameOverForm
			//print question/answer
			//pass clients question and dollar amt
			while(!answer) {
				//receive answers from everyone + uid
				//if wrong do nothing
				//if right pass to all clients correct guess + uid
				//if right answer = true
			}
			//pass uid + dollar amt
		}
	}
}