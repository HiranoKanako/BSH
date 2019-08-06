package jp.co.pscsrv.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.pscsrv.bean.Fee;
import jp.co.pscsrv.bean.PagingBean;
import jp.co.pscsrv.bean.ProductBean;
import jp.co.pscsrv.constants.SessionConstants;
import jp.co.pscsrv.dto.Category;
import jp.co.pscsrv.dto.Member;
import jp.co.pscsrv.dto.Product;
import jp.co.pscsrv.model.AlterMemberFormModel;
import jp.co.pscsrv.model.DetailFormModel;
import jp.co.pscsrv.model.LoginFormModel;
import jp.co.pscsrv.model.MemberFormModel;
import jp.co.pscsrv.model.SearchFormModel;
import jp.co.pscsrv.model.SelectProductFormModel;
import jp.co.pscsrv.service.ShoppingService;

@Controller
public class ShoppingController {

	@Autowired
	private ShoppingService shoppingService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * index.html表示
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showIndex(Model model, HttpSession session) {
		return "/menu/MEN101.html";
	}

	/**
	 * ログイン画面表示
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/LOG101.html", method = RequestMethod.GET)
	public String showLogin(Model model) {
		model.addAttribute("loginFormModel", new LoginFormModel());
		return "/log/LOG101";
	}

	/**
	 * メニュー画面表示(GET)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEN101.html", method = RequestMethod.GET)
	public String showMenu() {
		return "/menu/MEN101";
	}

	/**
	 *かごの中身を全削除しメニュー画面表示
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEN101.html", params = "drop", method = RequestMethod.POST)
	public String dropBag(HttpSession session) {
		session.removeAttribute("registerProductsMap");
		session.removeAttribute("registerProducts");
		session.removeAttribute("productBean");
		session.removeAttribute("fee");
		return "/menu/MEN101";
	}

	/**
	 * メニュー画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEN101.html", method = RequestMethod.POST)
	public String backMenu() {
		return "/menu/MEN101";
	}

	/**
	 * ログイン
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM101.html", params = "login", method = RequestMethod.POST)
	public String login(@ModelAttribute @Valid LoginFormModel form, BindingResult errors, Model model,
			HttpSession session, Locale locale) {
		if (errors.hasErrors()) {
			return "/log/LOG101";
		}
		try {
			Member user = shoppingService.loginUser(form.getMemberNo(), form.getPass());
			session.setAttribute("loginUser", user);
			model.addAttribute("form", form);
		} catch (Exception e) {
			String message = messageSource.getMessage("content.MSG012", null, locale);
			model.addAttribute("error", message);
			return "/log/LOG101";
		}

		String nextPage = "/menu/MEN101";
		if (session.getAttribute("prevPage") != null) {
			String prevPage = (String) session.getAttribute("prevPage");
			switch (prevPage) {
			case "MEM201":
				nextPage = "/user/MEM201";
				session.removeAttribute("prevPage");
				break;
			case "KGO102":
				nextPage = "/bag/KGO102";
				session.removeAttribute("prevPage");
				break;
			default:
			}
		}
		return nextPage;
	}

	/**
	 * ログアウト処理
	 */
	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public String logout(Model model, HttpSession session) {
		session.invalidate();
		model.addAttribute("loginFormModel", new LoginFormModel());
		return "/log/LOG101";
	}

