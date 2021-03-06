package main.java.com.icare.database;


import java.sql.Connection;
import java.sql.SQLException;



public class makeDatabase {
 

  /**
   * Initialize the database.
   */
  public static void initialize() {
    
    Connection connection = databaseDriver.connectDataBase();
    try {
      initializeDatabase(connection);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Unable to close connection");
      }
    }
    
  }
  

  private static void initializeDatabase(Connection connection) {
    try {
      databaseDriver.initialize(connection);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
  




}