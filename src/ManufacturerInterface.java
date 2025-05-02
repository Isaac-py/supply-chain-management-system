import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

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

        boolean condition = true;
        while(condition){
            System.out.println("~~~ Manufacturer menu ~~~");
            System.out.println("Where do you want to go from here?");
            System.out.println("1. Record a new Manufacturing Event");
            System.out.println("2. Exit to main interface");
            System.out.print("Please select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            switch(choice){
                case 1:
                    recordNewManufacturingEvent(conn, scanner, supplierId);
                    break;
                case 2:
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
    public static void recordNewManufacturingEvent(Connection conn, Scanner scanner, int supplierId){
        try{
            conn.setAutoCommit(false);
        }catch(SQLException e){
            System.err.println("[Error] Could not start transaction: " + e.getMessage());
            return;
        }
        try{
            // we first ask if it's a new product. 
            System.out.print("Is this a new product? (Yes/No): ");
            boolean isNewProduct=scanner.nextLine().equalsIgnoreCase("YES");
            int productId;
            String name = null;
            if(isNewProduct){
                System.out.print("Enter the name of the product: ");
                name = scanner.nextLine();
                int isEnd = 1;
                String insertProduct = "insert into product (product_id, name, is_end_product) values (product_seq.nextval, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertProduct)) {
                    ps.setString(1, name);
                    ps.setInt(2, isEnd);
                    ps.executeUpdate();
                }
                productId = functions.getCurrVal(conn,"product_seq");
                System.out.println("New product was successfully created with ID: " + productId);
            }else{
                System.out.print("Enter product ID: ");
                productId = scanner.nextInt();
                scanner.nextLine();
            }

            // secondly, we ask for how much quanntity we want to produce
            System.out.print("Enter quantity of the product: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            // next, we ask for the type of the product, do decide what type of unique number will it get, serial or lot
            System.out.print("Is this product a unique item (Yes/No)?: ");
            boolean isUnique = scanner.nextLine().equalsIgnoreCase("YES");
            String serial = null;
            String lot = null;
            if(isUnique){
                serial=generateSerialNumber();
            }else{
                lot=generateLotNumber();
            }

            // next,we ask for date of the event
            System.out.print("Enter manufacturing date (YYYY-MM-DD): ");//handle input later
            String date =scanner.nextLine();

            // next,we finally insert into manufacturing what we have so far
            String insertIntoManufacturing = "insert into manufacturing (manufacturing_id, supplier_id, manufacturing_date)"+" values (manufacturing_seq.nextval, ?, to_date(?, 'YYYY-MM-DD'))";//https://www.oreilly.com/library/view/oracle-and-plsql/9781430232070/converting_a_string_to_a_date.html#:~:text=Use%20the%20TO_DATE%20function%20to,'MM%2FDD%2FYYYY')%3B
            try (PreparedStatement ps = conn.prepareStatement(insertIntoManufacturing)){
                ps.setInt(1, supplierId);
                ps.setString(2, date);
                ps.executeUpdate();
            }
            int manufacturingId=functions.getCurrVal(conn, "manufacturing_seq");

            // also, don't forget to insert into batch too
            String insertBatch = "insert into batch (batch_id, product_id, quantity,serial_number, lot_number) "+"values (batch_seq.nextval, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertBatch)){
                ps.setInt(1,productId);
                ps.setInt(2,quantity);
                ps.setString(3, serial);
                ps.setString(4, lot);
                ps.executeUpdate();
            }
            int batchId=functions.getCurrVal(conn, "batch_seq");

            // also put details about what is being produced into table manufactruing produces
            String link = "insert into manufacturingProduces (manufacturing_id, batch_id) values (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(link)){
                ps.setInt(1,manufacturingId);
                ps.setInt(2, batchId);
                ps.executeUpdate();
            }

            // We should record components that were used
            List<String> componentsOverview = new ArrayList<>();
            System.out.print("How many components were used? ");
            int compCount = scanner.nextInt();
            scanner.nextLine();
            for (int i=0;i<compCount;i++){
                String compName = null;
                System.out.print("Is this a new component? (Yes/No): ");
                boolean isNewComp = scanner.nextLine().equalsIgnoreCase("YES");
                int compId;
                if(isNewComp){
                    System.out.print("Enter component name: ");
                    compName = scanner.nextLine();
                    String insertComp = "insert into product (product_id, name,is_end_product) values (product_seq.NEXTVAL, ?, 0)";
                    try (PreparedStatement ps = conn.prepareStatement(insertComp)){
                        ps.setString(1, compName);
                        ps.executeUpdate();
                    }
                    compId=functions.getCurrVal(conn, "product_seq");
                    System.out.println("Component created with ID: " +compId);
                }else{
                    System.out.print("Enter existing component product ID: ");
                    compId = scanner.nextInt();
                    scanner.nextLine();
                    String getName = "select name from product where product_id = ?";
                    try(PreparedStatement ps=conn.prepareStatement(getName)){
                        ps.setInt(1, compId);
                        ResultSet rs=ps.executeQuery();
                        if(rs.next()){
                            compName = rs.getString("name");
                        }
                    }
                }


                System.out.print("Enter quantity used: ");
                int quantityUsed = scanner.nextInt();
                scanner.nextLine();
                String useInsert = "insert into manufacturingUses (manufacturing_id,product_id,quantity_used) values (?, ?, ?)";
                try(PreparedStatement ps = conn.prepareStatement(useInsert)){
                    ps.setInt(1, manufacturingId);
                    ps.setInt(2, compId);
                    ps.setInt(3, quantityUsed);
                    ps.executeUpdate();
                    componentsOverview.add("Component ID: " + compId + ", Quantity Used: " + quantityUsed + ", Component Name: " + compName);
                }
            }
            System.out.println("[Success]");
            System.out.println("Manufacturing event recorded.");
            System.out.println("~~~ Congratulations!!! ~~~");
            System.out.println("--- Summary ---");
            System.out.println("Manufacturing ID: "+manufacturingId);
            System.out.println("Product ID: "+productId);
            if (name != null) System.out.println("Product Name: "+name);
            System.out.println("Quantity Produced: "+quantity);
            if (serial != null) System.out.println("Serial Number: "+serial);
            if (lot != null) System.out.println("Lot Number: "+lot);
            System.out.println("Date: " + date);
            System.out.println("--- Components Used ---");
            for  (String summary:componentsOverview){
                System.out.println(summary);
            }
            conn.commit();
        }catch(SQLException e){
            try{
                conn.rollback();
            }catch(SQLException rollbackEx){
                System.err.println("[Error] Something went wrong during rollback: "+rollbackEx.getMessage());
            }
            System.err.println("[Error] Something went wrong during manufacturing event: "+ e.getMessage());
        }finally{
            try{
                conn.setAutoCommit(true);
            }catch(SQLException e){
                 System.err.println("[Error] couldn't reset auto commit" + e.getMessage());
            }
        }
    }
    private static String generateSerialNumber(){
        return "SN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    private static String generateLotNumber(){
        return "LOT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

