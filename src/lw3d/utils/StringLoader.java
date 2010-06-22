package lw3d.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StringLoader {
	public static String loadString(File file)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			builder.append(line + "\n");
		}
		reader.close();

		return builder.toString();
	}
}
