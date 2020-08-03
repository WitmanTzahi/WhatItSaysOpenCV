package com.lbmotion.whatitsays.data;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Parameters {
	public static final String PARAMETER_FILE = "parameter.par";
	public static Context context;

	public static void deleteParametersFile() {
		context.deleteFile(PARAMETER_FILE);
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getAll() {
		HashMap<String, String> parameters = null;
		FileInputStream inputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			inputStream = context.openFileInput(PARAMETER_FILE);
			objectInputStream = new ObjectInputStream(inputStream);
			parameters = (HashMap<String, String>) objectInputStream.readObject();
		} catch (Exception e) {
		}
		try {
			objectInputStream.close();
		} catch (Exception e) {
		}
		try {
			inputStream.close();
		} catch (Exception e) {
		}
		if (parameters == null)
			parameters = new HashMap<String, String>();
		return parameters;
	}

	public static boolean write(HashMap<String, String> parameters) {
		boolean returnCode = false;
		FileOutputStream outputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			outputStream = context.openFileOutput(PARAMETER_FILE, Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(parameters);
			returnCode = true;
		} catch (Exception e) {
		}
		try {objectOutputStream.flush();} catch (Exception e) {}
		try {outputStream.flush();} catch (Exception e) {}
		try {objectOutputStream.close();} catch (Exception e) {}
		try {outputStream.close();} catch (Exception e) {}
		return returnCode;
	}

	public static synchronized boolean update(String key, String value) {
		boolean returnCode = false;
		HashMap<String, String> parameters = getAll();
		String v = parameters.get(key);
		if (v != null)
			parameters.remove(key);
		parameters.put(key, value);
		FileOutputStream outputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			outputStream = context.openFileOutput(PARAMETER_FILE, Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(parameters);
			returnCode = true;
		} catch (Exception e) {
		}
		try {
			objectOutputStream.close();
		} catch (Exception e) {
		}
		try {
			outputStream.close();
		} catch (Exception e) {
		}
		return returnCode;
	}

	public static synchronized String get(String key) {
		HashMap<String, String> parameters = getAll();
		return parameters.get(key);
	}	

}