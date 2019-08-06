package jp.co.pscsrv.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.pscsrv.bean.Fee;
import jp.co.pscsrv.bean.PagingBean;
import jp.co.pscsrv.bean.ProductBean;
import jp.co.pscsrv.dao.CategoryDao;
import jp.co.pscsrv.dao.MemberDao;
import jp.co.pscsrv.dao.OrderDao;
import jp.co.pscsrv.dao.OrderDetailDao;
import jp.co.pscsrv.dao.ProductDao;
import jp.co.pscsrv.dto.Category;
import jp.co.pscsrv.dto.Member;
import jp.co.pscsrv.dto.Order;
import jp.co.pscsrv.dto.OrderDetail;
import jp.co.pscsrv.dto.Product;
import jp.co.pscsrv.model.AlterMemberFormModel;
import jp.co.pscsrv.model.MemberFormModel;
import jp.co.pscsrv.model.SearchFormModel;

@Service
public class ShoppingService {

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private OrderDetailDao orderDetailDao;

	/**
	 * ログインを行う
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public Member loginUser(String no, String pass) throws Exception {
		Member user = null;
		try {
			user = memberDao.findUser(Integer.parseInt(no), pass);
			user.setRegistDate(replaceDate(user.getRegistDate()));
		} catch (Exception e) {
			throw e;
		}

		return user;
	}

	/**
	 * 会員を登録する
	 * @param form
	 * @return
	 */
	public Member insertUser(MemberFormModel form) throws Exception {
		Member member = setMemberInfo(form);
		createMemberNo(member);
		//登録に失敗したとき
		if (memberDao.insertOne(member) != 1) {
			throw new Exception();
		}
		return member;
	}

	/**
	 * 会員Noから1件検索
	 * @param form
	 * @return
	 */
	public Member findMemberOne(Member logUser) {
		Member detailMember = memberDao.findOne(logUser.getNo());
		detailMember.setRegistDate(replaceDate(detailMember.getRegistDate()));
		return detailMember;
	}

	/**
	 * 会員情報の修正
	 */

	public int updateMember(AlterMemberFormModel form) {
		Member member = refillMember(form);
		int result = memberDao.update(member);
		return result;
	}

	/**
	 * 会員情報の削除
	 */
	public int deleteMember(Member member) {
		int result = memberDao.delete(member);
		return result;
	}

	/**
	 * カテゴリーの全件検索
	 */
	public List<Category> findCategoryAll() {
		List<Category> listCategory = categoryDao.findAll();

		return listCategory;
	}

	/**
	 * 商品検索(複合条件検索)
	 */
	public List<Product> searchProducts(SearchFormModel form) throws Exception {
		List<Product> listProducts = productDao.search(form);
		if (listProducts.size() == 0) {
			throw new Exception();
		}
		return listProducts;
	}

	/**
	 * 商品検索(1件検索)
	 */
	public Product findProductOne(String code) {
		Product product = productDao.findOne(code);
		return product;
	}

	/**
	 * 商品検索(複数件)
	 */
	public List<String> findProducts(List<ProductBean> listProductBean){
		List<String> noProductCode = new ArrayList<String>();
		Product findProduct = null;

		for(ProductBean bean: listProductBean) {
			findProduct = productDao.findOneNoFlag(bean.getProductCode());
			//商品が見つからなかった場合
			if(findProduct == null) {
				String noCode = bean.getProductCode();
				noProductCode.add(noCode);
			}
		}
		return noProductCode;
	}

	/**
	 * 選択した商品の検索（配列）
	 */
	public List<Product> findSelectedProducts(String[] arraySelected) {
		List<Product> listProduct = new ArrayList<Product>();
		for (String code : arraySelected) {
			listProduct.add(productDao.findOne(code));
		}
		return listProduct;
	}

