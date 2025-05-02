import java.io.*;
import java.util.Scanner;
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
}
