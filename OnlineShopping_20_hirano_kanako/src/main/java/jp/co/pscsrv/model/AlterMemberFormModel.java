package jp.co.pscsrv.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AlterMemberFormModel {

	private String no;

	@NotEmpty(message = "「氏名は必ず入力してください。」")
	@Size(min = 1, max = 20, message = "「氏名には{max}桁以内で入力して下さい。」")
	private String name;

	@Size(min = 0, max = 8, message = "「パスワードには{max}桁以内で入力して下さい。」")
	@Pattern(regexp = "^[0-9a-zA-Z]*$", message="「パスワードは必ず半角英数字で入力してください。」")
	private String pass;

	@Size(min = 0, max = 8, message = "「パスワード(確認用)には{max}桁以内で入力して下さい。」")
	@Pattern(regexp = "^[0-9a-zA-Z]*$", message="「パスワード(確認用)は必ず半角英数字で入力してください。」")
	private String passConf;

	private String prevPass;

	@NotEmpty(message = "「年齢は必ず入力してください。」")
	@Pattern(regexp = "^[0-9]+$", message = "「年齢は必ず半角数字で入力してください。」")
	@Pattern(regexp = "(0|[1-9]\\d*)", message = "「年齢には正の数で入力してください。」")
	private String age;

	private String gender;

	@Pattern(regexp = "(\\s*|^\\d{3}-\\d{4}$)", message = "「郵便番号はXXX-XXXXの形式で入力してください。」")
	private String postal;

	@Size(max = 50, message = "「住所には{max}桁以内で入力して下さい。」")
	private String address;

	@Size(max = 20, message = "「電話番号には{max}桁以内で入力して下さい。」")
	@Pattern(regexp = "(\\s*|^[0-9\\-]+$)", message = "「電話番号は必ず半角数字とハイフンで入力してください。」")
	private String phone;

	private String registDate;

	private boolean passEqual;

	@AssertTrue(message = "「パスワードとパスワード(確認用)は同じ値を入力してください。」")
	private boolean isPassEqual() {
		if (pass.equals(passConf)) {
			return true;
		}
		return false;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getPassConf() {
		return passConf;
	}

	public void setPassConf(String passConf) {
		this.passConf = passConf;
	}

	public String getPrevPass() {
		return prevPass;
	}

	public void setPrevPass(String prevPass) {
		this.prevPass = prevPass;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAge() {
		return this.age;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGender() {
		return this.gender;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	public String getPostal() {
		return this.postal;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return this.address;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return this.phone;
	}

	public String getRegistDate() {
		return registDate;
	}

	public void setRegistDate(String registDate) {
		this.registDate = registDate;
	}


	public void setPassEqual(boolean passEqual) {
		this.passEqual = passEqual;
	}

	public boolean getPassEqual() {
		return passEqual;
	}
}
