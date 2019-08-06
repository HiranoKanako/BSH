package jp.co.pscsrv.dto;

import java.sql.Timestamp;

public class Staff {
	private int no;
	private String pass;
	private String name;
	private int age;
	private String gender;
	private String registDate;
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
	public String getRegistDate() {
		return registDate;
	}
	public void setRegistDate(String registDate) {
		this.registDate = registDate;
	}
	public Timestamp getLastUpDay() {
		return lastUpDay;
	}
	public void setLastUpDay(Timestamp lastUpDay) {
		this.lastUpDay = lastUpDay;
	}
}
