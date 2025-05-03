import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
              System.out.println("\n~~~ Supplier menu ~~~");
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
                      viewPastShipments(conn, supplierId);
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
      private static void viewPastShipments(Connection conn, int supplierId) {
        String query = "select shipment.shipment_id, shipment.shipment_date,store.location,product.name,shipmentContains.quantity, shipmentContains.price_per_unit,batch.batch_id " +
                       "from shipment join shipmentContains on shipment.shipment_id = shipmentContains.shipment_id "+
                       "join batch on shipmentContains.batch_id = batch.batch_id "+
                       "join product on batch.product_id = product.product_id "+
                       "join store on shipment.store_id = store.store_id "+
                       "where shipment.supplier_id = ? order by shipment.shipment_date desc";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, supplierId);
            ResultSet rs=ps.executeQuery();
            int order=0;
            boolean hasShipments=false;
            while(rs.next()){
              if(!hasShipments){
                System.out.println("\nShipment list:");
                hasShipments=true;
            }
                order=order+1;
                System.out.println("\n"+order+".");
                System.out.println("Shipment ID: "+rs.getInt("shipment_id"));
                System.out.println("Date of the shipment: "+rs.getDate("shipment_date"));
                System.out.println("Store location: "+rs.getString("location"));
                System.out.println("Product name: "+rs.getString("name"));
                System.out.println("Batch id: "+rs.getString("batch_id"));
                System.out.println("Quantity: "+rs.getInt("quantity"));
                System.out.println("Price per unit: $" +rs.getDouble("price_per_unit"));
                System.out.println("Total price: $" +rs.getInt("quantity")*rs.getDouble("price_per_unit"));
            }if(!hasShipments){
              System.out.println("\nNo shipments were found.");
          }
        }catch(SQLException e){
            System.err.println("[Error] Something wrong happened during the retrieving shipments" + e.getMessage());
        }
    }
}
