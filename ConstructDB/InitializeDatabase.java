package com.example.mysql;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;


public class TestMySQLDB {
	private String className = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost/medication";
	private String username = "admin";
	private String password = "admin";
	private java.sql.Connection conn;	
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private PreparedStatement preparedStatement;
	private AESEncryptDecrypt aes;
	
	public static void main(String[] args) {
		new TestMySQLDB().start();
	}
	
	public void start() {	
		//connect();
		
		getConn();
		
		try {
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try {
			aes = new AESEncryptDecrypt();	
			
			//create table medicalrecords_cipher
			stmt.executeUpdate("create table medicalrecords_cipher (id int primary key, name varchar(100), age varchar(50), sex varchar(10), medical_allergies varchar(200), ssn varchar(100), insurance_company varchar(100), userid varchar(50))");
			
			//create table passwords_cipher
			stmt.executeUpdate("create table passwords_cipher (userid varchar(50) primary key, password varchar(300), priority varchar(50))");
			
			//insert into table medicalrecords_cipher
			preparedStatement = conn.prepareStatement("insert into medicalrecords_cipher values (?, ?, ?, ?, ?, ?, ?, ?)");
			
			//1st medical record
			preparedStatement.setInt(1, 1);
		    preparedStatement.setString(2, aes.Encrypt("Susan Hoover"));
		    preparedStatement.setString(3, aes.Encrypt("27"));
		    preparedStatement.setString(4, "female");
		    preparedStatement.setString(5, aes.Encrypt("Allergic rhinitis"));
		    preparedStatement.setString(6, aes.Encrypt("123456789"));
		    preparedStatement.setString(7, aes.Encrypt("Aetna"));
		    preparedStatement.setString(8, "susan");
		    preparedStatement.executeUpdate();
		    
		    //2nd medical record
			preparedStatement.setInt(1, 2);
		    preparedStatement.setString(2, aes.Encrypt("Bill Sunday"));
		    preparedStatement.setString(3, aes.Encrypt("40"));
		    preparedStatement.setString(4, "male");
		    preparedStatement.setString(5, aes.Encrypt("Asthma"));
		    preparedStatement.setString(6, aes.Encrypt("987654321"));
		    preparedStatement.setString(7, aes.Encrypt("Amica Mutual Insurance"));
		    preparedStatement.setString(8, "bill");
		    preparedStatement.executeUpdate();
			
		    //3rd medical record
			preparedStatement.setInt(1, 3);
		    preparedStatement.setString(2, aes.Encrypt("James Russell"));
		    preparedStatement.setString(3, aes.Encrypt("31"));
		    preparedStatement.setString(4, "male");
		    preparedStatement.setString(5, aes.Encrypt("N/A"));
		    preparedStatement.setString(6, aes.Encrypt("123789456"));
		    preparedStatement.setString(7, aes.Encrypt("Jackson National Life"));
		    preparedStatement.setString(8, "james");
		    preparedStatement.executeUpdate();
		    
		    //4th medical record
			preparedStatement.setInt(1, 4);
		    preparedStatement.setString(2, aes.Encrypt("John Wayne"));
		    preparedStatement.setString(3, aes.Encrypt("50"));
		    preparedStatement.setString(4, "male");
		    preparedStatement.setString(5, aes.Encrypt("Insect venom"));
		    preparedStatement.setString(6, aes.Encrypt("789456123"));
		    preparedStatement.setString(7, aes.Encrypt("MetLife"));
		    preparedStatement.setString(8, "john");
		    preparedStatement.executeUpdate();
		    
		    //5th medical record
			preparedStatement.setInt(1, 5);
		    preparedStatement.setString(2, aes.Encrypt("Anna Williams"));
		    preparedStatement.setString(3, aes.Encrypt("35"));
		    preparedStatement.setString(4, "female");
		    preparedStatement.setString(5, aes.Encrypt("Food allergies"));
		    preparedStatement.setString(6, aes.Encrypt("456123789"));
		    preparedStatement.setString(7, aes.Encrypt("MetLife"));
		    preparedStatement.setString(8, "anna");
		    preparedStatement.executeUpdate();
			
			
			//insert into table passwords_cipher
		    preparedStatement = conn.prepareStatement("insert into passwords_cipher values (?, ?, ?)");
			
			//1st
			preparedStatement.setString(1, "admin");
		    preparedStatement.setString(2, aes.Encrypt("admin"));
		    preparedStatement.setString(3, "admin");
		    preparedStatement.executeUpdate();
		    
		    //2nd
		    preparedStatement.setString(1, "susan");
		    preparedStatement.setString(2, aes.Encrypt("Hoover"));
		    preparedStatement.setString(3, "guest");
		    preparedStatement.executeUpdate();
			
		    //3rd 
		    preparedStatement.setString(1, "bill");
		    preparedStatement.setString(2, aes.Encrypt("Sunday"));
		    preparedStatement.setString(3, "guest");
		    preparedStatement.executeUpdate();
		    
		    //4th
		    preparedStatement.setString(1, "james");
		    preparedStatement.setString(2, aes.Encrypt("Russell"));
		    preparedStatement.setString(3, "guest");
		    preparedStatement.executeUpdate();
		    
		    //5th
		    preparedStatement.setString(1, "john");
		    preparedStatement.setString(2, aes.Encrypt("Wayne"));
		    preparedStatement.setString(3, "guest");
		    preparedStatement.executeUpdate();
		    
		    //6th 
		    preparedStatement.setString(1, "anna");
		    preparedStatement.setString(2, aes.Encrypt("Williams"));
		    preparedStatement.setString(3, "guest");
		    preparedStatement.executeUpdate();
			
			
		    
		    //get the content from table passwords_cipher and decrypt
		    rs = stmt.executeQuery("select * from passwords_cipher");
			String userid = null;
			String password = null;
			String priority = null;
			while (rs.next()) {
			      userid = rs.getString("userid");
			      password = aes.Decrypt(rs.getString("password"));
			      priority = rs.getString("priority");
			      
			      System.out.println("userid: " + userid);
			      System.out.println("password: " + password);
			      System.out.println("priority: " + priority);      
			}
			
			
			//get the content from table medicalrecords_cipher and decrypt
			rs = stmt.executeQuery("select * from medicalrecords_cipher"); 
			MedRecordsPackage mrp = getDecryptedResultSetContent(rs);
	
		} catch (SQLException e) {  
			e.printStackTrace();  
		} finally {  
			closed();
		}
	}
	
	
	
	//connect to the server using socket
	Socket s;
	boolean bConnect = false;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private FileOutputStream fos;
	
	public void connect() {
		try {
			s = new Socket("127.0.0.1", 8888);
			bConnect = true;
System.out.println("connected!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	
	
	public MedRecordsPackage getResultSetContent(ResultSet resultSet) {
		MedRecordsPackage mrp = new MedRecordsPackage();
		
		if (resultSet != null) {
			try {
				//MedRecordsPackage mrp = new MedRecordsPackage();
				int id = 0;
				String name = null;
				int age = 0;
				String sex = null;
				String medical_allergies = null;
				int ssn = 0;
				String insurance_company = null;
				String userid = null;
				
				
				
				while (resultSet.next()) {
				      // It is possible to get the columns via name
				      // also possible to get the columns via the column number
				      // which starts at 1
				      // e.g. resultSet.getSTring(2);
				      id = resultSet.getInt("id");
				      name = resultSet.getString("name");
				      age = resultSet.getInt("age");
				      sex = resultSet.getString("sex");
				      medical_allergies = resultSet.getString("medical_allergies");
				      ssn = resultSet.getInt("ssn");
				      insurance_company = resultSet.getString("insurance_company");
				      userid = resultSet.getString("userid");
				      
				      //System.out.println("id: " + id);
				      //System.out.println("name: " + name);
				      //System.out.println("sex: " + sex);
				      //System.out.println("age: " + age);
				      //System.out.println("medication allergies: " + medical_allergies);
				      //System.out.println("ssn: " + ssn);
				      //System.out.println("insurance_company: " + insurance_company);
				      //System.out.println("userid: " + userid);
				      
				      //oos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
				      
				      MedRecord mr = new MedRecord(id, name, age, sex, medical_allergies, ssn, insurance_company, userid);
				      mrp.addMedRecord(mr);
				      
				      
				}
				
				/*
				if (mrp != null) {
					oos.writeObject(mrp);
				}
				*/
				
				
				 
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			
			
		} else {
			System.out.println("the result set is null");
		}
		
		return mrp;
		
	}
	
	public MedRecordsPackage getDecryptedResultSetContent(ResultSet resultSet) {
		MedRecordsPackage mrp = new MedRecordsPackage();
		
		if (resultSet != null) {
			try {
				//MedRecordsPackage mrp = new MedRecordsPackage();
				int id = 0;
				String name = null;
				int age = 0;
				String sex = null;
				String medical_allergies = null;
				int ssn = 0;
				String insurance_company = null;
				String userid = null;
				
				
				
				while (resultSet.next()) {
				      // It is possible to get the columns via name
				      // also possible to get the columns via the column number
				      // which starts at 1
				      // e.g. resultSet.getSTring(2);
				      id = resultSet.getInt("id");
				      name = aes.Decrypt(resultSet.getString("name"));
				      age = Integer.parseInt(aes.Decrypt(resultSet.getString("age")));
				      sex = resultSet.getString("sex");
				      medical_allergies = aes.Decrypt(resultSet.getString("medical_allergies"));
				      ssn = Integer.parseInt(aes.Decrypt(resultSet.getString("ssn")));
				      insurance_company = aes.Decrypt(resultSet.getString("insurance_company"));
				      userid = resultSet.getString("userid");
				      
				      System.out.println("id: " + id);
				      System.out.println("name: " + name);
				      System.out.println("sex: " + sex);
				      System.out.println("age: " + age);
				      System.out.println("medication allergies: " + medical_allergies);
				      System.out.println("ssn: " + ssn);
				      System.out.println("insurance_company: " + insurance_company);
				      System.out.println("userid: " + userid);
				      
				      //oos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
				      
				      MedRecord mr = new MedRecord(id, name, age, sex, medical_allergies, ssn, insurance_company, userid);
				      mrp.addMedRecord(mr);
				      
				      
				}
				
				/*
				if (mrp != null) {
					oos.writeObject(mrp);
				}
				*/
				
				
				 
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			
			
		} else {
			System.out.println("the result set is null");
		}
		
		return mrp;
		
	}

	
	//register the DriverManager
	public void loadDrive() {
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//get connection to database(using Oracle here)
	public void getConn() {
		loadDrive();
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	//method that closing the resources	
	public void closed() {
		try { 
			
			if(rs != null) {  
				rs.close();  
				rs = null;  
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
						
			}
			if(stmt != null) {  
				stmt.close();  
				stmt = null;  
			}  
			if(conn != null) {  
				conn.close();  
				conn = null;  
			}  
		} catch (SQLException e) {  
			e.printStackTrace();  
		} 		
	}	
	
}
