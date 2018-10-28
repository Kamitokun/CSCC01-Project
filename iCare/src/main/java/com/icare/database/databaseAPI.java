package main.java.com.icare.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import main.java.com.icare.passwords.passwords;

public class databaseAPI{

	private static final String DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public static boolean insertUser(Connection connection, String username, String password, String firstName, String lastName, String accountType) {

		String sql = "INSERT INTO Login(username, firstName, lastName, accountType) VALUES(?,?,?,?);";
		try {

			PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, firstName);
			preparedStatement.setString(3, lastName);
			preparedStatement.setString(4, accountType);
			int id = 0;
			if (preparedStatement.executeUpdate()>0){
		        ResultSet uniqueKey = preparedStatement.getGeneratedKeys();
		        if (uniqueKey.next()) {
		        	id = uniqueKey.getInt(1);
		        	accountPasswordHelper(connection, id, password);
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean accountPasswordHelper(Connection connection, int id, String password){
		String sql = "Update Login set password = ? where ID = ?;";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, passwords.passwordHash(password));
			preparedStatement.setInt(2, id);
			preparedStatement.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static LinkedHashMap<String, ArrayList<String>> importData(String fileName) {
		BufferedReader csvReader = null;
		LinkedHashMap <String, ArrayList<String>> fileData = new LinkedHashMap <String, ArrayList<String>>();

		try {
			String headers = null;
			String line = null;
			csvReader = new BufferedReader(new FileReader("resources/" + fileName));

			// read the first line to get the column headers
			headers = csvReader.readLine();

			// split the column headers
			String[] columnHeaders = headers.split(DELIMITER, -1);

			// add column headers as keys to fileData hashmap
			for (String header : columnHeaders) {
				fileData.put(header, new ArrayList<String>());
			}

			// go through each line
			while ((line = csvReader.readLine()) != null) {
				// split each line
				String[] lineData = line.split(DELIMITER, -1);
				// go through each piece of data and add to relevant column header's list
				for (int i = 0; i < lineData.length; i++) {
					ArrayList <String> columnData = fileData.get(columnHeaders[i]);
					columnData.add(lineData[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("Error while reading .csv file: " + fileName);
			e.printStackTrace();
		} finally {
			try {
				csvReader.close();
			} catch (IOException e) {
				System.out.println("Error while closing .csv file: " + fileName);
				e.printStackTrace();
			}
		}

		return fileData;
	}

	public static void exportData(LinkedHashMap<String, ArrayList<String>> fileData, String fileName) {
		FileWriter csvWriter = null;

		try {
			csvWriter = new FileWriter("resources/" + fileName);

			// add column headers as first line in csv file
			int i = 0;
			for (String header : fileData.keySet()) {
				csvWriter.append(header);
				if (i < (fileData.keySet().size() - 1)) {
					csvWriter.append(DELIMITER);
				}
				i++;
			}
			// add new line after column headers
			csvWriter.append(NEW_LINE_SEPARATOR);

			// iterate through each key-value pairing and write to file
			for (int j = 0; j < fileData.keySet().size(); j++) {
				int k = 0;
				for (String header : fileData.keySet()) {
					csvWriter.append(fileData.get(header).get(j));
					k++;
					if (k < (fileData.keySet().size())) {
						csvWriter.append(DELIMITER);
					}
				}
				if (j < (fileData.keySet().size() - 1)) {
					csvWriter.append(NEW_LINE_SEPARATOR);
				}
			}
		} catch (Exception e) {
			System.out.println("Error while writing to .csv file: " + fileName);
			e.printStackTrace();
		} finally {
			try {
				csvWriter.close();
			} catch (IOException e) {
				System.out.println("Error while closing .csv file: " + fileName);
				e.printStackTrace();
			}
		}
	}
}
