import java.io.*;
import java.util.Scanner;
import java.util.UUID;
import java.sql.*;
import java.util.InputMismatchException;
public class functions {
    public static boolean idExists(String table, int id, Connection conn){
        String query = "select count(*) from "+ table +" where "+table+"_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1) > 0;
            }
        }catch(SQLException e){
            System.err.println("[Error] " + e.getMessage());
        }
        return false;
    }
    public static int getCurrVal(Connection conn, String sequenceName){//https://forums.oracle.com/ords/apexds/post/generate-unique-random-number-4457
        String query="select " + sequenceName +".currval from dual";
        try (Statement st = conn.createStatement(); 
            ResultSet rs = st.executeQuery(query)){
            if(rs.next()){return rs.getInt(1);}
        }catch(SQLException e){
            System.err.println("[Error] " + e.getMessage());
        }
        return -1;
    }
    public static int getIntInput(Scanner scanner){
        while(true){
            if(scanner.hasNextInt()){
                int val = scanner.nextInt();
                scanner.nextLine();
                return val;
            }else{
                System.out.println("[Error] Please enter a integer.");
                scanner.nextLine();
            }
        }
    }
    
    public static double getDoubleInput(Scanner scanner){
        while(true){
            if(scanner.hasNextDouble()){
                double val = scanner.nextDouble();
                scanner.nextLine();
                return val;
            }else{
                System.out.println("[Error] Should be double(eg. 1.2 etc.).");
                scanner.nextLine();
            }
        }
    }
}
