package jp.co.pscsrv.dto;

public class OrderProduct {
	//注文詳細テーブル
	private String collectNo;
	private int price;
	private int buyCount;

	//商品テーブル
	private String productCode;
	private String productName;
	private String maker;

	public String getCollectNo() {
		return collectNo;
	}

	public void setCollectNo(String collectNo) {
		this.collectNo = collectNo;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}
}