	/**
	 * 取り扱っていない商品の検索（List）
	 */
	public List<Product> findSelectedDeleteProducts(List<ProductBean> buyProduct) {
		List<Product> noProduct = new ArrayList<Product>();

		for (int count = 0; count < buyProduct.size(); count++) {
			Product product = productDao.deleteFindOne(buyProduct.get(count).getProductCode());
			if (product != null) {
				noProduct.add(product);
			}
		}
		return noProduct;
	}

	/**
	 * 選択した商品の検索（Map）
	 */
	public List<Product> findSelectedProductsMap(LinkedHashMap<String, String> registerProducts) {
		List<Product> listProduct = new ArrayList<Product>();
		for (String code : registerProducts.keySet()) {
			listProduct.add(productDao.findOne(code));
		}
		return listProduct;
	}

	/**
	 * 注文確定処理
	 */
	@Transactional(rollbackForClassName = "Exception")
	public int order(HttpSession session) throws Exception{
		int result = 0;
		Member user = (Member) session.getAttribute("loginUser");
		Fee fee = (Fee) session.getAttribute("fee");
		List<ProductBean> listProductBean = (List<ProductBean>) session.getAttribute("productBean");

		try {
			Order order = setOrderInfo(user, fee);
			//注文台帳テーブルに追加
			if (orderDao.insert(order) == 1) {
				for (ProductBean pb : listProductBean) {
					OrderDetail od = setOrderDetailInfo(order, pb);
					//注文詳細台帳テーブル
					orderDetailDao.insert(od);
				}
				for (ProductBean pb : listProductBean) {
					Product product = productDao.findOne(pb.getProductCode());
					int newStock = product.getStockCount() - Integer.parseInt(pb.getBuyCount());
					//商品テーブルの在庫数を更新
					result = productDao.update(pb.getProductCode(), newStock);
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return result;
	}

	/**
	 * お買い物かご登録時に在庫数の比較
	 */
	public boolean isProductStockSession(LinkedHashMap<String, String> registerProduct, HttpSession session) {

		//セッションに買い物かご情報が存在するか
		if (session.getAttribute("registerProductsMap") == null) {
			//セッションに買い物かご情報が存在しない
			for (String code : registerProduct.keySet()) {
				Product product = productDao.findOne(code);
				//在庫数が足りない場合
				if (Integer.parseInt(registerProduct.get(code)) > product.getStockCount()) {
					return false;
				}
			}
		} else {
			//セッションに買い物かご情報が存在する
			//セッションのお買いもの情報取得
			LinkedHashMap<String, String> sessionProductsMap = (LinkedHashMap<String, String>) session
					.getAttribute("registerProductsMap");
			for (String code : registerProduct.keySet()) {
				Product product = productDao.findOne(code);
				//セッションに商品が入っているとき
				if (sessionProductsMap.get(code) != null) {
					int value = Integer.parseInt(sessionProductsMap.get(code));
					//在庫数が足りない場合
					if (Integer.parseInt(registerProduct.get(code)) + value > product.getStockCount()) {
						return false;
					}
				} else {
					//セッションに商品が入っていないとき
					if (Integer.parseInt(registerProduct.get(code)) > product.getStockCount()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 買い物かごと在庫数の比較
	 */
	public boolean isProductStock(HttpSession session) {

		//セッションに買い物かご情報が存在する
		//セッションのお買いもの情報取得
		LinkedHashMap<String, String> sessionProductsMap = (LinkedHashMap<String, String>) session
				.getAttribute("registerProductsMap");
		for (String code : sessionProductsMap.keySet()) {
			Product product = productDao.findOne(code);
			if (Integer.parseInt(sessionProductsMap.get(code)) > product.getStockCount()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * セッションのお買い物情報の更新
	 */
	public boolean updateSessionBag(LinkedHashMap<String, String> registerProduct, HttpSession session) {

		//セッションに買い物かご情報が存在するか
		if (session.getAttribute("registerProductsMap") == null) {
			//セッションに買い物かご情報が存在しない
			session.setAttribute("registerProductsMap", registerProduct);
		} else {
			//セッションに買い物かご情報が存在する
			LinkedHashMap<String, String> sessionProductsMap = (LinkedHashMap<String, String>) session
					.getAttribute("registerProductsMap");
			for (String code : registerProduct.keySet()) {
				//セッションに商品が入っているか
				if (sessionProductsMap.get(code) == null) {
					//セッションにない商品
					sessionProductsMap.put(code, registerProduct.get(code));
				} else {
					//セッションに商品にある商品
					int total = Integer.parseInt(sessionProductsMap.get(code))
							+ Integer.parseInt(registerProduct.get(code));
					sessionProductsMap.put(code, String.valueOf(total));
				}
			}
		}
		return true;
	}

	/**
	 * フォームに入力された値をMemberにセット
	 * @param form
	 * @return
	 */
	private Member setMemberInfo(MemberFormModel form) {
		Member member = new Member();

		//Calendarクラスのオブジェクトを生成する
		Calendar cl = Calendar.getInstance();
		//SimpleDateFormatクラスでフォーマットパターンを設定する
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		member.setPass(form.getPass());
		member.setName(form.getName());
		member.setAge(Integer.parseInt(form.getAge()));
		member.setGender(form.getGender());
		member.setPostal(form.getPostal());
		member.setAddress(form.getAddress());
		member.setPhone(form.getPhone());
		member.setRegistDate(sdf.format(cl.getTime()));
		member.setLastUpDay(new Timestamp(System.currentTimeMillis()));
		return member;
	}

	/**
	 * ログインユーザの情報をフォームに詰め替える
	 */
	public AlterMemberFormModel refillInfo(Member member) {
		AlterMemberFormModel form = new AlterMemberFormModel();
		String registDate = member.getRegistDate();
		form.setNo(String.valueOf(member.getNo()));
		form.setName(member.getName());
		form.setPrevPass(member.getPass());
		form.setAge(String.valueOf(member.getAge()));
		form.setGender(member.getGender());
		form.setPostal(member.getPostal());
		form.setAddress(member.getAddress());
		form.setPhone(member.getPhone());
		form.setRegistDate(member.getRegistDate());

		return form;
	}

	/**
	 * 修正フォームの情報をMemberに詰め替える
	 */
	public Member refillMember(AlterMemberFormModel form) {
		Member member = new Member();
		member.setNo(Integer.parseInt(form.getNo()));
		member.setPass(form.getPass());
		member.setName(form.getName());
		member.setAge(Integer.parseInt(form.getAge()));
		member.setGender(form.getGender());
		member.setPostal(form.getPostal());
		member.setAddress(form.getAddress());
		member.setPhone(form.getPhone());

		return member;
	}

	/**
	 * 会員番号の発行
	 */
	private void createMemberNo(Member member) {
		//会員が登録されていないとき
		if (memberDao.findMaxNumber() == null) {
			member.setNo(1);
		} else {
			member.setNo(memberDao.findMaxNumber() + 1);
		}
	}

	/**
	 * 商品情報をBeanに詰め替え(sessionに入っているMapから)
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public List<ProductBean> setSessionProductBean(HttpSession session) throws Exception {
		List<ProductBean> listPb = new ArrayList<ProductBean>();
		List<String> keyList = new ArrayList<String>();
		LinkedHashMap<String, String> registerProducts = (LinkedHashMap<String, String>) session
				.getAttribute("registerProductsMap");

		for (String code : registerProducts.keySet()) {
			keyList.add(code);
		}
		//プロダクトコード順にソート
		Collections.sort(keyList);

		for (String code : keyList) {
			Product product = productDao.findOne(code);

			if(product == null) {
				throw new Exception();
			}

			ProductBean pb = new ProductBean();

			pb.setProductCode(product.getProductCode());
			pb.setCategoryId(product.getCategoryId());
			pb.setProductName(product.getProductName());
			pb.setMaker(product.getMaker());
			pb.setStockCount(product.getStockCount());
			pb.setRegisterDate(product.getRegisterDate());
			pb.setPrice(product.getPrice());
			pb.setPictureName(product.getPictureName());
			pb.setMemo(product.getMemo());
			pb.setDeleteFlag(product.getDeleteFlag());
			pb.setBuyCount(registerProducts.get(code));

			listPb.add(pb);
		}
		return listPb;
	}

	/**
	 * 商品情報をBeanに詰め替え(Mapに入っている情報)
	 * @param session
	 * @return
	 */
	public List<ProductBean> setMapProductBean(LinkedHashMap<String, String> registerProducts) {
		List<ProductBean> listPb = new ArrayList<ProductBean>();
		for (String code : registerProducts.keySet()) {
			Product product = productDao.findOne(code);
			ProductBean pb = new ProductBean();

			pb.setProductCode(product.getProductCode());
			pb.setCategoryId(product.getCategoryId());
			pb.setProductName(product.getProductName());
			pb.setMaker(product.getMaker());
			pb.setStockCount(product.getStockCount());
			pb.setRegisterDate(product.getRegisterDate());
			pb.setPrice(product.getPrice());
			pb.setPictureName(product.getPictureName());
			pb.setMemo(product.getMemo());
			pb.setDeleteFlag(product.getDeleteFlag());
			pb.setBuyCount(registerProducts.get(code));

			listPb.add(pb);
		}
		return listPb;
	}

	/**
	 * 料金を返す
	 */
	public Fee calcFeeInfo(List<ProductBean> productList) {
		Fee fee = new Fee();
		fee.setSubTotal(calcSubTotal(fee, productList));
		fee.setTax((int) (fee.getSubTotal() * 0.08));
		fee.setTotal(fee.getSubTotal() + fee.getTax());
		return fee;
	}

	/**
	 * 料金の計算
	 */
	private int calcSubTotal(Fee fee, List<ProductBean> productList) {
		int money = 0;
		for (ProductBean product : productList) {
			money += product.getPrice() * Integer.parseInt(product.getBuyCount());
		}
		return money;
	}

	/**
	 * 注文台帳の情報セット
	 */
	private Order setOrderInfo(Member user, Fee fee) {
		Order order = new Order();
		//Calendarクラスのオブジェクトを生成する
		Calendar cl = Calendar.getInstance();
		//SimpleDateFormatクラスでフォーマットパターンを設定する(注文日)
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		//SimpleDateFormatクラスでフォーマットパターンを設定する(とりまとめ番号)
		SimpleDateFormat orderSdf = new SimpleDateFormat("YYMMddHHmmss");
		order.setMemberNo(user.getNo());
		order.setTotal(fee.getTotal());
		order.setTax(fee.getTax());
		order.setOrderDate(sdf.format(cl.getTime()));
		order.setCollectNo(String.format("%04d", user.getNo()) + orderSdf.format(cl.getTime()));
		return order;
	}

	/**
	 * 注文台帳詳細の情報セット
	 */
	private OrderDetail setOrderDetailInfo(Order order, ProductBean productBean) {
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setCollectNo(order.getCollectNo());
		orderDetail.setProductCode(productBean.getProductCode());
		orderDetail.setOrderCount(Integer.parseInt(productBean.getBuyCount()));
		orderDetail.setPrice(productBean.getPrice());
		return orderDetail;
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
	 * @throws Exception
	 */
	public List<Product> searchProductPage(SearchFormModel formModel, PagingBean pagingBean) throws Exception {

		// フォームモデル(検索条件)から検索件数を取得(limit無し)
		Integer searchCount = productDao.countByFormModel(formModel);
		if(searchCount == 0) {
			throw new Exception();
		}

		// 検索件数をページングビーンに格納
		// このsetterで現在ページや最大ページを算出
		pagingBean.setSearchCount(searchCount);

		// limit 含めた検索実行
		List<Product> productList = productDao.findBySearchCondition(formModel, pagingBean.getStart(), PagingBean.MAX_DISP);

		return productList;
	}
}
