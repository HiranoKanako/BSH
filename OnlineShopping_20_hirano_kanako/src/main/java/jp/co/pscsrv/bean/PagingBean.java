package jp.co.pscsrv.bean;

public class PagingBean {
	// 1ページの最大表示件数
	public static final int MAX_DISP = 10;

	// 検索件数(総数)
	private Integer searchCount;

	// 現在のページ
	private Integer currentPage;

	// 最後のページ
	private Integer endPage;

	// コンストラクタ
	public PagingBean() {
		currentPage = 0;
		currentPage = 1;
		endPage = 1;
	}

	/** ↓ココから↓　仮のgetter(存在しないフィールドのgetter)　↓ココから↓ **/

	// SQLのlimit句の抽出開始件数を算出（抽出件数はMAX_DISP）
	public Integer getStart() {
		if (currentPage == 1) {
			return 0;
		} else {
			return (currentPage - 1) * MAX_DISP;
		}
	}

	// 次のページを返すgetter
	public Integer getNext() {
		if (currentPage < endPage) {
			// 現在のページが最後のページより小さい場合、次のページ(現在のページ+1)を返す
			return currentPage + 1;
		} else {
			// 現在のページが最後のページと同じか大きい場合、画面遷移しない
			return null;
		}
	}

	// 前のページを返すgetter
	public Integer getBack() {
		if (currentPage > 1) {
			// 現在のページが最初のページより大きい場合、前のページ(現在のページ-1)を返す
			return currentPage - 1;
		} else {
			// 現在のページが最初のページと同じか小さい場合、画面遷移しない
			return null;
		}
	}

	// 最初のページを返すgetter
	public Integer getFirst() {
		if (currentPage == 1) {
			// 現在のページが1の場合、最初のページと現在のページが同じなので画面遷移しない
			return null;
		} else {
			// 現在のページが1以外の場合、最初のページ(1ページ)を返す
			return 1;
		}
	}

	// 最後のページを返すgetter
	public Integer getLast() {
		if (currentPage < endPage) {
			// 現在のページが最後のページより小さい場合、最後のページを返す
			return endPage;
		} else {
			// 現在のページが最後のページと同じか大きい場合、画面遷移しない
			return null;
		}
	}
	/** ↑ココまで↑　仮のgetter(存在しないフィールドのgetter)　↑ココまで↑ **/

	// 以下、アクセサ
	/**
	 * @param searchCount セットする searchCount
	 */
	public void setSearchCount(Integer searchCount) {
		this.searchCount = searchCount;

		// 検索件数を更新するタイミングで、最大ページも算出する
		if (this.searchCount == null || this.searchCount == 0) {
			endPage = 1;
		} else {
			// 検索件数が 11～20の場合、最大ページは 2ページ
			// (n*10)+1～((n+1)*10)+0の場合、最大ページはnページ
			endPage = (this.searchCount - 1) / MAX_DISP + 1;
		}

		// 現在ページが最大ページより大きい場合、現在ページを更新
		if (endPage < currentPage) {
			currentPage = endPage;
		}
	}

	/**
	 * @return searchCount
	 */
	public Integer getSearchCount() {
		return searchCount;
	}

	/**
	 * @return currentPage
	 */
	public Integer getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage セットする currentPage
	 */
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @return endPage
	 */
	public Integer getEndPage() {
		return endPage;
	}

	/**
	 * @param endPage セットする endPage
	 */
	public void setEndPage(Integer endPage) {
		this.endPage = endPage;
	}
}
