import java.sql.Connection;
import java.util.Scanner;

public class RegorkManagementInterface{
  public static void connect(Connection conn, Scanner scanner){
      System.out.println("\n ~~~ Welcome, Regork Management! ~~~");
      System.out.print("Enter your Manager ID: ");
      int managerId = functions.getIntInput(scanner);
  
      try{
          if(!functions.idExists("supplier", managerId, conn)) {
              System.out.println("[Error] Manager ID not found.");
              return;
          }
  
          boolean condition = true;
          while(condition){
              System.out.println("\n~~~ Regork Manager menu ~~~");
              System.out.println("Where do you want to go from here?");
              System.out.println("1. See shipments that a store received");
              System.out.println("2. Add a new store");
              System.out.println("3. Update store information");
              System.out.println("4. Option");
              System.out.println("5. Option");
              System.out.println("6. Exit to main interface");
              System.out.print("Please select an option: ");
              int choice = 0;
              choice=functions.getIntInput(scanner);
              switch(choice){
                  case 1:
                      break;
                  case 2:
                      break;
                  case 3:
                      break;
                  case 4:
                      break;
                  case 5:
                    break;
                  case 6:
                      condition=false;
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
