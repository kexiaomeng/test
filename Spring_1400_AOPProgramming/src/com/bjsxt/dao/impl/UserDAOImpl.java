package com.bjsxt.dao.impl;







import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.bjsxt.dao.UserDAO;
import com.bjsxt.model.User;


public class UserDAOImpl implements UserDAO {

	private DataSource dataSource;                         
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public void save(User user) {
		//Hibernate
		//JDBC
		//XML
		//NetWork
		Connection connection;
		try {
			connection = dataSource.getConnection();
			connection.createStatement().executeUpdate("insert into user values(null,'zhangsna')");
			connection.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("save start...");
		System.out.println("user saved!");
	}

}
