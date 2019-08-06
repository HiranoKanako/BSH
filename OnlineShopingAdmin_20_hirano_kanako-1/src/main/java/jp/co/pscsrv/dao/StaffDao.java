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

import jp.co.pscsrv.dto.Staff;

@Repository
public class StaffDao {
	// NamedParameterJDBC Templateを注入
		@Autowired
		private NamedParameterJdbcTemplate namedPramTemplate;

		// JDBC Templateを注入
		@Autowired
		private JdbcTemplate jdbcTemplate;

		//会員情報マッピング(内部クラス)
		private class OnlineStaffRowMapper extends BeanPropertyRowMapper<Staff> {

			// mapRowメソッドにマッピングを書く
			@Override
			public Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
				Staff staff = new Staff();
				staff.setNo(rs.getInt("STAFF_NO"));
				staff.setPass(rs.getString("PASSWORD"));
				staff.setName(rs.getString("NAME"));
				staff.setAge(rs.getInt("AGE"));
				staff.setGender(rs.getString("SEX"));
				staff.setRegistDate(rs.getString("REGISTER_DATE"));
				return staff;
			}
		}

		/**
		 * 管理者番号とパスワードよる検索(1件)
		 * @param String number
		 * @return Staff
		 */
		public Staff findUser(int no, String pass) throws Exception {
			// SQLクエリ、バインド変数
			String sql = " SELECT * FROM ONLINE_STAFF WHERE STAFF_NO = :no AND PASSWORD = :pass;";

			MapSqlParameterSource param = new MapSqlParameterSource().addValue("no", no)
					.addValue("pass", pass);

			// クエリ実行
			List<Staff> staffList = this.namedPramTemplate.query(sql, param, new OnlineStaffRowMapper());

			//検索に失敗した場合
			if (staffList.size() != 1) {
				throw new Exception();
			}

			return staffList.get(0);
		}
}
