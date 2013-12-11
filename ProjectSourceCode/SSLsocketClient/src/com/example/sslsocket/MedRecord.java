package com.example.sslsocket;
import java.io.Serializable;

class MedRecord implements Serializable {
	private int id;
	private String name;
	private int age;
	private String sex;
	private String medAllergy;
	private int ssn;
	private String insuranceCompany;
	private String userid;
	
	MedRecord(int _id, String _name, int _age, String _sex, String _medAllergy, int _ssn, String _insuranceCompany, String _userid) {
		id = _id;
		name = _name;
		age = _age;
		sex = _sex;
		medAllergy = _medAllergy;
		ssn = _ssn;
		insuranceCompany = _insuranceCompany;
		userid = _userid;
	}
	
	//get id, name, age, sex, medAllergy, ssn, insuranceCompany
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getAge() {
		return age;
	}
	
	public String getSex() {
		return sex;
	}
	
	public String getMedAllergy() {
		return medAllergy;
	}
	
	public int getSsn() {
		return ssn;
	}
	
	public String getInsuranceCompany() {
		return insuranceCompany;
	}
	
	public String getUserid() {
		return userid;
	}
	
	
	//set id, name, age, sex, medAllergy, ssn, insuranceCompany
	public void setId(int _id) {
		id = _id;
	}
	
	public void setName(String _name) {
		name = _name;
	}
	
	public void setAge(int _age) {
		age = _age;
	}
	
	public void setSex(String _sex) {
		sex = _sex;
	}
	
	public void setMedAllergy(String _medAllergy) {
		medAllergy = _medAllergy;
	}
	
	public void setSsn(int _ssn) {
		ssn = _ssn;
	}
	
	public void setInsuranceCompany(String _insuranceCompany) {
		insuranceCompany = _insuranceCompany;
	}
	
	public void setUserid(String _userid) {
		userid = _userid;
	}
	
	

}
