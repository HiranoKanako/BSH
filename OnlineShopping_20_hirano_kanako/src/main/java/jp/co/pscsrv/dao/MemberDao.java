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

import jp.co.pscsrv.dto.Member;

@Repository
public class MemberDao {
	// NamedParameterJDBC Templateを注入
	@Autowired
	private NamedParameterJdbcTemplate namedPramTemplate;

	// JDBC Templateを注入
	@Autowired
	private JdbcTemplate jdbcTemplate;

	//会員情報マッピング(内部クラス)
	private class OnlineMemberRowMapper extends BeanPropertyRowMapper<Member> {

		// mapRowメソッドにマッピングを書く
		@Override
		public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
			Member mem = new Member();
			mem.setNo(rs.getInt("MEMBER_NO"));
			mem.setPass(rs.getString("PASSWORD"));
			mem.setName(rs.getString("NAME"));
			mem.setAge(rs.getInt("AGE"));
			mem.setGender(rs.getString("SEX"));
			mem.setPostal(rs.getString("ZIP"));
			mem.setAddress(rs.getString("ADDRESS"));
			mem.setPhone(rs.getString("TEL"));
			mem.setRegistDate(rs.getString("REGISTER_DATE"));
			mem.setDelteFlag(rs.getString("DELETE_FLG"));
			return mem;
		}
	}

	public int insertOne(Member member) {
		// SQLクエリ、バインド変数
		String sql = "INSERT INTO ONLINE_MEMBER" +
				"(MEMBER_NO,PASSWORD,NAME,AGE,SEX,ZIP,ADDRESS,TEL,REGISTER_DATE)" +
				"VALUES (:no,:pass,:name,:age,:gender,:postal,:address,:phone,:registDate);";

		// パラメータをバインド
		MapSqlParameterSource paramMap = new MapSqlParameterSource()
				.addValue("no", member.getNo())
				.addValue("pass", member.getPass())
				.addValue("name", member.getName())
				.addValue("age", member.getAge())
				.addValue("gender", member.getGender())
				.addValue("postal", member.getPostal())
				.addValue("address", member.getAddress())
				.addValue("phone", member.getPhone())
				.addValue("registDate", member.getRegistDate());

		//クエリ実行
		return this.namedPramTemplate.update(sql, paramMap);
	}

	/**
	 * 最新の会員No検索
	 * @param String number
	 * @return List<Member>
	 */
	public Integer findMaxNumber() {
		// SQLクエリ、バインド変数
		String sql = " select MAX(MEMBER_NO) AS 'NO' FROM ONLINE_MEMBER;";

		// クエリ実行
		Integer max = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return max;
	}

	/**
	 * 会員Noによる検索(1件)
	 * @param String number
	 * @return Member
	 */
	public Member findOne(int no) {
		// SQLクエリ、バインド変数
		String sql = " SELECT * FROM ONLINE_MEMBER WHERE MEMBER_NO = :no AND DELETE_FLG = '0';";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("no", no);

		// クエリ実行
		List<Member> memberList = this.namedPramTemplate.query(sql, param, new OnlineMemberRowMapper());
		return memberList.get(0);
	}

	/**
	 * 会員Noとパスワードよる検索(1件)
	 * @param String number
	 * @return Member
	 */
	public Member findUser(int no, String pass) throws Exception {
		// SQLクエリ、バインド変数
		String sql = " SELECT * FROM ONLINE_MEMBER WHERE MEMBER_NO = :no AND PASSWORD = :pass AND DELETE_FLG = '0';";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("no", no)
				.addValue("pass", pass);

		// クエリ実行
		List<Member> memberList = this.namedPramTemplate.query(sql, param, new OnlineMemberRowMapper());

		//検索に失敗した場合
		if (memberList.size() != 1) {
			throw new Exception();
		}

		return memberList.get(0);
	}

	/**
	 * 会員情報の変更
	 */
	public int update(Member member) {
		// SQLクエリ、バインド変数
		String sql = "UPDATE ONLINE_MEMBER SET PASSWORD =:pass, NAME=:name,AGE = :age,SEX= :gender,ZIP =:postal,ADDRESS= :address,TEL = :phone WHERE MEMBER_NO= :no";

		// パラメータをバインド
		MapSqlParameterSource paramMap = new MapSqlParameterSource()
				.addValue("pass", member.getPass())
				.addValue("name", member.getName())
				.addValue("age", member.getAge())
				.addValue("gender", member.getGender())
				.addValue("postal", member.getPostal())
				.addValue("address", member.getAddress())
				.addValue("phone", member.getPhone())
				.addValue("no", member.getNo());

		//クエリ実行
		return this.namedPramTemplate.update(sql, paramMap);
	}

	/**
	 * 会員情報の削除(論理削除)
	 */
	public int delete(Member member) {
		// SQLクエリ、バインド変数
		String sql = "UPDATE ONLINE_MEMBER SET DELETE_FLG = '1' WHERE MEMBER_NO= :no";

		// パラメータをバインド
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("no", member.getNo());

		//クエリ実行
		return this.namedPramTemplate.update(sql, param);
	}
}
