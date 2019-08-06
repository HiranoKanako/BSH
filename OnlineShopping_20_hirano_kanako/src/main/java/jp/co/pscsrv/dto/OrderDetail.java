package jp.co.pscsrv.dto;

public class OrderDetail {
	private int listNo;
	private String collectNo;
	private String productCode;
	private int orderCount;
	private int price;

	public int getListNo() {
		return listNo;
	}
	public void setListNo(int listNo) {
		this.listNo = listNo;
	}
	public String getCollectNo() {
		return collectNo;
	}
	public void setCollectNo(String collectNo) {
		this.collectNo = collectNo;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
