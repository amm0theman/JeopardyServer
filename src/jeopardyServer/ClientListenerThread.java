package jeopardyServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jeopardyForms.AnswerForm;
import jeopardyForms.GuessForm;
import jeopardyForms.QuestionForm;

public class ClientListenerThread implements Runnable {

	GuessForm newForm = null;
	ObjectInputStream[] playerObjectIn;
	ObjectOutputStream[] playerObjectOut;
	int numPlayers;
	int playerListeningTo;
	QuestionForm[] questions;
	String[] answerBank;
	static volatile int questionCounter = 0;
	
	ClientListenerThread(ObjectInputStream[] _playerObjectIn,
						int _numPlayers,
						int _playerListeningTo,
						ObjectOutputStream[] _playerObjectOut,
						QuestionForm[] _questions,
						String[] _answerBank) {
		answerBank = _answerBank;
		playerObjectIn = _playerObjectIn;
		numPlayers = _numPlayers;
		playerListeningTo = _playerListeningTo;
		playerObjectOut = _playerObjectOut;
		questions = _questions;
		questionCounter = 0;
	}
	
	@Override
	synchronized public void run() {
		boolean gameRunning = true;
		while(gameRunning) {
			
			//read form from user
			try {
				newForm = (GuessForm) playerObjectIn[playerListeningTo].readObject();
				System.out.println("ClientListener:" + playerListeningTo + "  Read form from user player " + playerListeningTo);
				System.out.println("ClientListener:" + playerListeningTo + "  questions.Length:" + questions.length + " questionCounter:" + questionCounter);
				//if special case where question was answered but you weren't the one who answered it AND no questions left
				if(newForm.returnFormType() == 5 && questionCounter >= questions.length) {
					System.out.println("ClientListener:" + playerListeningTo + " returning ln 48");
					return;
				}
				
				if(questionCounter >= questions.length) {
					System.out.println("ClientListener:" + playerListeningTo + " returning ln 53");
					break;
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//if wrong answer send everybody the wrong answer
			if(!newForm.getGuess().equals(answerBank[questionCounter].toString())) {
				System.out.println("ClientListener:" + playerListeningTo + "  Guess was incorrect");
				for(int i = 0; i < numPlayers; i++) {
					try {
						playerObjectOut[i].writeObject((GuessForm)newForm);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("ClientListener:" + playerListeningTo + "  Told users incorrect guess");
				}
			}
			
			//if right guess send out correct ans3wer to all users
			if(newForm.getGuess().equals(answerBank[questionCounter]))
			{
				System.out.println("ClientListener:" + playerListeningTo + "  Guess was correct");
				
				for(int i = 0; i < numPlayers; i++) {
					AnswerForm tempAnswer = new AnswerForm();
					tempAnswer.playerID = newForm.playerID;
					tempAnswer.dollarAmount = questions[questionCounter].dollarAmt;
					tempAnswer.theAnswer = newForm.theGuess;
					
					try {
						playerObjectOut[i].writeObject(tempAnswer);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				synchronized(this) { questionCounter++; }
				
				//send the next question
				System.out.println("ClientListener:" + playerListeningTo + "  Sending next question");
				for(int i = 0; i < numPlayers; i++) {
					try {
						if(questionCounter < questions.length)
							playerObjectOut[i].writeObject(questions[questionCounter]);
						//If no questions left quit out
						else
						{
							System.out.println("ClientListener:" + playerListeningTo + "  out of questions");
							gameRunning = false;
							System.out.println("ClientListener:" + playerListeningTo + "  Returning ln 106");
							return;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
		return;
	}
}
