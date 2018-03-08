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

import jeopardyForms.AnswerForm;
import jeopardyForms.GuessForm;
import jeopardyForms.QuestionForm;

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
	
	//Questions
	QuestionForm[] questions;
	String[] answerBank;
	
	//Constructor, stage 1 initialization, connect to clients etc
	public JeopardyServer(Integer numPlayers) throws IOException {
		//number of players
		numberOfPlayers = numPlayers;
		
		//Defining questions
		questions = new QuestionForm[2];
		answerBank = new String[2];
		questions[0].dollarAmt = 200;
		questions[0].question = "What is Ammon's last name?";
		
		questions[1].dollarAmt = 300;
		questions[1].question = "What is Nathan's favorite Overwatch team that is garbage?";
		
		answerBank[0] = "Riley";
		answerBank[1] = "Dallas Fuel";
		
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
			playerObjectIn[0] = new ObjectInputStream(playerSocket[i].getInputStream());
			
			//In/out data streams binding
			playerDataOut[i] = new DataOutputStream(playerOutput[i]);
			playerDataIn[i] = new DataInputStream(playerInput[i]);
			
			//Tell the player which one they are
			playerDataOut[i].writeInt((i + 1));
		}
		
		//Tell the players game has started
		for(int i = 0; i < numPlayers; i++) {
			playerDataOut[i].writeUTF("Game Started");
			playerDataOut[i].writeInt(numPlayers);
		}
		
		
		//STAGE TWO
		int questionCounter = 0;
		boolean gameOver = false;
		boolean answer = false;
		
		while(!gameOver) {
			//if no questions send gameOverForm
			if (questionCounter == questions.length)
			{
				for(int i = 0; i < numPlayers; i++) {
					playerDataOut[i].writeUTF("Game Over");
				}
				gameOver = true;
			}
			
			//pass clients question/answer
			for(int i = 0; i < numPlayers; i++) {
				playerObjectOut[i].writeObject(questions[questionCounter]);
				questionCounter++;
			}
			
			while(!answer) {
				//receive answers from everyone + uid
				GuessForm tempForm = null;
				try {
				for(int i = 0; i < numPlayers; i++) {
					tempForm = (GuessForm) playerObjectIn[0].readObject();
				}
				//if wrong do nothing
				//if right pass to all clients correct guess + uid
				if(tempForm.theGuess.equals(answerBank[questionCounter]))
				{
					for(int i = 0; i < numPlayers; i++) {
						AnswerForm tempAnswer = new AnswerForm();
						tempAnswer.playerID = tempForm.playerID;
						tempAnswer.dollarAmount = questions[questionCounter].dollarAmt;
						tempAnswer.theAnswer = tempForm.theGuess;
					}
					answer = true;
				}
			}
				
			 catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
}
}