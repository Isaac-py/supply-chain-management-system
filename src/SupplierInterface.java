import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SupplierInterface{
      public static void connect(Connection conn, Scanner scanner){
      System.out.println("\n~~~ Welcome to Supplier Interface! ~~~");
      System.out.print("Enter your Supplier ID: ");
      int supplierId = functions.getIntInput(scanner);
  
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
              System.out.println("5. List of products supplier offers");
              System.out.println("6. Exit to main interface");
              System.out.print("Please select an option: ");
              int choice = 0;
              choice=functions.getIntInput(scanner);
              switch(choice){
                  case 1:
                      recordNewShipment(conn,scanner,supplierId);
                      break;
                  case 2:
                      viewPastShipments(conn, supplierId);
                      break;
                  case 3:
                  addProductToCatalog(conn, scanner, supplierId);
                      break;
                  case 4:
                  updateProductPrice(conn,scanner,supplierId);
                      break;
                  case 5:
                  viewSupplierCatalog(conn, supplierId);
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
                System.out.println("Total price: $" +(double)rs.getInt("quantity")*rs.getDouble("price_per_unit"));
            }if(!hasShipments){
              System.out.println("\nNo shipments were found.");
          }
        }catch(SQLException e){
            System.err.println("[Error] Something wrong happened during the retrieving shipments" + e.getMessage());
        }
    }

    private static void addProductToCatalog(Connection conn, Scanner scanner, int supplierId){
      try{
          System.out.print("Enter product ID of the product you want to add: ");
          int productId=functions.getIntInput(scanner);
          if(!functions.idExists("product",productId,conn)){
              System.out.println("[Error] Product ID not found in product table.");
              return;
          }
          String checkQuery = "select count(*) from suppliesProduct where supplier_id=? and product_id=?";
          try(PreparedStatement ps=conn.prepareStatement(checkQuery)){
              ps.setInt(1, supplierId);
              ps.setInt(2, productId);
              ResultSet rs = ps.executeQuery();
              if(rs.next() && rs.getInt(1)>0){
                  System.out.println("[Error] Product already exists in supplier catalog.");
                  return;
              }
          }
          System.out.print("Enter price per unit: ");
          double price=functions.getDoubleInput(scanner);
          if(price<=0){
              System.out.println("[Error] Price must be greater than zero.");
              return;
          }
          String product = "insert into suppliesProduct(supplier_id,product_id,current_price) values (?,?,?)";
          try(PreparedStatement ps = conn.prepareStatement(product)){
              ps.setInt(1,supplierId);
              ps.setInt(2,productId);
              ps.setDouble(3,price);
              ps.executeUpdate();
              System.out.println("[Success] Product added to catalog");
              String getName = "select name from product where product_id = ?";
              try(PreparedStatement ps2=conn.prepareStatement(getName)){
                  ps2.setInt(1, productId);
                  ResultSet rs2=ps2.executeQuery();
                  if(rs2.next()){
                      System.out.println("Product overview:");
                      System.out.println("Product name: "+rs2.getString("name"));
                      System.out.println("Product ID: "+productId);
                      System.out.println("Price per unit: $"+price);
                  }
              }          
            }
      }catch(SQLException e){
          System.err.println("[Error] Something wrong happened while adding: "+e.getMessage());
      }
  }
  private static void updateProductPrice(Connection conn, Scanner scanner, int supplierId){
    try{
        String catalogCheck = "select count(*) from suppliesProduct where supplier_id=?";
        try(PreparedStatement ps=conn.prepareStatement(catalogCheck)){
            ps.setInt(1, supplierId);
            ResultSet rs=ps.executeQuery();
            if(rs.next()&&rs.getInt(1)==0){
                System.out.println("[Info] You don't offer anything. :(");
                return;
            }
        }
        System.out.print("Enter the product ID you want to update: ");
        int productId=functions.getIntInput(scanner);
        String checkQuery = "select current_price from suppliesProduct where supplier_id=? and product_id=?";
        try (PreparedStatement ps = conn.prepareStatement(checkQuery)){
            ps.setInt(1,supplierId);
            ps.setInt(2,productId);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                System.out.println("[Error] This product is not in your catalog.");
                return;
            }else{
                double oldPrice = rs.getDouble("current_price");
                System.out.println("Current price: $" + oldPrice);
            }
          }
        System.out.print("Enter the new price: ");
        double newPrice = functions.getDoubleInput(scanner);
        if(newPrice<=0){
            System.out.println("[Error] Price must be greater than zero.");
            return;
        }
        String newPriceUpdate = "update suppliesProduct set current_price=? where supplier_id=? and product_id=?";
        try(PreparedStatement ps=conn.prepareStatement(newPriceUpdate)){
            ps.setDouble(1, newPrice);
            ps.setInt(2, supplierId);
            ps.setInt(3, productId);
            int rows=ps.executeUpdate();
            if(rows>0){
                System.out.println("[Success] Price updated.");
                String getName = "select name from product where product_id=?";
                try(PreparedStatement ps2=conn.prepareStatement(getName)){
                    ps2.setInt(1, productId);
                    ResultSet rs2=ps2.executeQuery();
                    if(rs2.next()){System.out.println("New price: $" +newPrice);}
                }
            }else{System.out.println("[Error] Update failed. No rows affected.");}
        }
    }catch(SQLException e){
        System.err.println("[Error] Failed to update product price: "+e.getMessage());
    }
  } 
  private static void recordNewShipment(Connection conn, Scanner scanner, int supplierId){
    try{
        conn.setAutoCommit(false);
        System.out.print("Enter the store ID: ");
        int storeId = functions.getIntInput(scanner);
        if(!functions.idExists("store", storeId, conn)){
            System.out.println("[Error] Store ID not found.");
            return;
        }
        System.out.print("Enter shipment date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        String insertShipment = "insert into shipment(shipment_id,supplier_id,store_id,shipment_date) values (shipment_seq.nextval, ?, ?, to_date(?, 'YYYY-MM-DD'))";
        try(PreparedStatement ps=conn.prepareStatement(insertShipment)){
            ps.setInt(1, supplierId);
            ps.setInt(2, storeId);
            ps.setString(3, date);
            ps.executeUpdate();
        }
        int shipmentId = functions.getCurrVal(conn, "shipment_seq");
        System.out.println("Shipment recorded with ID: " + shipmentId);
        System.out.print("How many different batches are included in the shipment? ");
        int count = functions.getIntInput(scanner);
        for (int i=0;i<count;i++){
            System.out.print("Enter batch ID: ");
            int batchId = functions.getIntInput(scanner);
             if(!functions.idExists("batch", batchId, conn)){
                System.out.println("[Error] Batch ID not found.");
                conn.rollback();
                return;}
            System.out.print("How much of this butch is being shipped?: ");
            int quantity=functions.getIntInput(scanner);
            if(quantity<=0){
                System.out.println("[Error] Quantity must be greater than 0.");
                conn.rollback();
                return;
            }
            System.out.print("Enter price per unit for this batch: ");
            double price=functions.getDoubleInput(scanner);
            if (price<=0){
                System.out.println("[Error] Price must be greater than 0.");
                conn.rollback();
                return;
            }
            String insertSC="insert into shipmentContains(shipment_id,batch_id,quantity,price_per_unit) values(?,?,?,?)";
            try(PreparedStatement ps = conn.prepareStatement(insertSC)){
                ps.setInt(1,shipmentId);
                ps.setInt(2,batchId);
                ps.setInt(3,quantity);
                ps.setDouble(4,price);
                ps.executeUpdate();
            }
        }
        conn.commit();
        System.out.println("[Success] Shipment recorded successfully.");
        String query = "select shipment.shipment_id, shipment.shipment_date,store.location,product.name,shipmentContains.quantity, shipmentContains.price_per_unit,batch.batch_id " +
        "from shipment join shipmentContains on shipment.shipment_id = shipmentContains.shipment_id "+
        "join batch on shipmentContains.batch_id = batch.batch_id "+
        "join product on batch.product_id = product.product_id "+
        "join store on shipment.store_id = store.store_id "+
        "where shipment.shipment_id=?";
        try (PreparedStatement ps=conn.prepareStatement(query)){
          ps.setInt(1, shipmentId);
          ResultSet rs=ps.executeQuery();
          int number=1;
          while(rs.next()){
              if(number==1){
                  System.out.println("Shipment ID: " + rs.getInt("shipment_id"));
                  System.out.println("Date of Shipment: " + rs.getDate("shipment_date"));
                  System.out.println("Store Location: " + rs.getString("location"));
                  System.out.println("Batches Shipped:");
              }
              System.out.println("\n"+number+".");
              System.out.println("Product name: "+rs.getString("name"));
              System.out.println("Batch id: "+rs.getString("batch_id"));
              System.out.println("Quantity: "+rs.getInt("quantity"));
              System.out.println("Price per unit: $" +rs.getDouble("price_per_unit"));
              System.out.println("Total price: $" +(double)rs.getInt("quantity")*rs.getDouble("price_per_unit"));
              number=number+1;
          }
      }
    }catch(SQLException e){
        try{
            conn.rollback();
        }catch(SQLException rollEx){
            System.err.println("[Error] Something wrong happened during rollback" + rollEx.getMessage());
        }
        System.err.println("[Error] Failed to record shipment: " + e.getMessage());
    }finally{
        try{
            conn.setAutoCommit(true);
        }catch(SQLException e){
            System.err.println("[Error] Couldn't reset auto-commit: " + e.getMessage());
        }
    }
  }
  private static void viewSupplierCatalog(Connection conn, int supplierId){
    String query = "select product.product_id, product.name, suppliesProduct.current_price " +
                   "from suppliesProduct " +
                   "join product on suppliesProduct.product_id = product.product_id " +
                   "where suppliesProduct.supplier_id=? " +
                   "order by product.name";

    try(PreparedStatement ps=conn.prepareStatement(query)){
        ps.setInt(1,supplierId);
        ResultSet rs=ps.executeQuery();
        int number=0;
        while(rs.next()){
            if(number==0){
              System.out.println("\n--- Supplier Catalog ---");
            }
            number=number+1;
            System.out.println("\n"+number+".");
            System.out.println("Product ID: "+rs.getInt("product_id"));
            System.out.println("Name: " +rs.getString("name"));
            System.out.println("Current Price: $"+rs.getDouble("current_price"));
        }
        if(number==0){
            System.out.println("\n[Info] This supplier has no products in their catalog.");
        }
    }catch(SQLException e){
        System.err.println("[Error] Something worng happened when tried to retrieve supplier catalog: " + e.getMessage());
    }
  }
}
