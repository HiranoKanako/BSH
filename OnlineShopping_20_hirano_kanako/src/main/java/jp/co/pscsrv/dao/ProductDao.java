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

import jp.co.pscsrv.bean.SearchConditionBean;
import jp.co.pscsrv.dto.Product;
import jp.co.pscsrv.model.SearchFormModel;

@Repository
public class ProductDao {

	// JDBC Templateを注入
	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	//商品情報マッピング(内部クラス)
	private class ProductRowMapper extends BeanPropertyRowMapper<Product> {

		// mapRowメソッドにマッピングを書く
		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setProductCode(rs.getString("PRODUCT_CODE"));
			product.setCategoryId(rs.getInt("CATEGORY_ID"));
			product.setProductName(rs.getString("PRODUCT_NAME"));
			product.setMaker(rs.getString("MAKER"));
			product.setStockCount(rs.getInt("STOCK_COUNT"));
			product.setRegisterDate(rs.getString("REGISTER_DATE"));
			product.setPrice(rs.getInt("UNIT_PRICE"));
			product.setPictureName(rs.getString("PICTURE_NAME"));
			product.setMemo(rs.getString("MEMO"));
			product.setDeleteFlag(rs.getString("DELETE_FLG"));
			return product;
		}
	}

	/**
	 * 複合条件検索件数（ページングなし）
	 * @param searchFormModel
	 * @return
	 */
	public List<Product> search(SearchFormModel searchFormModel) {
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT * FROM ONLINE_PRODUCT ");

		StringBuilder sbWhere = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		if (!searchFormModel.getCategory().isEmpty() && Integer.parseInt(searchFormModel.getCategory()) != 0) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			}
			sbWhere.append("CATEGORY_ID = :category ");
			paramMap.addValue("category", searchFormModel.getCategory());
		}

		if (!searchFormModel.getProductName().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("PRODUCT_NAME LIKE :word ");
			paramMap.addValue("word", "%" + searchFormModel.getProductName() + "%");
		}

		if (!searchFormModel.getMaker().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("MAKER LIKE :maker ");
			paramMap.addValue("maker", "%" + searchFormModel.getMaker() + "%");
		}

		if (!searchFormModel.getPriceMin().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append(":goodsLowerPrice <= UNIT_PRICE ");
			paramMap.addValue("goodsLowerPrice", Integer.parseInt(searchFormModel.getPriceMin()));
		}

		if (!searchFormModel.getPriceMax().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("UNIT_PRICE <= :goodsUpperPrice ");
			paramMap.addValue("goodsUpperPrice", Integer.parseInt(searchFormModel.getPriceMax()));
		}

		if (sbWhere.length() == 0) {
			sbWhere.append("WHERE ");
		} else {
			sbWhere.append("AND ");
		}
		sbWhere.append("DELETE_FLG = 0 ");

		sbSQL.append(sbWhere);

		// クエリ実行
		List<Product> productList = this.npJdbcTemplate.query(sbSQL.toString(), paramMap, new ProductRowMapper());
		return productList;

	}

	/**
	 * 全件検索
	 * @return List<Product>
	 */
	public List<Product> findAll() {
		// SQLクエリ、バインド変数
		String sql = " SELECT * FROM ONLINE_PRODUCT AND DELETE_FLG = 0;";

		// クエリ実行
		List<Product> priductList = this.npJdbcTemplate.query(sql, new ProductRowMapper());

		return priductList;
	}

	/**
	 * 1件検索(削除フラグあり)
	 * @return List<Product>
	 */
	public Product findOne(String code) {
		// SQLクエリ、バインド変数
		String sql = " SELECT * FROM ONLINE_PRODUCT WHERE PRODUCT_CODE = :code AND DELETE_FLG = 0;";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("code", code);

		// クエリ実行
		List<Product> productList = this.npJdbcTemplate.query(sql, param, new ProductRowMapper());

		if (productList.size() != 1) {
			return null;
		}

		return productList.get(0);
	}

	/**
	 * 1件検索(削除フラグなし)
	 * @return List<Product>
	 */
	public Product findOneNoFlag(String code) {
		// SQLクエリ、バインド変数
		String sql = " SELECT * FROM ONLINE_PRODUCT WHERE PRODUCT_CODE = :code;";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("code", code);

		// クエリ実行
		List<Product> productList = this.npJdbcTemplate.query(sql, param, new ProductRowMapper());

		if (productList.size() != 1) {
			return null;
		}

		return productList.get(0);
	}

	/**
	 * 取り扱っていない商品を検索
	 * @return List<Product>
	 */
	public Product deleteFindOne(String code) {
		// SQLクエリ、バインド変数
		String sql = " SELECT * FROM ONLINE_PRODUCT WHERE PRODUCT_CODE = :code AND DELETE_FLG = 1;";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("code", code);

		// クエリ実行
		List<Product> productList = this.npJdbcTemplate.query(sql, param, new ProductRowMapper());

		if (productList.size() != 1) {
			return null;
		}

		return productList.get(0);
	}

	/**
	 * 在庫数の変更
	 */
	public int update(String code, int newStock) throws Exception {
		// SQLクエリ、バインド変数
		String sql = "UPDATE ONLINE_PRODUCT SET STOCK_COUNT =:stock WHERE PRODUCT_CODE= :code AND DELETE_FLG = 0;";

		// パラメータをバインド
		MapSqlParameterSource paramMap = new MapSqlParameterSource()
				.addValue("stock", newStock)
				.addValue("code", code);

		//クエリ実行
		int result = this.npJdbcTemplate.update(sql, paramMap);

		if (result != 1) {
			throw new Exception();
		}

		return result;
	}

	// 複合条件検索件数
	public Integer countByFormModel(SearchFormModel formModel) {

		// 検索状態bean生成
		// (where句のSQL文と、バインドするパラメータマップを保持するbean)
		SearchConditionBean searchCondition = createSearchConditionBean(formModel);

		// SQL文生成
		String sql = "select count(*) from ONLINE_PRODUCT ";
		StringBuilder sb = new StringBuilder(sql);
		sb.append(searchCondition.getSql());
		sql = sb.toString();

		// SQL実行
		Integer count = this.npJdbcTemplate.queryForObject(sql, searchCondition.getParamMap(), Integer.class);
		return count;
	}

	// 複合条件検索
	public List<Product> findBySearchCondition(SearchFormModel formModel, int start, int limit) {

		// 検索状態bean生成
		// (where句のSQL文と、バインドするパラメータマップを保持するbean)
		SearchConditionBean searchCondition = createSearchConditionBean(formModel);

		// SQL文生成
		String sql = "select * from ONLINE_PRODUCT ";
		StringBuilder sb = new StringBuilder(sql);
		sb.append(searchCondition.getSql());

		// limit句を追加
		sb.append("order by CATEGORY_ID ");
		sb.append("limit :start, :limit ");
		sql = sb.toString();

		// バインドするパラメータマップ
		MapSqlParameterSource paramMap = searchCondition.getParamMap();
		// limit句にバインドするパラメータを追加
		paramMap.addValue("start", start);
		paramMap.addValue("limit", limit);

		// SQL実行
		List<Product> memberList = this.npJdbcTemplate.query(sql, paramMap, new ProductRowMapper());
		return memberList;
	}

	/**
	 * フォームモデル(検索条件)から
	 * 検索状態(where句のSQL文と バインドするパラメータマップ)を生成するメソッド
	 * @param formModel
	 * @return
	 */
	private SearchConditionBean createSearchConditionBean(SearchFormModel searchFormModel) {

		StringBuilder sbWhere = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		if (!searchFormModel.getCategory().isEmpty() && Integer.parseInt(searchFormModel.getCategory()) != 0) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			}
			sbWhere.append("CATEGORY_ID = :category ");
			paramMap.addValue("category", searchFormModel.getCategory());
		}

		if (!searchFormModel.getProductName().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("PRODUCT_NAME LIKE :word ");
			paramMap.addValue("word", "%" + searchFormModel.getProductName() + "%");
		}

		if (!searchFormModel.getMaker().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("MAKER LIKE :maker ");
			paramMap.addValue("maker", "%" + searchFormModel.getMaker() + "%");
		}

		if (!searchFormModel.getPriceMin().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append(":goodsLowerPrice <= UNIT_PRICE ");
			paramMap.addValue("goodsLowerPrice", Integer.parseInt(searchFormModel.getPriceMin()));
		}

		if (!searchFormModel.getPriceMax().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("UNIT_PRICE <= :goodsUpperPrice ");
			paramMap.addValue("goodsUpperPrice", Integer.parseInt(searchFormModel.getPriceMax()));
		}

		if (sbWhere.length() == 0) {
			sbWhere.append("WHERE ");
		} else {
			sbWhere.append("AND ");
		}
		sbWhere.append("DELETE_FLG = 0 ");

		return new SearchConditionBean(sbWhere.toString(), paramMap);
	}
}
