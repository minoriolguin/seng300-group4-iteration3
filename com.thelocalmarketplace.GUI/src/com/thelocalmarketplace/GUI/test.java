package com.thelocalmarketplace.GUI;

import javax.swing.SwingUtilities;

public class test {
	public static void main(String[] args) {
	    	//To open GUI 
        SwingUtilities.invokeLater(() -> {
            AttendantFrame attendantFrame = new AttendantFrame(); 
            attendantFrame.show();
    });
	}
}