	/**
	 * 会員情報入力画面表示(GET)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM101.html", method = RequestMethod.GET)
	public String showRegister(Model model, HttpSession session) throws Exception {
		if (session.getAttribute("memberFormModel") != null) {
			session.removeAttribute("memberFormModel");
		}
		model.addAttribute("memberFormModel", new MemberFormModel());
		return "/register/MEM101";
	}

	/**
	 * 会員情報入力画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM101.html", method = RequestMethod.POST)
	public String backRegister(Model model, HttpSession session) {
		//sessionにオブジェクトが存在するか
		if (session.getAttribute("memberFormModel") == null) {
			model.addAttribute("memberFormModel", new MemberFormModel());
		} else {
			model.addAttribute("memberFormModel", session.getAttribute("memberFormModel"));
		}
		return "/register/MEM101";
	}

	/**
	 * 会員情報入力確認画面表示
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM102.html", method = RequestMethod.POST)
	public String showRegisterConf(@ModelAttribute @Valid MemberFormModel form, BindingResult errors, Model model,
			HttpSession session) {
		if (errors.hasErrors()) {
			return "/register/MEM101";
		}
		session.setAttribute("memberFormModel", form);
		model.addAttribute("memberFormModel", form);
		return "/register/MEM102";
	}

	/**
	 * 会員情報入力結果画面表示
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM103.html", method = RequestMethod.POST)
	public String showRegisterResult(Model model, HttpSession session) {
		try {
			Member insertMember = shoppingService.insertUser((MemberFormModel) session.getAttribute("memberFormModel"));
			model.addAttribute("member", insertMember);
			session.removeAttribute("memberFormModel");
		} catch (Exception e) {
			return "/common/ERR101";
		}
		return "/register/MEM103";
	}

	/**
	 * 会員情報画面表示(GET)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM201.html", method = RequestMethod.GET)
	public String showUserInfo(Model model, HttpSession session) {
		Member member = shoppingService.findMemberOne((Member) session.getAttribute("loginUser"));
		model.addAttribute("memberInfo", member);
		return "/user/MEM201";
	}

	/**
	 * 会員情報画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM201.html", method = RequestMethod.POST)
	public String backUserInfo(Model model, HttpSession session) {
		Member member = shoppingService.findMemberOne((Member) session.getAttribute("loginUser"));
		model.addAttribute("memberInfo", member);
		return "/user/MEM201";
	}

	/**
	 * 会員情報修正画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM202.html", method = RequestMethod.POST)
	public String showUserAlter(Model model, HttpSession session) {
		Member member = shoppingService.findMemberOne((Member) session.getAttribute("loginUser"));
		AlterMemberFormModel amfm = shoppingService.refillInfo(member);
		model.addAttribute("alterMemberFormModel", amfm);
		return "/user/MEM202";
	}

	/**
	 * 会員情報修正確認画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM203.html", method = RequestMethod.POST)
	public String showUserAlterConf(@ModelAttribute @Valid AlterMemberFormModel form, BindingResult errors, Model model,
			HttpSession session, Locale locale) {
		if (errors.hasErrors()) {
			return "/user/MEM202";
		}

		String newPass = form.getPass();
		Member user = (Member) session.getAttribute("loginUser");

		//変更前のパスワードと同じかどうか
		if (newPass.equals(user.getPass())) {
			String message = messageSource.getMessage("content.MSG002", null, locale);
			model.addAttribute("error", message);
			return "/user/MEM202";
		}

		//パスワードに何も入力されなかったとき
		if (newPass.equals("")) {
			//前のパスワードをセット
			form.setPass(user.getPass());
		}

		model.addAttribute("alterMemberFormModel", form);
		session.setAttribute("alterMemberFormModel", form);
		return "/user/MEM203";
	}

	/**
	 * 会員情報修正確認画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEM204.html", method = RequestMethod.POST)
	public String alterUserInfo(Model model, HttpSession session) {

		Member user = (Member) session.getAttribute("loginUser");
		AlterMemberFormModel form = (AlterMemberFormModel) session.getAttribute("alterMemberFormModel");
		form.setNo(String.valueOf(user.getNo()));

		if (shoppingService.updateMember(form) == 1) {
			session.removeAttribute("alterMemberFormModel");
			session.removeAttribute("loginUser");
			try {
				Member updateUser = shoppingService.loginUser(form.getNo(), form.getPass());
				session.setAttribute("loginUser", updateUser);
			} catch (Exception e) {
				return "/common/ERR101";
			}
		}

		return "/user/MEM204";
	}

	/**
	 * 会員情報削除確認画面表示
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/MEM301.html", method = RequestMethod.POST)
	public String deleteUserInfoConf(Model model, HttpSession session) {
		Member member = shoppingService.findMemberOne((Member) session.getAttribute("loginUser"));
		model.addAttribute("memberInfo", member);

		return "/user/MEM301";
	}

	/**
	 * 会員情報削除結果画面
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/MEM302.html", method = RequestMethod.POST)
	public String deleteUserInfo(Model model, HttpSession session) {
		Member member = shoppingService.findMemberOne((Member) session.getAttribute("loginUser"));

		if (shoppingService.deleteMember(member) == 1) {
			session.removeAttribute("loginUser");
		}
		return "/user/MEM302";
	}

	/**
	 * 商品検索画面表示(GET) ページングあり
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/searchForm.html", method = RequestMethod.GET)
	public String showPageProductInfo(Model model, HttpSession session) {
		if (!model.asMap().containsKey(SessionConstants.SEARCH_PARAM)) {
			model.addAttribute(SessionConstants.SEARCH_PARAM, new SearchFormModel());
		}

		// 最初に検索画面を表示するコントローラなのでページングビーンを初期化する。
		PagingBean pagingBean = new PagingBean();

		// 最初に検索画面を表示するコントローラなのでセッション初期化
		session.removeAttribute(SessionConstants.SEARCH_PARAM);
		session.removeAttribute(SessionConstants.SELECT_PARAM);
		session.removeAttribute(SessionConstants.PRODUCT_LIST);
		session.removeAttribute(SessionConstants.CATEGORY_LIST);
		session.removeAttribute(SessionConstants.PAGE);

		//カテゴリリスト取得しsessionに追加
		//プルダウンのカテゴリーを取得
		List<Category> categoryList = shoppingService.findCategoryAll();
		model.addAttribute(SessionConstants.CATEGORY_LIST, categoryList);
		session.setAttribute(SessionConstants.CATEGORY_LIST, categoryList);

		return "/product/SHO101";
	}

	/**
	 * 商品検索画面表示(戻るボタン)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/SHO101.html", method = RequestMethod.POST)
	public String backProductInfo(Model model, HttpSession session) {

		if (session.getAttribute("searchFormModel") != null) {
			PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
			if (pagingBean == null) {
				pagingBean = new PagingBean();
			}
			//遷移前のフォーム、表示された商品リスト、ページをモデルに追加
			model.addAttribute(SessionConstants.SEARCH_PARAM, session.getAttribute(SessionConstants.SEARCH_PARAM));
			model.addAttribute(SessionConstants.SELECT_PARAM, session.getAttribute(SessionConstants.SELECT_PARAM));
			model.addAttribute(SessionConstants.PRODUCT_LIST, session.getAttribute(SessionConstants.PRODUCT_LIST));
			model.addAttribute(SessionConstants.PAGE, pagingBean);
		}

		//カテゴリリストをsessionから取得
		model.addAttribute(SessionConstants.CATEGORY_LIST, session.getAttribute(SessionConstants.CATEGORY_LIST));

		return "/product/SHO101";
	}

	/**
	 * 商品検索画面表示(POST) ページング用
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String psearchProducts(@ModelAttribute SelectProductFormModel spform,
			@ModelAttribute @Valid SearchFormModel form, BindingResult errors,
			Model model, HttpSession session, Locale locale) {

		// POSTで検索条件を受け取る → 検索条件が変わるので、ページングを初期化
		PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
		if (pagingBean == null) {
			pagingBean = new PagingBean();
		}

		if (errors.hasErrors()) {
			model.addAttribute(SessionConstants.CATEGORY_LIST, session.getAttribute(SessionConstants.CATEGORY_LIST));
			//model.addAttribute(SessionConstants.PRODUCT_LIST, session.getAttribute(SessionConstants.PRODUCT_LIST));
			model.addAttribute(SessionConstants.PAGE, pagingBean);
			return "/product/SHO101";
		}

		try {
			// 検索実行
			// フォームモデル、ページングビーンを引数で渡す
			List<Product> productList = shoppingService.searchProductPage(form, pagingBean);

			// フォームモデル(検索条件)とページングビーンをセッションに保持
			session.setAttribute(SessionConstants.SEARCH_PARAM, form);
			session.setAttribute(SessionConstants.SELECT_PARAM, spform);
			session.setAttribute(SessionConstants.PRODUCT_LIST, productList);
			session.setAttribute(SessionConstants.PAGE, pagingBean);

			// バインドされたフォームモデルはリクエストスコープオブジェクトとしてEL式でアクセスできるので、
			// ページングビーンと検索結果のみaddObjectする
			model.addAttribute(SessionConstants.SEARCH_PARAM, form);
			model.addAttribute(SessionConstants.SELECT_PARAM, spform);
			model.addAttribute(SessionConstants.PRODUCT_LIST, productList);
			model.addAttribute(SessionConstants.PAGE, pagingBean);

		} catch (Exception e) {
			String message = messageSource.getMessage("content.MSG005", null, locale);
			model.addAttribute("MSG005", message);
		}

		model.addAttribute(SessionConstants.CATEGORY_LIST, session.getAttribute(SessionConstants.CATEGORY_LIST));

		return "/product/SHO101";
	}

	/**
	 * 検索結果表示（検索フォームから遷移）
	 * @param formModel
	 * @param session
	 * @return
	 * ページングボタンからの遷移 → リクエストパラメータにpageがある
	 * 商品詳細やカート画面からの遷移 → リクエストパラメータにpageが無い
	 * 400 But Request にならないように、required = falseを指定
	 */
	@RequestMapping(value = "/SHO101.html", method = RequestMethod.GET)
	public String searchMember(@RequestParam(required = false) Integer page, HttpSession session, Model model,
			Locale locale) {
		// セッションにページングビーンが存在する場合はセッションから取得
		// セッションに存在しない場合は、初期化したページングビーンを生成する
		PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
		if (pagingBean == null) {
			pagingBean = new PagingBean();
		}

		// pageに値がある → ページングボタンからの遷移 → 現在ページを更新
		// pageがnull → 商品詳細やカート画面からの遷移 → ページングはそのまま
		// またpageが0以下は不正
		if (page != null && page > 0) {
			pagingBean.setCurrentPage(page);
		}

		// セッションにフォームモデルが存在する場合はセッションから取得
		// セッションに存在しない場合は、初期化したフォームモデルを生成する
		SearchFormModel formModel = (SearchFormModel) session.getAttribute(SessionConstants.SEARCH_PARAM);
		if (formModel == null) {
			formModel = new SearchFormModel();
		}

		SelectProductFormModel spform = (SelectProductFormModel) session.getAttribute(SessionConstants.SELECT_PARAM);
		if (spform == null) {
			spform = new SelectProductFormModel();
		}

		// 検索実行
		// フォームモデル、ページングビーンを引数で渡す
		try {
			List<Product> productList = shoppingService.searchProductPage(formModel, pagingBean);

			// フォームモデル(検索条件)とページングビーンをセッションに保持
			session.setAttribute(SessionConstants.SEARCH_PARAM, formModel);
			session.setAttribute(SessionConstants.SELECT_PARAM, spform);
			session.setAttribute(SessionConstants.PRODUCT_LIST, productList);
			session.setAttribute(SessionConstants.PAGE, pagingBean);

			// フォームモデルはバインドされていないのでaddObjectする
			model.addAttribute(SessionConstants.SEARCH_PARAM, formModel);
			model.addAttribute(SessionConstants.SELECT_PARAM, spform);
			model.addAttribute(SessionConstants.PRODUCT_LIST, productList);
			model.addAttribute(SessionConstants.PAGE, pagingBean);
		} catch (Exception e) {
			String message = messageSource.getMessage("content.MSG005", null, locale);
			model.addAttribute("MSG005", message);

		}

		return "/product/SHO101";
	}

