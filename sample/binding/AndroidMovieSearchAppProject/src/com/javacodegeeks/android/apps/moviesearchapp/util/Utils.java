package com.javacodegeeks.android.apps.moviesearchapp.util;

import java.io.IOException;
import java.io.InputStream;

public class Utils {
	
	public static void closeStreamQuietly(InputStream inputStream) {
		 try {
			if (inputStream != null) {
			     inputStream.close();  
			 }
		} catch (IOException e) {
			// ignore exception
		}
	}
	
	public static boolean isMissing(String s){		
    	if (s==null||s.trim().equals("")) {
    		return true;
    	}
    	return false;    		
    }

}
