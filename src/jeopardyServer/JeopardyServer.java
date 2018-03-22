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
import jeopardyForms.QuestionForm;

public class JeopardyServer {
	
	//number of players
	Integer numberOfPlayers;	
	//Connection members
	ServerSocket playerServerSocket;
	Socket[] playerSocket;
	InputStream[] playerInput;
	OutputStream[] playerOutput;
	DataOutputStream[] playerDataOut;
	DataInputStream[] playerDataIn;
	ObjectOutputStream[] playerObjectOut;
	ObjectInputStream[] playerObjectIn;
	static volatile int questionCounter = 0;
	
	//Forms
	
	//Questions
	QuestionForm[] questions;
	String[] answerBank;
	
	//Constructor, stage 1 initialization, connect to clients etc
	public JeopardyServer(Integer numPlayers) throws IOException {
		
		//ServerSocket initialization and binding
		playerServerSocket = new ServerSocket();		
		playerServerSocket = new ServerSocket(5557);
		
		while(true) {
		synchronized(this) {
			questionCounter = 0;
		}
		//number of players
		numberOfPlayers = numPlayers;
		
		//Defining questions
		questions = new QuestionForm[2];
		answerBank = new String[2];
		questions[0] = new QuestionForm();
		questions[1] = new QuestionForm();
		questions[0].dollarAmt = 200;
		questions[0].question = "What is Ammon's last name?";
		
		questions[1].dollarAmt = 300;
		questions[1].question = "What is Nathan's favorite Overwatch team that is garbage?";
		
		answerBank[0] = "Riley";
		answerBank[1] = "Dallas Fuel";
		
		//Connection member initialization
		playerSocket = new Socket[numPlayers];
		playerInput = new InputStream[numPlayers];
		playerOutput = new OutputStream[numPlayers];
		playerDataOut = new DataOutputStream[numPlayers];
		playerDataIn = new DataInputStream[numPlayers];
		playerObjectOut = new ObjectOutputStream[numPlayers];
		playerObjectIn = new ObjectInputStream[numPlayers];
		
		
		
		//ServerSocket initialization, binding to port, binding to socket, binding to data stream, output stream, input stream
		for(int i = 0; i < numPlayers; i++) {
			System.out.println("Waiting on player: " + (i + 1));
			
			//Notify the players who they are waiting on
			for(int x = 0; x < i; x++) {
				playerDataOut[x].writeUTF("Waiting on player: " + (i + 1));
			}
			
			System.out.println("waiting to accept network connection");
			playerSocket[i] = playerServerSocket.accept();
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
			playerDataOut[i].writeInt((i + 1));
		}
		
		System.out.println("questionCounter: " + questionCounter);
		
		//Tell the players game has started
		for(int i = 0; i < numPlayers; i++) {
			System.out.println("Game Started");
			playerDataOut[i].writeUTF("Game Started");
			playerDataOut[i].writeInt(numPlayers);
			playerDataOut[i].flush();
		}
		
		
		//STAGE TWO
		
		ClientListenerThread[] clients = new ClientListenerThread[numPlayers];
		//create threads that have access to the playerObjectOut and playerObjectIn objects
		for(int i = 0; i < numPlayers; i++) {
			clients[i] = new ClientListenerThread(playerObjectIn, numPlayers, i, playerObjectOut, questions, answerBank);
		}
		
		for(int i = 0; i < numPlayers; i++) {
			playerObjectOut[i].writeObject(questions[0]);
		}
		
		Thread[] clientThread = new Thread[numPlayers];
		
		for(int i = 0; i < numPlayers; i++) {
			clientThread[i] = new Thread(clients[i]);
		}
		
		for(int i = 0; i < numPlayers; i++) {
			clientThread[i].start();
		}
		
		//Wait for game to finish
		for(int i = 0; i < numPlayers; i++) {
			try {
				System.out.println("ServerMAIN: Waiting on join for " + i + " thread");
				clientThread[i].join();
				System.out.println("ServerMAIN: Successful join on " + i + " thread");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
		System.out.println("ServerMAIN: Game finished");
		
		//Close resources
		for(int i = 0; i < numPlayers; i++) {
			playerDataOut[i].close();
			playerDataIn[i].close();
			playerObjectOut[i].close();
			playerObjectIn[i].close();
			playerInput[i].close();
			playerOutput[i].close();
			playerSocket[i].close();
		}
		System.out.println("ServerMAIN: Ready for next game");
		}
	}
}