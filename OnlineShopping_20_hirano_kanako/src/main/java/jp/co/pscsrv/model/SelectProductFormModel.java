package jp.co.pscsrv.model;

public class SelectProductFormModel {
	/**
	 * 商品選択用テーブルの選択チェックボックスのリスト
	 * テーブルの行インデックスを保持する。
	 * 選択したチェックボックスのインデックスのみ格納される。
	 *
	 */
	private String[] productsSelect;

	/**
	 * 商品選択用テーブルの商品番号（非表示項目）のリスト
	 */
	private String[] productCode;

	/**
	 * 商品選択テーブルの数量のリスト
	 */
	private String[] productsCnt;

	public String[] getProductsSelect() {
		String[] ret = null;
		if (this.productsSelect != null) {
			ret = this.productsSelect;
		}
		return ret;
	}

	public void setProductsSelect(String[] productsSelect) {
		this.productsSelect = productsSelect;
	}

	public String[] getProductCode() {
		return productCode;
	}

	public void setProductCode(String[] productCode) {
		this.productCode = productCode;
	}

	public String[] getproductsCnt() {
		return productsCnt;
	}

	public void setProductsCnt(String[] productsCnt) {
		this.productsCnt = productsCnt;
	}
}
