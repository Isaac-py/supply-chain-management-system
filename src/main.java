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
        try{
          boolean condition=true;
          while(condition){
              System.out.println("\n~~~ Welcome to Regork Supply Chain Interface! ~~~");
              System.out.println("Where do you want to go from here?");
              System.out.println("1. Supplier Interface");
              System.out.println("2. Manufacturer Interface");
              System.out.println("3. Regork Management Interface");
              System.out.println("4. Exit");
              System.out.print("Please select an option: ");

              int choice = scanner.nextInt();
              scanner.nextLine();
              switch (choice){
                  case 1:
                      SupplierInterface.connect(conn, scanner);
                      break;
                  case 2:
                      ManufacturerInterface.connect(conn, scanner);
                      break;
                  case 3:
                      RegorkManagementInterface.connect(conn, scanner);
                      break;
                  case 4:
                      condition=false;
                      break;
                  default:
                      System.out.println("Invalid choice. Please try again.");
              }
          }
        }catch (Exception e) {
          e.printStackTrace();
      }
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
