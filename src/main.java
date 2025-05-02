import java.io.*;
import java.util.Scanner;
import java.sql.*;
import java.util.InputMismatchException;
public class main {//The begining part of this code such as login handling, input etc. was used the code from HW4Q1.java file
    private static final String DB_URL = "";

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        Scanner scanner = new Scanner(System.in);
        Console in = System.console();
        if( in == null ){
            System.err.println( "[Error]: Could not get console instance." ); 
            System.exit(1);
        }
        do{
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

        try(Statement s = conn.createStatement();){
            System.out.print("Input name search substring: ");
            int check=0;

            do{
              String name = in.readLine();
              String searchName = "select ID, name from student where name like (?)";
              try(PreparedStatement ps = conn.prepareStatement(searchName);){
                  ps.setString(1, "%"+name+"%");
                  ResultSet rs = ps.executeQuery();
                  if (!rs.next()){
                      System.out.println("No matches. Try again.");
                  }else{
                      System.out.println("Here is a list of all matching IDs");
                      do{
                          System.out.println(rs.getInt("ID") + " " +rs.getString("name"));
                      }while(rs.next());

                      check=1;
                  }
              }
            }while(check==0);
            int idSt=0;
            System.out.print("Enter the student ID for the student whose transcript you seek. ");
            int check1=0;
            do{
              if(scanner.hasNextInt()){
                idSt = scanner.nextInt();
                if (idSt>0 && idSt<99999){
                  check1=1;}
                  else{
                    System.out.print("Please enter an integer between 0 and 99999: ");
                  }
              }else{
                System.out.print("Please enter an integer between 0 and 99999: ");
                scanner.next(); 
              }
              scanner.nextLine();
            }while(check1==0);
                          String searchID = "select semester, year, grade, dept_name, title, course.course_id from takes join course on takes.course_id=course.course_id  where ID = ? order by year asc, semester desc";
                          String studentName="select name from student where ID= ?";
              try (PreparedStatement ps = conn.prepareStatement(searchID); PreparedStatement ps1 = conn.prepareStatement(studentName);) {
                ps.setInt(1, idSt);
                ps1.setInt(1, idSt);
                ResultSet rs = ps.executeQuery();
                ResultSet rs1 = ps1.executeQuery();
                String nameStudent="";
                if (rs1.next()) {
                    nameStudent=rs1.getString("name");  } 
                    
                if(!rs.next()){
                  System.out.println("No transcript, student has not taken courses yet.");
                }else{
                    System.out.println("Transcript for student " + idSt + " "+rs1.getString("name"));
                    do{
                        System.out.printf("%-5d %-10s %-15s %-4d %-30s %-2s%n",rs.getInt("year"),rs.getString("semester"),rs.getString("dept_name"),rs.getInt("course_id"),rs.getString("title"),rs.getString("grade"));
                    }while(rs.next());
                }
            }catch (SQLException e) {
            e.printStackTrace();
            }
        }catch (SQLException e) {
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
