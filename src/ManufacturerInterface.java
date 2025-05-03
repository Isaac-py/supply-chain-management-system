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
            System.out.println("2. Show all manufacturing activity");
            System.out.println("3. Delete manufacturing event");
            System.out.println("4. Exit to main interface");
            System.out.print("Please select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            switch(choice){
                case 1:
                    recordNewManufacturingEvent(conn, scanner, supplierId);
                    break;
                case 2:
                    viewManufacturingActivity(conn, supplierId);
                    break;
                case 3:
                    deleteManufacturingEvent(conn, scanner, supplierId);
                    break;
                case 4:
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
            System.out.println("\n");
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
    public static void viewManufacturingActivity(Connection conn, int supplierId) {
        try {
            String query = "select manufacturing_id, to_char(manufacturing_date,'YYYY-MM-DD') as manufacturing_date from manufacturing where supplier_id = ? order by manufacturing_date desc";//https://stackoverflow.com/questions/44105462/get-only-date-without-time-in-oracle
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, supplierId);
                ResultSet rs=ps.executeQuery();
                while(rs.next()){
                    int manId = rs.getInt("manufacturing_id");
                    String date = rs.getString("manufacturing_date");
                    System.out.println("\nManufacturing ID: " + manId);
                    System.out.println("Date: " +date);
                    String batchQuery = "select batch.product_id, product.name, batch.quantity, batch.serial_number, batch.lot_number from manufacturingProduces join batch on manufacturingProduces.batch_id = batch.batch_id join product product on batch.product_id = product.product_id where manufacturingProduces.manufacturing_id = ?";
                    try(PreparedStatement ps2 = conn.prepareStatement(batchQuery)){
                        ps2.setInt(1, manId);
                        ResultSet brs = ps2.executeQuery();
                        if(brs.next()){
                            System.out.println("Product ID: "+brs.getInt("product_id"));
                            System.out.println("Product Name: "+brs.getString("name"));
                            System.out.println("Quantity Produced: "+ brs.getInt("quantity"));
                            String serial=brs.getString("serial_number");
                            String lot=brs.getString("lot_number");
                            if(serial!=null) System.out.println("Serial Number: " +serial);
                            if(lot!=null) System.out.println("Lot Number: " + lot) ;
                        }
                    }
                    String component = "select manufacturingUses.product_id, product.name, manufacturingUses.quantity_used from manufacturingUses join product on manufacturingUses.product_id = product.product_id where manufacturingUses.manufacturing_id = ?";
                    try(PreparedStatement ps3 = conn.prepareStatement(component)){
                        ps3.setInt(1, manId);
                        ResultSet crs =  ps3.executeQuery();
                        boolean hasComponents=false;
                        int number =0;
                        while(crs.next()){
                            if(!hasComponents){
                                System.out.println("Components Used:");
                                hasComponents=true;
                            }
                            number=number+1;
                            System.out.print(number);
                            System.out.println(". Component ID: " + crs.getInt("product_id")+", Name: " + crs.getString("name")+ ", Quantity Used: "+ crs.getInt("quantity_used"));
                        }
                        if(!hasComponents){
                            System.out.println("No components were used during the manufacturing.");
                        }
                        
                    }
                }
                System.out.println(" \n");
            }
        }catch(SQLException e){
            System.err.println("[Error] Something happened during the retrieving manufacturing events" + e.getMessage());
        }
    }

    public static void deleteManufacturingEvent(Connection conn, Scanner scanner, int supplierId){
        System.out.print("Enter the Manufacturing ID to delete: ");
        int manId=scanner.nextInt();
        scanner.nextLine();
        try {
            conn.setAutoCommit(false);
            if(!functions.idExists("manufacturing", manId, conn)){
                System.out.println("[Error] Manufacturing event not found.");
                return;
            }
            try(PreparedStatement ps1 = conn.prepareStatement("delete from manufacturingUses where manufacturing_id=?");
                PreparedStatement ps2 = conn.prepareStatement("delete from manufacturingProduces where manufacturing_id=?");
                PreparedStatement ps3 = conn.prepareStatement("delete from manufacturing where manufacturing_id=?")){
                ps1.setInt(1, manId);
                ps2.setInt(1, manId);
                ps3.setInt(1, manId);
                ps1.executeUpdate();
                ps2.executeUpdate();
                ps3.executeUpdate();
                conn.commit();
                System.out.println("[Success] Manufacturing event deleted.\n");
            }
        }catch(SQLException e){
            try{
                conn.rollback();
            }catch (SQLException rollbackEx){
                System.err.println("[Error] Something went wrong during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("[Error] Something happened during the deleting manufacturing event" + e.getMessage());
        }finally{
            try{
                conn.setAutoCommit(true);
            }catch(SQLException e){
                System.err.println("[Error] couldn't reset auto commit: " + e.getMessage());
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

