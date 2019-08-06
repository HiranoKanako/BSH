package jp.co.pscsrv.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderListDao {
	// JDBC Templateを注入
	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// 削除検索件数
	public Integer countByDeleteProduct(String no) {

		// SQL文生成
		String sql = "select count(*) from ONLINE_ORDER_LIST WHERE COLLECT_NO = :no;";

		// パラメータをバインド
		MapSqlParameterSource param = new MapSqlParameterSource().addValue("no", no);

		// SQL実行
		Integer count = this.npJdbcTemplate.queryForObject(sql, param, Integer.class);
		return count;
	}

	/**
	 * 購入履歴の削除(物理削除)
	 */
	public int deleteOrderList(String no) throws Exception {
		// SQLクエリ、バインド変数
		String sql = "DELETE FROM ONLINE_ORDER_LIST WHERE COLLECT_NO = :no;";

		// パラメータをバインド
		MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue("no", no);

		int result = this.npJdbcTemplate.update(sql, paramMap);

		//クエリ実行
		return result;
	}
}
