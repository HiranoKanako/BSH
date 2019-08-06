package jp.co.pscsrv.bean;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class SearchConditionBean {
	// where句のSQL文
	private String sql;

	// where句にバインドするパラメータマップ
	private MapSqlParameterSource paramMap;

	// 引数無しのコンストラクタ
	public SearchConditionBean() {
		super();
	}

	// 引数ありのコンストラクタ
	public SearchConditionBean(String sql, MapSqlParameterSource paramMap) {
		super();
		this.sql = sql;
		this.paramMap = paramMap;
	}

	// 以下、アクセサ

	/**
	 * @return sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql セットする sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @return paramMap
	 */
	public MapSqlParameterSource getParamMap() {
		return paramMap;
	}

	/**
	 * @param paramMap セットする paramMap
	 */
	public void setParamMap(MapSqlParameterSource paramMap) {
		this.paramMap = paramMap;
	}
}
