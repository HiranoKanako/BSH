package jp.co.pscsrv.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;

public class SearchFormModel {
	private String category;
	private String productName;
	private String maker;

	@Pattern(regexp = "^[0-9]*$", message = "「金額上限は必ず半角数字で入力してください。」")
	//@Pattern(regexp = "(0|[1-9]\\d*)", message="「金額上限には正の数で入力してください。」")
	private String priceMax;

	@Pattern(regexp = "^[0-9]*$", message = "「金額下限は必ず半角数字で入力してください。」")
	//@Pattern(regexp = "(0|[1-9]\\d*)", message="「金額下限には正の数で入力してください。」")
	private String priceMin;

	private boolean positivePriceMax;
	private boolean positivePriceMin;
	private boolean priceIntegrity;

	@AssertTrue(message = "「金額上限には正の数で入力してください。」")
	private boolean isPositivePriceMax() {
		if (priceMax.equals("")) {
			return true;
		}
		try {
			if (Integer.parseInt(priceMax) > 0) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	@AssertTrue(message = "「金額下限には正の数で入力してください。」")
	private boolean isPositivePriceMin() {
		if (priceMin.equals("")) {
			return true;
		}
		try {
			if (Integer.parseInt(priceMin) > 0) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	@AssertTrue(message = "「金額上限は金額下限より大きい値を入力してください。」")
	private boolean isPriceIntegrity() {
		try {
			if (priceMax.equals("") || priceMin.equals("")) {
				return true;
			}

			if (Integer.parseInt(priceMax) > Integer.parseInt(priceMin)) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(String priceMax) {
		this.priceMax = priceMax;
	}

	public String getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(String priceMin) {
		this.priceMin = priceMin;
	}

	public void setPositivePriceMax(boolean positivePriceMax) {
		this.positivePriceMax = positivePriceMax;
	}

	public boolean getPositivePriceMax() {
		return positivePriceMax;
	}

	public void setPositivePriceMin(boolean positivePriceMin) {
		this.positivePriceMin = positivePriceMin;
	}

	public boolean getPositivePriceMin() {
		return positivePriceMin;
	}

	public void setPriceIntegrity(boolean priceIntegrity) {
		this.priceIntegrity = priceIntegrity;
	}

	public boolean getPriceIntegrity() {
		return priceIntegrity;
	}
}