	/**
	 * 商品詳細(GET)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/SHO102.html", method = RequestMethod.GET)
	public String productsDetail(Model model, @RequestParam(name = "id", required = false) String id) {
		Product product = shoppingService.findProductOne(id);
		model.addAttribute("detailProduct", product);
		model.addAttribute("detailFormModel", new DetailFormModel());
		return "/product/SHO102";
	}

	/**
	 *商品登録結果
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/SHO103.html", method = RequestMethod.POST)
	public String bagProducts(@ModelAttribute SearchFormModel form,
			@ModelAttribute SelectProductFormModel spform, Model model, HttpSession session, Locale locale) {

		//商品個数が入力されたものをMapに追加
		LinkedHashMap<String, String> productMap = new LinkedHashMap<String, String>();
		//先頭に選択された商品コード(nullあり)
		String[] arraySelected = spform.getProductsSelect();
		//該当箇所に入力された商品個数(空文字あり)
		String[] arrayCnt = spform.getproductsCnt();
		//全ての商品コード
		String[] arrayCode = spform.getProductCode();

		for (int index = 0; index < arrayCode.length; index++) {
			//key=商品コード、value=購入個数(選択されていない商品も含めている)
			productMap.put(arrayCode[index], arrayCnt[index]);
		}

		int notNullCount = 0;
		//個数が入力された商品のみMapに追加
		LinkedHashMap<String, String> registerProducts = new LinkedHashMap<String, String>();
		for (int index = 0; index < arraySelected.length; index++) {
			if (arraySelected[index] != null) {
				//key=商品コード、value=購入個数(選択されていない商品は含めていない)
				registerProducts.put(arraySelected[index], productMap.get(arraySelected[index]));
				notNullCount++;
			}
		}

		if (notNullCount == 0) {
			String message = messageSource.getMessage("content.MSG006", null, locale);
			model.addAttribute("MSG006", message);
			PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
			if (pagingBean == null) {
				pagingBean = new PagingBean();
			}
			model.addAttribute(SessionConstants.SEARCH_PARAM, session.getAttribute(SessionConstants.SEARCH_PARAM));
			model.addAttribute(SessionConstants.SELECT_PARAM, spform);
			model.addAttribute(SessionConstants.PRODUCT_LIST, session.getAttribute(SessionConstants.PRODUCT_LIST));
			model.addAttribute(SessionConstants.CATEGORY_LIST,
					session.getAttribute(SessionConstants.CATEGORY_LIST));
			model.addAttribute(SessionConstants.PAGE, pagingBean);
			return "/product/SHO101";

		}

		//選択されていて商品個数が入力されていない場合
		for (String key : registerProducts.keySet()) {
			if (registerProducts.get(key).equals("")) {

				String message = messageSource.getMessage("content.MSG007", null, locale);
				model.addAttribute("MSG007", message);
				PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
				if (pagingBean == null) {
					pagingBean = new PagingBean();
				}
				model.addAttribute(SessionConstants.SEARCH_PARAM, session.getAttribute(SessionConstants.SEARCH_PARAM));
				model.addAttribute(SessionConstants.SELECT_PARAM, spform);
				model.addAttribute(SessionConstants.PRODUCT_LIST, session.getAttribute(SessionConstants.PRODUCT_LIST));
				model.addAttribute(SessionConstants.CATEGORY_LIST,
						session.getAttribute(SessionConstants.CATEGORY_LIST));
				model.addAttribute(SessionConstants.PAGE, pagingBean);
				return "/product/SHO101";
			}
		}

		//選択されていて商品個数が1以上999以下の数値ではない場合
		for (String key : registerProducts.keySet()) {
			try {
				int count = Integer.parseInt(registerProducts.get(key));
				if (!(count >= 1 && count <= 999)) {
					String message = messageSource.getMessage("content.MSG007", null, locale);
					model.addAttribute("MSG007", message);
					PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
					if (pagingBean == null) {
						pagingBean = new PagingBean();
					}
					model.addAttribute(SessionConstants.SEARCH_PARAM,
							session.getAttribute(SessionConstants.SEARCH_PARAM));
					model.addAttribute(SessionConstants.SELECT_PARAM,
							spform);
					model.addAttribute(SessionConstants.PRODUCT_LIST,
							session.getAttribute(SessionConstants.PRODUCT_LIST));
					model.addAttribute(SessionConstants.CATEGORY_LIST,
							session.getAttribute(SessionConstants.CATEGORY_LIST));
					model.addAttribute(SessionConstants.PAGE, pagingBean);
					return "/product/SHO101";
				}
			} catch (Exception e) {
				String message = messageSource.getMessage("content.MSG007", null, locale);
				model.addAttribute("MSG007", message);
				PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
				if (pagingBean == null) {
					pagingBean = new PagingBean();
				}
				model.addAttribute(SessionConstants.SEARCH_PARAM, session.getAttribute(SessionConstants.SEARCH_PARAM));
				model.addAttribute(SessionConstants.SELECT_PARAM, spform);
				model.addAttribute(SessionConstants.PRODUCT_LIST, session.getAttribute(SessionConstants.PRODUCT_LIST));
				model.addAttribute(SessionConstants.CATEGORY_LIST,
						session.getAttribute(SessionConstants.CATEGORY_LIST));
				model.addAttribute(SessionConstants.PAGE, pagingBean);
				return "/product/SHO101";
			}
		}

		//在庫数チェック
		if (!isStockSession(registerProducts, session)) {
			PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
			if (pagingBean == null) {
				pagingBean = new PagingBean();
			}
			model.addAttribute(SessionConstants.SEARCH_PARAM, session.getAttribute(SessionConstants.SEARCH_PARAM));
			model.addAttribute(SessionConstants.SELECT_PARAM, spform);
			model.addAttribute(SessionConstants.PRODUCT_LIST, session.getAttribute(SessionConstants.PRODUCT_LIST));
			model.addAttribute(SessionConstants.CATEGORY_LIST, session.getAttribute(SessionConstants.CATEGORY_LIST));
			model.addAttribute(SessionConstants.PAGE, pagingBean);
			String message = messageSource.getMessage("content.MSG008", null, locale);
			model.addAttribute("MSG008", message);
			return "/product/SHO101";
		}

		//選択した商品コードと個数をsessionに追加
		shoppingService.updateSessionBag(registerProducts, session);

		if (registerProducts != null) {
			//モデルに商品情報（購入数を含む）を追加
			model.addAttribute("productBean", setMapProductBean(registerProducts));
		}

		return "/product/SHO103";
	}

	/**
	 * お買い物かご画面表示(GET)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/KGO101.html", method = RequestMethod.GET)
	public String showBag(Model model, HttpSession session) {
		if (!model.asMap().containsKey("selectProductFormModel")) {
			model.addAttribute("selectProductFormModel", new SelectProductFormModel());
		}

		if (session.getAttribute("registerProductsMap") != null) {

			try {
				//セッションに商品情報（購入数を含む）を追加
				session.setAttribute("productBean", setSessionProductBean(session));
			} catch (Exception e) {
				return "/bag/KGO101";
			}
		}
		return "/bag/KGO101";
	}

	/**
	 * 商品登録結果からお買い物かご画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/KGO101.html", method = RequestMethod.POST)
	public String showBagInfo(Model model, HttpSession session) {
		if (!model.asMap().containsKey("selectProductFormModel")) {
			model.addAttribute("selectProductFormModel", new SelectProductFormModel());
		}
		if (session.getAttribute("registerProductsMap") != null) {

			try {
				//セッションに商品情報（購入数を含む）を追加
				session.setAttribute("productBean", setSessionProductBean(session));
			} catch (Exception e) {
				return "/bag/KGO101";
			}
		}
		return "/bag/KGO101";
	}

	/**
	 * 商品詳細からお買い物かご画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/KGO101.html", params = "detail", method = RequestMethod.POST)
	public String putDetailBag(@ModelAttribute @Valid DetailFormModel form,
			BindingResult errors, Model model, HttpSession session, Locale locale) {
		if (!model.asMap().containsKey("selectProductFormModel")) {
			model.addAttribute("selectProductFormModel", new SelectProductFormModel());
		}

		LinkedHashMap<String, String> registerProduct = new LinkedHashMap<String, String>();
		registerProduct.put(form.getProductCode(), form.getCount());

		//選択されていて商品個数が1以上999以下の数値ではない場合
		for (String key : registerProduct.keySet()) {
			try {
				int count = Integer.parseInt(registerProduct.get(key));
				if (!(count >= 1 && count <= 999)) {
					model.addAttribute("detailProduct", shoppingService.findProductOne(form.getProductCode()));
					String message = messageSource.getMessage("content.MSG007", null, locale);
					model.addAttribute("MSG007", message);
					return "/product/SHO102";
				}
			} catch (Exception e) {
				model.addAttribute("detailProduct", shoppingService.findProductOne(form.getProductCode()));
				String message = messageSource.getMessage("content.MSG007", null, locale);
				model.addAttribute("MSG007", message);
				return "/product/SHO102";
			}
		}

		//在庫数チェック
		if (!isStockSession(registerProduct, session)) {
			model.addAttribute("detailProduct", shoppingService.findProductOne(form.getProductCode()));
			String message = messageSource.getMessage("content.MSG008", null, locale);
			model.addAttribute("MSG008", message);
			return "/product/SHO102";
		}

		//選択した商品コードと個数をsessionに追加
		shoppingService.updateSessionBag(registerProduct, session);

		if (session.getAttribute("registerProductsMap") != null) {
			//セッションに商品情報（購入数を含む）を追加
			try {
				session.setAttribute("productBean", setSessionProductBean(session));
			} catch (Exception e) {
				return "/bag/KGO101";
			}
		}
		return "/bag/KGO101";
	}

	/**
	 * お買い物確認画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/KGO102.html", method = RequestMethod.POST)
	public String showBagConf(@ModelAttribute SelectProductFormModel form, Model model, HttpSession session,
			Locale locale) {
		if (!model.asMap().containsKey("selectProductFormModel")) {
			model.addAttribute("selectProductFormModel", new SelectProductFormModel());
		}

		if (session.getAttribute("productBean") != null) {
			List<ProductBean> pb = (List<ProductBean>) session.getAttribute("productBean");
			LinkedHashMap<String, String> sessionProductsMap = (LinkedHashMap<String, String>) session
					.getAttribute("registerProductsMap");

			//商品個数をMapに追加
			LinkedHashMap<String, String> productMap = new LinkedHashMap<String, String>();
			String[] arrayCnt = form.getproductsCnt();
			String[] arrayCode = form.getProductCode();
			int index = 0;
			//選択されていて商品個数が1以上999以下の数値ではない場合
			for (String inputCount : arrayCnt) {
				try {
					int count = Integer.parseInt(inputCount);
					if (!(count >= 1 && count <= 999)) {
						String message = messageSource.getMessage("content.MSG007", null, locale);
						model.addAttribute("MSG007", message);
						model.addAttribute("productBean", pb);
						model.addAttribute("selectProductFormModel", form);
						return "/bag/KGO101";
					}
				} catch (Exception e) {
					String message = messageSource.getMessage("content.MSG007", null, locale);
					model.addAttribute("MSG007", message);
					model.addAttribute("productBean", pb);
					model.addAttribute("selectProductFormModel", form);
					return "/bag/KGO101";
				}
				//個数が変更された場合情報を更新する
				for (String code : sessionProductsMap.keySet()) {
					if (code.equals(arrayCode[index])) {
						sessionProductsMap.put(code, inputCount);
					}
				}
				index++;
			}

			//sessionのお買い物かご更新
			session.setAttribute("registerProductsMap", sessionProductsMap);
			try {
				session.setAttribute("productBean", setSessionProductBean(session));
			} catch (Exception e) {
				return "/bag/KGO102";
			}

			List<ProductBean> newPb = (List<ProductBean>) session.getAttribute("productBean");

			//在庫数チェック
			if (!isStock(session)) {
				String message = messageSource.getMessage("content.MSG008", null, locale);
				model.addAttribute("MSG008", message);
				model.addAttribute("selectProductFormModel", form);
				return "/bag/KGO101";
			}

			//料金の計算
			Fee fee = shoppingService.calcFeeInfo(newPb);
			model.addAttribute("fee", fee);
			model.addAttribute("productBean", newPb);
			session.setAttribute("fee", fee);
		}
		return "/bag/KGO102";
	}

	/**
	 * 注文結果画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/KGO103.html", method = RequestMethod.POST)
	public String showOrder(Model model, HttpSession session, Locale locale) {
		//商品が取り扱われているか確認
		List<ProductBean> listProductBean = (List<ProductBean>) session.getAttribute("productBean");

		//商品コードがすべて存在するか確認
		List<String> noExistProduct = shoppingService.findProducts(listProductBean);
		if (noExistProduct.size() != 0) {
			String message = messageSource.getMessage("content.MSG010", null, locale);
			model.addAttribute("MSG010", message);
			model.addAttribute("noExistProduct", noExistProduct);
			return "/bag/KGO102";
		}

		//商品の削除フラグが1のとき
		List<Product> deleteProduct = shoppingService.findSelectedDeleteProducts(listProductBean);
		if (deleteProduct.size() != 0) {
			String message = messageSource.getMessage("content.MSG010", null, locale);
			model.addAttribute("MSG010", message);
			model.addAttribute("noProduct", deleteProduct);
			return "/bag/KGO102";
		}

		try {
			if (shoppingService.order(session) != 1) {
				return "/common/ERR101";
			}

		} catch (Exception e) {
			return "/common/ERR101";
		}

		//セッションに追加されている買い物情報削除
		session.removeAttribute("registerProductsMap");
		session.removeAttribute("registerProducts");
		session.removeAttribute("productBean");
		session.removeAttribute("fee");

		return "/bag/KGO103";
	}

	/**
	 * お買い物かごの取り消しボタン(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/KGO101.html", params = "delete", method = RequestMethod.POST)
	public String deleteProductOne(@ModelAttribute SelectProductFormModel form,
			Model model, HttpSession session, Locale locale) {

		//取り消し対象の選択がされていない場合
		if (form.getProductsSelect() == null) {
			String message = messageSource.getMessage("content.MSG009", null, locale);
			model.addAttribute("MSG009", message);
			model.addAttribute("selectProductFormModel", form);
			return "/bag/KGO101";
		}

		if (session.getAttribute("registerProductsMap") != null) {
			LinkedHashMap<String, String> registerProducts = (LinkedHashMap<String, String>) session
					.getAttribute("registerProductsMap");
			for (String code : form.getProductsSelect()) {
				registerProducts.remove(code);
			}
			//セッションに商品情報（購入数を含む）を追加
			try {
				session.setAttribute("productBean", setSessionProductBean(session));
			} catch (Exception e) {
				return "/bag/KGO101";
			}
		}
		return "/bag/KGO101";
	}

	/**
	 * 在庫数チェック
	 * @param product 選択された商品の商品コードと数量
	 * @param form
	 * @param session
	 * @return
	 */
	private boolean isStockSession(LinkedHashMap<String, String> product, HttpSession session) {
		if (shoppingService.isProductStockSession(product, session)) {
			return true;
		}
		return false;
	}

	/**
	 * 在庫数チェック(注文するボタン押下時)
	 * @param product 選択された商品の商品コードと数量
	 * @param form
	 * @param session
	 * @return
	 */
	private boolean isStock(HttpSession session) {
		if (shoppingService.isProductStock(session)) {
			return true;
		}
		return false;
	}

	/**
	 * 購入する商品の情報をBeanに詰め替え(session)
	 * @throws Exception
	 */
	private List<ProductBean> setSessionProductBean(HttpSession session) throws Exception {
		List<ProductBean> listPb = new ArrayList<ProductBean>();
		try {
			listPb = shoppingService.setSessionProductBean(session);
		} catch (Exception e) {
			throw e;
		}

		return listPb;
	}

	/**
	 * 購入する商品の情報をBeanに詰め替え(Map)
	 */
	private List<ProductBean> setMapProductBean(LinkedHashMap<String, String> product) {
		return shoppingService.setMapProductBean(product);
	}

	/**
	 * 共通エラー画面
	 * @return
	 */
	@RequestMapping("/common/ERR101.html")
	public String showError() {
		return "common/ERR101";
	}
}
