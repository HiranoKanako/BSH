package jp.co.pscsrv.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {
	// JDBC Templateを注入
		@Autowired
		private NamedParameterJdbcTemplate npJdbcTemplate;

		@Autowired
		private JdbcTemplate jdbcTemplate;

		/**
		 * 購入履歴の削除(物理削除)
		 */
		public int deleteOrder(String no) throws Exception{
			// SQLクエリ、バインド変数
			String sql = "DELETE FROM ONLINE_ORDER WHERE COLLECT_NO = :no;";

			// パラメータをバインド
			MapSqlParameterSource paramMap = new MapSqlParameterSource().addValue("no", no);

			int result = this.npJdbcTemplate.update(sql, paramMap);

			if(result != 1) {
				throw new Exception();
			}

			//クエリ実行
			return result;
		}
}
