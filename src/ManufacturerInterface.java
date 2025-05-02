import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class ManufacturerInterface{
  public static void connect(Connection conn, Scanner scanner){
    System.out.println("\n~~~ Welcome to Manufacturer Interface! ~~~");
    System.out.print("Enter your Supplier ID: ");
    int supplierId = scanner.nextInt();
    scanner.nextLine();

    try{
        if(!functions.idExists("supplier", supplierId, conn)) {
            System.out.println("[Error] Manufacturer ID not found.");
            return;
        }
    }catch(SQLException e){
        trace;
    }
  }
}

