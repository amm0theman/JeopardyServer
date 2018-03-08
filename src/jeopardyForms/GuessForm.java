package jeopardyForms;

import java.io.Serializable;

public class GuessForm implements JeopardyForm, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String theGuess;
	public Integer playerID;
	
	@Override
	public int returnFormType() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public String toString() {
		return playerID + ": " + theGuess + " INCORRECCT";
	}
}
