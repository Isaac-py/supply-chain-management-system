import java.sql.Connection;
import java.util.Scanner;

public class SupplierInterface{
      public static void connect(Connection conn, Scanner scanner){
      System.out.println("\n~~~ Welcome to Supplier Interface! ~~~");
      System.out.print("Enter your Supplier ID: ");
      int supplierId = scanner.nextInt();
      scanner.nextLine();
  
      try{
          if(!functions.idExists("supplier", supplierId, conn)) {
              System.out.println("[Error] Supplier ID not found.");
              return;
          }
  
          boolean condition = true;
          while(condition){
              System.out.println("~~~ Supplier menu ~~~");
              System.out.println("Where do you want to go from here?");
              System.out.println("1. Record a new Shipment");
              System.out.println("2. Show all past shipments");
              System.out.println("3. Add a new product to catalog");
              System.out.println("4. Update current price of the product");
              System.out.println("5. Exit to main interface");
              System.out.print("Please select an option: ");
              int choice = scanner.nextInt();
              scanner.nextLine(); 
              switch(choice){
                  case 1:
                      // recordNewManufacturingEvent(conn, scanner, supplierId);
                      break;
                  case 2:
                      // viewManufacturingActivity(conn, supplierId);
                      break;
                  case 3:
                      // deleteManufacturingEvent(conn, scanner, supplierId);
                      break;
                  case 4:
                      //funct
                      break;
                  case 5:
                      condition = false;
                      break;
                  default:
                      System.out.println("Invalid option.");
              }
  
          }
      }catch(Exception e){
          System.err.println("[Error] " + e.getMessage());
      }
  }
}
