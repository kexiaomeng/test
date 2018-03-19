import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.PreparedStatement;


public class JdbcTest {
	private static final String DB_URL="jdbc:mysql://localhost/hibrenate";
	private static final String DB_USER = "root";
	private static final String USER_PWD = "root";
	public static void main(String[] args) throws SQLException {
		Connection con = getConnection();
		String sql = "select id ,name from user where id= ?";
		String sql1 = "insert into user(id,name) values(?,?)";
		PreparedStatement  st1 = con.prepareStatement(sql1);
		st1.setInt(1, 1);
		ResultSet rt = null;
		try {
			con.setAutoCommit(false);
			for(int i=20;i<22;i++){
				st1.setInt(1, i);
				st1.setString(2,"sunmeng");
				st1.addBatch();
			}
			st1.addBatch("insert into user(id,name) values(\"hello\",111)");
			
			int temp[] = st1.executeBatch();
			System.out.println("¸üÐÂÁË "+temp.length);
			con.commit();
			
		} catch (SQLException e) {
			con.rollback();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		while(rt.next()){
//			int id = rt.getInt(1);
//			String name = rt.getString(2);
//			System.out.println(id+"   "+name);
//		}
//		
			
		st1.close();
		con.close();
	}
	
	public static Connection getConnection(){
		Connection con = null;
		
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			con = DriverManager.getConnection(DB_URL, DB_USER, USER_PWD);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return con;
	}
}



