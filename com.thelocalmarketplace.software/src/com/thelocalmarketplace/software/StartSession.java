//Elizabeth Szentmiklossy UCID: 30165216
//Justine Mangaliman UCID: 30164741
//Enzo Mutiso UCID: 30182555
//Abdelrahman Mohamed UCID: 30162037
//Mohammad Mustafa Mehtab UCID: 30189394 
package com.thelocalmarketplace.software;

/**
 * The StartSession class is responsible for managing a session state and starting a session.
 * This class provides methods to check if a session has already started and to start a session.
 * 
 * @author Justine Mangaliman (UCID: 30164741)
 *
 */

public class StartSession {
	private boolean status = false;
	
	/**
     * Checks if the session has already started.
     *
     * @return {@code true} if the session has started, {@code false} otherwise.
     */
	public boolean checkSessionStarted() {
		if (status == false) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
     * Starts the session if it hasn't already started.
     * Prints a message to indicate the result.
     */
	public void startSession() {
		status = checkSessionStarted();
		
		if (status == false) {
			status = true;
			System.out.println("Touch Anywhere to Start");
		} else {
			System.out.println("System already started!");
		}
	}
}