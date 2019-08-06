package jp.co.pscsrv.dto;

import java.sql.Timestamp;

public class Member {
	private int no;
	private String pass;
	private String name;
	private int age;
	private String gender;
	private String postal;
	private String address;
	private String phone;
	private String registDate;
	private String delteFlag;
	private int lastUpStaff;
	private Timestamp lastUpDay;


	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPostal() {
		return postal;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRegistDate() {
		return registDate;
	}

	public void setRegistDate(String registDate) {
		this.registDate = registDate;
	}


	public String getDelteFlag() {
		return delteFlag;
	}

	public void setDelteFlag(String delteFlag) {
		this.delteFlag = delteFlag;
	}


	public int getLastUpStaff() {
		return lastUpStaff;
	}

	public void setLastUpStaff(int lastUpStaff) {
		this.lastUpStaff = lastUpStaff;
	}

	public Timestamp getLastUpDay() {
		return lastUpDay;
	}

	public void setLastUpDay(Timestamp lastUpDay) {
		this.lastUpDay = lastUpDay;
	}
}
