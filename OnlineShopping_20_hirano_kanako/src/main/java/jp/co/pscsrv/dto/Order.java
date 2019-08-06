package jp.co.pscsrv.dto;

public class Order {
	private int orderNo;
	private int memberNo;
	private int total;
	private int tax;
	private String orderDate;
	private String collectNo;

	public int getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	public int getMemberNo() {
		return memberNo;
	}
	public void setMemberNo(int memberNo) {
		this.memberNo = memberNo;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
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
