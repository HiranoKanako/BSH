package jp.co.pscsrv.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import jp.co.pscsrv.dto.Category;

@Repository
public class CategoryDao {
	// JDBC Templateを注入
	@Autowired
	private NamedParameterJdbcTemplate template;

	//カテゴリー情報マッピング(内部クラス)
	private class CategoryRowMapper extends BeanPropertyRowMapper<Category> {

		// mapRowメソッドにマッピングを書く
		@Override
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			Category ctgr = new Category();
			ctgr.setCategoryId(rs.getInt("CTGR_ID"));
			ctgr.setCategoryName(rs.getString("NAME"));
			return ctgr;
		}
	}

	/**
	 * カテゴリーNoによる検索(1件)
	 * @param String number
	 * @return List<Category>
	 */
	public List<Category> findOne(int id) {
		// SQLクエリ、バインド変数
		String sql = " SELECT * FROM ONLINE_CATEGORY WHERE CTGR_ID = :id;";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

		// クエリ実行
		List<Category> categoryList = this.template.query(sql, param, new CategoryRowMapper());
		return categoryList;
	}

	/**
	 * 全件検索
	 * @param String number
	 * @return List<Category>
	 */
	public List<Category> findAll() {
		// SQLクエリ、バインド変数
		String sql = " SELECT * FROM ONLINE_CATEGORY";

		// クエリ実行
		List<Category> categoryList = this.template.query(sql, new CategoryRowMapper());

		return categoryList;
	}
}
