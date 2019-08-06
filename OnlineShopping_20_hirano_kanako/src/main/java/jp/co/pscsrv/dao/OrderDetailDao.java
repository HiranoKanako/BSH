package jp.co.pscsrv.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import jp.co.pscsrv.dto.OrderDetail;

@Repository
public class OrderDetailDao {
	// NamedParameterJDBC Templateを注入
	@Autowired
	private NamedParameterJdbcTemplate namedPramTemplate;

	// JDBC Templateを注入
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int insert(OrderDetail orderDetail)throws Exception{
		// SQLクエリ、バインド変数
		String sql = "INSERT INTO ONLINE_ORDER_LIST" +
				"(COLLECT_NO,PRODUCT_CODE,ORDER_COUNT,ORDER_PRICE) " +
				"VALUES (:collectNo,:productCode,:count,:price);";

		// パラメータをバインド
		MapSqlParameterSource paramMap = new MapSqlParameterSource()
				.addValue("collectNo", orderDetail.getCollectNo())
				.addValue("productCode", orderDetail.getProductCode())
				.addValue("count", orderDetail.getOrderCount())
				.addValue("price", orderDetail.getPrice());

		//クエリ実行
		int result = this.namedPramTemplate.update(sql, paramMap);

		if(result != 1) {
			throw new Exception();
		}

		return result;
	}
}
