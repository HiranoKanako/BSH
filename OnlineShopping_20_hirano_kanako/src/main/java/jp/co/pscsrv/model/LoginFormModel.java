package jp.co.pscsrv.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class LoginFormModel {

	@NotEmpty(message="「会員NOは必ず入力してください。」")
	@Pattern(regexp = "^[0-9]+$", message = "「会員NOは半角数字で入力してください。」")
	private String memberNo;

	@NotEmpty(message="「パスワードは必ず入力してください。」")
	@Size(min = 1, max = 8, message="「パスワードは{max}桁以下で入力して下さい。」")
	@Pattern(regexp = "^[0-9a-zA-Z]+$", message="「パスワードは半角英数字で入力してください。」")
	private String pass;

	public String getMemberNo() {
		return memberNo;
	}
	public void setMemberNo(String memberNo) {
		this.memberNo = memberNo;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
}
