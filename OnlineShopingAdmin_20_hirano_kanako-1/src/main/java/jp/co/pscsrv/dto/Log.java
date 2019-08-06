package jp.co.pscsrv.dto;

public class Log {
	//会員テーブル
	private int memberNo;
	private String memberName;
	private String tel;

	//注文テーブル
	private int orderNo;
	private int money;
	private int tax;
	private String orderDate;
	private String collectNo;

	public int getMemberNo() {
		return memberNo;
	}

	public void setMemberNo(int memberNo) {
		this.memberNo = memberNo;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getTax() {
		return tax;
	}

	public void setTax(int tax) {
		this.tax = tax;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getCollectNo() {
		return collectNo;
	}

	public void setCollectNo(String collectNo) {
		this.collectNo = collectNo;
	}
}
