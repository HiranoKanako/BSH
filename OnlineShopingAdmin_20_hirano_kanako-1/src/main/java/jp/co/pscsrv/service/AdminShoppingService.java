package jp.co.pscsrv.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.pscsrv.bean.PagingBean;
import jp.co.pscsrv.dao.LogDao;
import jp.co.pscsrv.dao.OrderDao;
import jp.co.pscsrv.dao.OrderListDao;
import jp.co.pscsrv.dao.OrderProductDao;
import jp.co.pscsrv.dao.StaffDao;
import jp.co.pscsrv.dto.Log;
import jp.co.pscsrv.dto.OrderProduct;
import jp.co.pscsrv.dto.Staff;
import jp.co.pscsrv.model.SearchFormModel;

@Service
public class AdminShoppingService {

	@Autowired
	private StaffDao staffDao;

	@Autowired
	private LogDao logDao;

	@Autowired
	private OrderProductDao orderProductDao;

	@Autowired
	private OrderListDao orderListDao;

	@Autowired
	private OrderDao orderDao;

	/**
	 * ログインを行う
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public Staff loginUser(String no, String pass) throws Exception {
		Staff user = null;
		try {
			user = staffDao.findUser(Integer.parseInt(no), pass);
			user.setRegistDate(replaceDate(user.getRegistDate()));
		} catch (Exception e) {
			throw e;
		}

		return user;
	}

	/**
	 * 日付の表示形式変更("-"からへ"/")
	 */
	private String replaceDate(String date) {
		String newDate = date.replace("-", "/");
		return newDate;
	}

	/**
	 * 検索条件から検索
	 * @param formModel
	 * @param pagingBean
	 * @return List<Product>
	 */
	public List<Log> searchLogPage(SearchFormModel formModel, PagingBean pagingBean) throws Exception {

		Date sqlStartDate = null;
		Date sqlEndDate = null;

		if (!formModel.getStartYear().equals("") && !formModel.getStartMonth().equals("") && !formModel.getStartDay().equals("")) {
			sqlStartDate = changeDate(formModel.getStartYear(), formModel.getStartMonth(), formModel.getStartDay());
		}

		if (!formModel.getEndYear().equals("") && !formModel.getEndMonth().equals("") && !formModel.getEndDay().equals("")) {
			sqlEndDate = changeDate(formModel.getEndYear(), formModel.getEndMonth(), formModel.getEndDay());
		}

		// フォームモデル(検索条件)から検索件数を取得(limit無し)
		Integer searchCount = logDao.countByFormModel(formModel, sqlStartDate, sqlEndDate);

		if (searchCount == 0) {
			throw new Exception();
		}

		// 検索件数をページングビーンに格納
		// このsetterで現在ページや最大ページを算出
		pagingBean.setSearchCount(searchCount);

		// limit 含めた検索実行
		List<Log> productList = logDao.findBySearchCondition(formModel, pagingBean.getStart(), PagingBean.MAX_DISP,
				sqlStartDate, sqlEndDate);

		for (int index = 0; index < productList.size(); index++) {
			productList.get(index).setOrderDate(replaceDate(productList.get(index).getOrderDate()));
		}

		return productList;
	}

	private Date changeDate(String year, String month, String day) {
		String date = year + "-" + month + "-" + day;
		Date sqldate = Date.valueOf(date);
		return sqldate;
	}

	/**
	 * 履歴詳細の情報取得
	 */
	public Log searchLogDetail(String id) throws Exception {
		Log log = null;
		try {
			log = logDao.searchDetail(id);
			log.setOrderDate(replaceDate(log.getOrderDate()));
		} catch (Exception e) {
			throw e;
		}
		return log;
	}

	/**
	 * 購入商品の情報取得
	 */
	public List<OrderProduct> searchOrderProducts(String collectNo) {
		List<OrderProduct> orderProduct = orderProductDao.searchDetail(collectNo);
		return orderProduct;
	}

	/**
	 * 購入履歴の削除
	 */
	@Transactional(rollbackForClassName = "Exception")
	public int logDelete(String no) throws Exception {
		int orderListCount = 0;
		int resultOrderList = 0;
		int resultOrder = 0;
		try {
			orderListCount = orderListDao.countByDeleteProduct(no);
			resultOrderList = orderListDao.deleteOrderList(no);

			//削除件数と削除結果件数が違う場合
			if (orderListCount != resultOrderList) {
				throw new Exception();
			}

			resultOrder = orderDao.deleteOrder(no);

			if (resultOrder != 1) {
				throw new Exception();
			}

		} catch (Exception e) {
			throw e;
		}
		return resultOrder;
	}
}
