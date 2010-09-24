package lw3d.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringLoader {
	
	static private Object object = new Object(); 
	
	public static String loadStringExceptionless(String filename) {
		try {
			return loadString(filename);
		} catch (IOException e) {
			System.out.println("Can't read " + filename + ", using empty string instead.");
			return "";
		}
	}

	
	public static String loadString(String filename)
			throws IOException {
		InputStream is = object.getClass().getResourceAsStream(filename);
		if(is == null) {
			System.out.println("Cant't load text file: " + filename);
			return "";
		}
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		StringBuilder builder = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			builder.append(line + "\n");
		}
		reader.close();

		return builder.toString();
	}
	
	public static void setObject(Object givenObject) {
		object = givenObject;
	}
}
