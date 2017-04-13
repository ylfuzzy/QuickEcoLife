package QuickEcoLife.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class ResourceHandler {
	// There will be a directory called "res" in the jar's working directory.
	private final static String RESOURCE_DIR = "res";
	private final static String ID_HISTORY = "id_history.data";
	private final static String ID_HISTORY_PATH = getWorkingDirectory() + RESOURCE_DIR + File.separator + ID_HISTORY;
	
	private static String getWorkingDirectory() {
		String working_dir = ClassLoader.getSystemClassLoader().getResource(".").getPath();
		try {
			working_dir = URLDecoder.decode(working_dir, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// The working directory of the running jar.
		return working_dir;
	}
	
	public static String getChromeDriverPath() {
		// File name for Unix like OS.
		String chromedriver_name = "chromedriver";
		
		// If the program runs on the Windows system, 
		// the filename extension ".exe" needs to be appended.
		if (System.getProperty("os.name").startsWith("Windows")) {
			chromedriver_name += ".exe";
		}
		
		// chromeDriver will be put inside the "RESOURCE_DIR".
		return getWorkingDirectory() + RESOURCE_DIR + File.separator + chromedriver_name;
	}
	
	public static void saveToIDHistory(String id) {
		File id_history = new File(ID_HISTORY_PATH);
		if (!id_history.exists()) {
			try {
				id_history.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(id_history, true), "UTF-8"));
			bufferedWriter.write(id + System.getProperty("line.separator"));
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<String> getAutoCompleteListFromIDHistory() {
		List<String> autoCompleteList = new ArrayList<String>();
		File id_history = new File(ID_HISTORY_PATH);
		if (id_history.exists()) {
			// Construct BufferedReader from InputStreamReader
			try {
				BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(id_history), "UTF-8"));
				String id = null;
				while ((id = bufferReader.readLine()) != null) {
					autoCompleteList.add(id);
				}
				bufferReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return autoCompleteList;
	}
}
