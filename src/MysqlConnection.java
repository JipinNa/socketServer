
import com.example.chioy.test.PersonInfo;

import java.sql.*;
import static java.lang.Class.forName;

public class MysqlConnection{
    private static final String URL = "jdbc:mysql://localhost:3306/information_patient?useUnicode=true&characterEncoding=utf-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static Connection conn = null;
    static Statement statement;

    public MysqlConnection(){

    }

    public static Connection getConn() {
        return conn;
    }

    public static void setConn() throws ClassNotFoundException, SQLException {

                    forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("启动驱动");
                    conn = DriverManager.getConnection(URL,USER,null);
                    System.out.println("创建连接");
                   statement = conn.createStatement();
                    System.out.println("连接成功");
                }

    public static ResultSet getResultSet(String sql) throws SQLException {
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }
    public void submitPatientInfo(PersonInfo personInfo) throws SQLException {
        String name = personInfo.getName();
        String sex = personInfo.getSex();
        String age = personInfo.getAge();
        String height = Double.toString(personInfo.getHeight());
        String weight = Double.toString(personInfo.getWeight());
        String sql = "INSERT INTO patient_info(patient_name,patient_sex,patient_age,patient_height,patient_weight,patient_init) value('"+name+"','"+sex+"','"+age+"',"+height+","+weight+",1);";
        statement.execute(sql);
    }
    public void updatePatientInfo(PersonInfo personInfo) throws SQLException {
        String id = Integer.toString(personInfo.getId());
        String name = personInfo.getName();
        String sex = personInfo.getSex();
        String age = personInfo.getAge();
        String height = Double.toString(personInfo.getHeight());
        String weight = Double.toString(personInfo.getWeight());
        String sql = "update patient_info set patient_name='"+name+"',patient_sex='"+sex+"',patient_age='"+age+"',patient_height='"+height+"',patient_weight='"+weight+"',patient_init=2 where patient_id='"+id+"'";
        statement.execute(sql);
    }
}
