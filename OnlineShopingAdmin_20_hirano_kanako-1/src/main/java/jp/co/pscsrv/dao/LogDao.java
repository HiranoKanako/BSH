package jp.co.pscsrv.dao;

import java.sql.Date;
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
import jp.co.pscsrv.dto.Log;
import jp.co.pscsrv.model.SearchFormModel;

@Repository
public class LogDao {

	// JDBC Templateを注入
	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	//履歴検索情報マッピング(内部クラス)
	private class LogRowMapper extends BeanPropertyRowMapper<Log> {

		// mapRowメソッドにマッピングを書く
		@Override
		public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
			Log log = new Log();
			log.setOrderNo(rs.getInt("oo.ORDER_NO"));
			log.setMemberNo(rs.getInt("om.MEMBER_NO"));
			log.setMemberName(rs.getString("om.NAME"));
			log.setMoney(rs.getInt("oo.TOTAL_MONEY"));
			log.setTax(rs.getInt("oo.TOTAL_TAX"));
			log.setOrderDate(rs.getString("oo.ORDER_DATE"));
			log.setCollectNo(rs.getString("oo.COLLECT_NO"));
			return log;
		}
	}

	//履歴詳細情報マッピング(内部クラス)
	private class LogDetailRowMapper extends BeanPropertyRowMapper<Log> {

		// mapRowメソッドにマッピングを書く
		@Override
		public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
			Log log = new Log();
			log.setMemberNo(rs.getInt("om.MEMBER_NO"));
			log.setMemberName(rs.getString("om.NAME"));
			log.setTel(rs.getString("om.TEL"));
			log.setCollectNo(rs.getString("oo.COLLECT_NO"));
			log.setOrderDate(rs.getString("oo.ORDER_DATE"));
			log.setMoney(rs.getInt("oo.TOTAL_MONEY"));
			log.setTax(rs.getInt("oo.TOTAL_TAX"));
			return log;
		}
	}

	// 複合条件検索件数
	public Integer countByFormModel(SearchFormModel formModel, Date startDate, Date endDate) {

		// 検索状態bean生成
		// (where句のSQL文と、バインドするパラメータマップを保持するbean)
		SearchConditionBean searchCondition = createSearchConditionBean(formModel, startDate, endDate);

		// SQL文生成
		String sql = "select count(*) from ONLINE_ORDER oo INNER JOIN ONLINE_MEMBER om ON oo.MEMBER_NO = om.MEMBER_NO ";
		StringBuilder sb = new StringBuilder(sql);
		sb.append(searchCondition.getSql());
		sql = sb.toString();

		// SQL実行
		Integer count = this.npJdbcTemplate.queryForObject(sql, searchCondition.getParamMap(), Integer.class);
		return count;
	}

	// 複合条件検索
	public List<Log> findBySearchCondition(SearchFormModel formModel, int start, int limit, Date startDate,
			Date endDate) {

		// 検索状態bean生成
		// (where句のSQL文と、バインドするパラメータマップを保持するbean)
		SearchConditionBean searchCondition = createSearchConditionBean(formModel, startDate, endDate);
		// SQL文生成
		String sql = "select * from ONLINE_ORDER oo INNER JOIN ONLINE_MEMBER om ON oo.MEMBER_NO = om.MEMBER_NO ";
		StringBuilder sb = new StringBuilder(sql);
		sb.append(searchCondition.getSql());

		// limit句を追加
		sb.append("order by ORDER_DATE DESC ");
		sb.append("limit :start, :limit ");
		sql = sb.toString();

		// バインドするパラメータマップ
		MapSqlParameterSource paramMap = searchCondition.getParamMap();
		// limit句にバインドするパラメータを追加
		paramMap.addValue("start", start);
		paramMap.addValue("limit", limit);

		// SQL実行
		List<Log> memberList = this.npJdbcTemplate.query(sql, paramMap, new LogRowMapper());

		return memberList;
	}

	/**
	 * フォームモデル(検索条件)から
	 * 検索状態(where句のSQL文と バインドするパラメータマップ)を生成するメソッド
	 * @param formModel
	 * @return
	 */
	private SearchConditionBean createSearchConditionBean(SearchFormModel searchFormModel, Date startDate,
			Date endDate) {

		StringBuilder sbWhere = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		if (!searchFormModel.getMemberNo().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("om.MEMBER_NO = :no ");
			paramMap.addValue("no", Integer.valueOf(searchFormModel.getMemberNo()));
		}

		if (searchFormModel.getMemberName() != null && !searchFormModel.getMemberName().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("om.NAME like :name ");
			paramMap.addValue("name", "%" + searchFormModel.getMemberName() + "%");
		}

		if (startDate != null) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append(":startDate <= oo.ORDER_DATE ");
			paramMap.addValue("startDate", startDate);
		}

		if (endDate != null) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("oo.ORDER_DATE <= :endDate ");
			paramMap.addValue("endDate", endDate);
		}

		if (!searchFormModel.getPriceMin().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append(":goodsLowerPrice <= (oo.TOTAL_MONEY + oo.TOTAL_TAX) ");
			paramMap.addValue("goodsLowerPrice", Integer.valueOf(searchFormModel.getPriceMin()));
		}

		if (!searchFormModel.getPriceMax().isEmpty()) {
			if (sbWhere.length() == 0) {
				sbWhere.append("WHERE ");
			} else {
				sbWhere.append("AND ");
			}
			sbWhere.append("(oo.TOTAL_MONEY + oo.TOTAL_TAX) <= :goodsUpperPrice ");
			paramMap.addValue("goodsUpperPrice", Integer.valueOf(searchFormModel.getPriceMax()));
		}

		if (sbWhere.length() == 0) {
			sbWhere.append("WHERE ");
		} else {
			sbWhere.append("AND ");
		}
		sbWhere.append("om.DELETE_FLG = 0 ");

		return new SearchConditionBean(sbWhere.toString(), paramMap);
	}

	/**
	 * 商品詳細検索
	 */
	public Log searchDetail(String no) throws Exception {

		// SQLクエリ、バインド変数
		String sql = "select * from online_order oo "
				+ "INNER JOIN online_member om   ON om.MEMBER_NO = oo.MEMBER_NO "
				+ "WHERE oo.ORDER_NO = :no;";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("no", no);

		// クエリ実行
		List<Log> logList = this.npJdbcTemplate.query(sql, param, new LogDetailRowMapper());
		//検索に失敗した場合
		if (logList.size() != 1) {
			throw new Exception();
		}

		return logList.get(0);
	}

}
