package jp.co.pscsrv.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import jp.co.pscsrv.dto.Order;

@Repository
public class OrderDao {
	// NamedParameterJDBC Templateを注入
	@Autowired
	private NamedParameterJdbcTemplate namedPramTemplate;

	// JDBC Templateを注入
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int insert(Order order) throws Exception{
		// SQLクエリ、バインド変数
		String sql = "INSERT INTO ONLINE_ORDER" +
				"(MEMBER_NO,TOTAL_MONEY,TOTAL_TAX,ORDER_DATE,COLLECT_NO) " +
				"VALUES (:memberNo,:total,:tax,:date,:collectNo);";

		// パラメータをバインド
		MapSqlParameterSource paramMap = new MapSqlParameterSource()
				.addValue("memberNo", order.getMemberNo())
				.addValue("total", order.getTotal())
				.addValue("tax", order.getTax())
				.addValue("date", order.getOrderDate())
				.addValue("collectNo", order.getCollectNo());

		//クエリ実行
		int result = this.namedPramTemplate.update(sql, paramMap);

		if(result != 1) {
			throw new Exception();
		}

		return result;
	}
}
