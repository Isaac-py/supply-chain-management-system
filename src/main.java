import java.io.*;
import java.util.Scanner;
import java.sql.*;
import java.util.InputMismatchException;
public class main {

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        Scanner scanner = new Scanner(System.in);
        Console in = System.console();
        if( in == null ){
            System.err.println( "[Error]: Could not get console instance." ); 
            System.exit(1);
        }
        do{
            System.out.print("Enter Oracle Databse URL: "); 
            String DB_URL = in.readLine();

            System.out.print("Enter Oracle user id: "); 
            String user = in.readLine();

            System.out.print("Enter Oracle user password: "); 
            String pass = new String( in.readPassword() );
            
          try{
            conn = DriverManager.getConnection(DB_URL, user, pass);
            System.out.println("Connection successfully established!.");
          } catch (SQLException se){
            System.err.println("[Error]: Connect error, re-enter login data.");
          }
        }while (conn == null);
        
        if(conn != null){
          try {
            conn.close();
            System.out.println("Connection successfully closed!");
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }    
    }
}
