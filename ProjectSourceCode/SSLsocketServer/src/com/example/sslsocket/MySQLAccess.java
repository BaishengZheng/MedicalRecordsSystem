package com.example.sslsocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLAccess {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
	private AESEncryptDecrypt aes;


    public void DBInitialization() {
        try {
    	
        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.jdbc.Driver");
        
        // Setup the connection with the DB
        String host = "jdbc:mysql://localhost/medication";
        String username = "admin";
        String password = "admin";
		connect = DriverManager.getConnection(host, username, password);

		statement = connect.createStatement();
		
		aes = new AESEncryptDecrypt();
    
        } catch (Exception e) {
        	e.printStackTrace();
        } 

    }

  	
  	@SuppressWarnings("finally")
	public MedRecordsPackage getAllMedRecords() {
    	MedRecordsPackage mrp = new MedRecordsPackage();
    	try {
        	resultSet = statement.executeQuery("select * from medicalrecords_cipher");
			int id = 0;
			String name = null;
			int age = 0;
			String sex = null;
			String medical_allergies = null;
			int ssn = 0;
			String insurance_company = null;
			String userid = null;
					
			while (resultSet.next()) {

		        id = resultSet.getInt("id");
		        name = aes.Decrypt(resultSet.getString("name"));
		        age = Integer.parseInt(aes.Decrypt(resultSet.getString("age")));
		        sex = resultSet.getString("sex");
		        medical_allergies = aes.Decrypt(resultSet.getString("medical_allergies"));
		        ssn = Integer.parseInt(aes.Decrypt(resultSet.getString("ssn")));
		        insurance_company = aes.Decrypt(resultSet.getString("insurance_company"));
		        userid = resultSet.getString("userid");	      
		      
		        MedRecord mr = new MedRecord(id, name, age, sex, medical_allergies, ssn, insurance_company, userid);
		        mrp.addMedRecord(mr);		      
			}	    	
	    } catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return mrp;
		}
  	}
  
  
  	@SuppressWarnings("finally")
	public MedRecord getOneMedRecord(String userIdStr) {
  		MedRecord oneMR = null;
  		try {
	    	resultSet = statement.executeQuery("select * from medicalrecords_cipher where userid = '" + userIdStr + "'");
			while (resultSet.next()) {
				
				int id = resultSet.getInt("id");
			    String name = aes.Decrypt(resultSet.getString("name"));
			    int age = Integer.parseInt(aes.Decrypt(resultSet.getString("age")));
			    String sex = resultSet.getString("sex");
			    String medical_allergies = aes.Decrypt(resultSet.getString("medical_allergies"));
			    int ssn = Integer.parseInt(aes.Decrypt(resultSet.getString("ssn")));
			    String insurance_company = aes.Decrypt(resultSet.getString("insurance_company"));
			    String userid = resultSet.getString("userid");
				
			    oneMR = new MedRecord(id, name, age, sex, medical_allergies, ssn, insurance_company, userid);

			}
	    } catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return oneMR;
		}
  	}
  	
  	public void updateMedRecord(MedRecord MR) {
  		int id = MR.getId();
  		String sql = "update medicalrecords_cipher set name = ?, age = ?, sex = ?, medical_allergies = ?, ssn = ?, insurance_company = ? where id = ?";
		
		try {
			preparedStatement = connect.prepareStatement(sql);
			preparedStatement.setString(1, aes.Encrypt(MR.getName()));
			preparedStatement.setString(2, aes.Encrypt(String.valueOf(MR.getAge())));
			preparedStatement.setString(3, MR.getSex());
			preparedStatement.setString(4, aes.Encrypt(MR.getMedAllergy()));
			preparedStatement.setString(5, aes.Encrypt(String.valueOf(MR.getSsn())));
			preparedStatement.setString(6, aes.Encrypt(MR.getInsuranceCompany()));
			preparedStatement.setInt(7, id);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
  	}
  
  
    @SuppressWarnings("finally")
	public String getPasswordFromDB(String userIdStr) {
    	String res = null;
	    try {
	    	resultSet = statement.executeQuery("select password from passwords_cipher where userid = '" + userIdStr + "'");
			while (resultSet.next()) {
				res = aes.Decrypt(resultSet.getString("password"));
			}
	    } catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return res;
		}
    }
    
    @SuppressWarnings("finally")
	public String getPriorityFromDB(String userIdStr) {
    	String res = null;
	    try {
	    	resultSet = statement.executeQuery("select priority from passwords_cipher where userid = '" + userIdStr + "'");
			while (resultSet.next()) {
				res = resultSet.getString("priority");
			}
	    } catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return res;
		}
    }

    // Close some stuff when do not need
    public void DBclose() {
	    try {
	        if (resultSet != null) {
	            resultSet.close();
	        }
	        if (statement != null) {
	            statement.close();
	        }
	        if (connect != null) {
	            connect.close();
	        }      
	        if (preparedStatement != null) {
	        	preparedStatement.close();
	        }
	    } catch (Exception e) {
	    	  e.printStackTrace();
	    }
    }


} 