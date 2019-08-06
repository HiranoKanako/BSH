package jp.co.pscsrv.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import jp.co.pscsrv.dto.OrderProduct;

@Repository
public class OrderProductDao {
	// JDBC Templateを注入
	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	//履歴検索情報マッピング(内部クラス)
	private class OrderProductRowMapper extends BeanPropertyRowMapper<OrderProduct> {

		// mapRowメソッドにマッピングを書く
		@Override
		public OrderProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
			OrderProduct orderProduct = new OrderProduct();
			orderProduct.setProductCode(rs.getString("op.PRODUCT_CODE"));
			orderProduct.setProductName(rs.getString("op.PRODUCT_NAME"));
			orderProduct.setMaker(rs.getString("op.MAKER"));
			orderProduct.setPrice(rs.getInt("op.UNIT_PRICE"));
			orderProduct.setBuyCount(rs.getInt("ool.ORDER_COUNT"));
			orderProduct.setCollectNo(rs.getString("ool.COLLECT_NO"));
			return orderProduct;
		}
	}

	/**
	 * 購入商品詳細検索
	 */
	public List<OrderProduct> searchDetail(String no) {

		// SQLクエリ、バインド変数
		String sql = "select * from online_order_list ool "
				+ "INNER JOIN online_product op ON ool.PRODUCT_CODE = op.PRODUCT_CODE "
				+ "WHERE ool.COLLECT_NO = :no;";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("no", no);

		// クエリ実行
		List<OrderProduct> orderProductList = this.npJdbcTemplate.query(sql, param, new OrderProductRowMapper());

		return orderProductList;
	}
}
