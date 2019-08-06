package jp.co.pscsrv.controller;

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

import jp.co.pscsrv.bean.PagingBean;
import jp.co.pscsrv.constants.SessionConstants;
import jp.co.pscsrv.dto.Log;
import jp.co.pscsrv.dto.OrderProduct;
import jp.co.pscsrv.dto.Staff;
import jp.co.pscsrv.model.DeleteFormModel;
import jp.co.pscsrv.model.LoginFormModel;
import jp.co.pscsrv.model.SearchFormModel;
import jp.co.pscsrv.service.AdminShoppingService;

@Controller
public class AdminShoppingController {

	@Autowired
	private AdminShoppingService shoppingService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * index.html表示
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showIndex(Model model, HttpSession session) {
		model.addAttribute("loginFormModel", new LoginFormModel());
		return "/log/LOG102";
	}

	/**
	 * 管理者ログイン画面表示
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/LOG102.html", method = RequestMethod.GET)
	public String showLogin(Model model) {
		model.addAttribute("loginFormModel", new LoginFormModel());
		return "/log/LOG102";
	}

	/**
	 * 管理者メニュー画面表示(GET)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEN102.html", method = RequestMethod.GET)
	public String showMenu() {
		return "/menu/MEN102";
	}

	/**
	 * 管理者メニュー画面表示(POST)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/MEN102.html", method = RequestMethod.POST)
	public String backMenu() {
		return "/menu/MEN102";
	}

	/**
	 * 管理者ログイン
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", params = "login", method = RequestMethod.POST)
	public String login(@ModelAttribute @Valid LoginFormModel form, BindingResult errors, Model model,
			HttpSession session, Locale locale) {
		if (errors.hasErrors()) {
			/* 問題点 IDを初期化したいがされていない*/
			form.setMemberNo("");
			model.addAttribute("loginFormModel", form);
			return "/log/LOG102";
		}

		try {
			Staff user = shoppingService.loginUser(form.getMemberNo(), form.getPass());
			session.setAttribute("loginUser", user);
			model.addAttribute("form", form);
		} catch (Exception e) {
			String message = messageSource.getMessage("content.MSG012", null, locale);
			model.addAttribute("error", message);
			//IDを初期化
			form.setMemberNo("");
			return "/log/LOG102";
		}

		return "/menu/MEN102";
	}

	/**
	 * 管理者ログアウト処理
	 */
	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public String logout(Model model, HttpSession session) {
		session.invalidate();
		model.addAttribute("loginFormModel", new LoginFormModel());
		return "/log/LOG102";
	}

	/**
	 * 購入履歴検索画面表示(GET) ページングあり
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/searchForm", method = RequestMethod.GET)
	public String showPageProductInfo(Model model, HttpSession session) {
		if (!model.asMap().containsKey(SessionConstants.SEARCH_PARAM)) {
			model.addAttribute(SessionConstants.SEARCH_PARAM, new SearchFormModel());
		}

		// 最初に検索画面を表示するコントローラなのでページングビーンを初期化する。
		PagingBean pagingBean = new PagingBean();

		// 最初に検索画面を表示するコントローラなのでセッション初期化
		session.removeAttribute(SessionConstants.SEARCH_PARAM);
		session.removeAttribute(SessionConstants.PAGE);

		return "/function/REC101";
	}

	/**
	 * 購入履歴検索画面表示(戻るボタン)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/REC101.html", method = RequestMethod.POST)
	public String backProductInfo(Model model, HttpSession session) {

		if (session.getAttribute("searchFormModel") != null) {
			PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
			if (pagingBean == null) {
				pagingBean = new PagingBean();
			}
			//遷移前のフォーム、表示された商品リスト、ページをモデルに追加
			model.addAttribute(SessionConstants.SEARCH_PARAM, session.getAttribute(SessionConstants.SEARCH_PARAM));
			model.addAttribute(SessionConstants.LOG_LIST, session.getAttribute(SessionConstants.LOG_LIST));
			model.addAttribute(SessionConstants.PAGE, pagingBean);
		}

		return "/function/REC101";
	}

	/**
	 * 購入履歴検索画面表示(POST) ページング用
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String psearchProducts(@ModelAttribute @Valid SearchFormModel form, BindingResult errors,
			Model model, HttpSession session, Locale locale) {

		// POSTで検索条件を受け取る → 検索条件が変わるので、ページングを初期化
		PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
		if (pagingBean == null) {
			pagingBean = new PagingBean();
		}

		if (errors.hasErrors()) {
			return "/function/REC101";
		}

		try {

			// 検索実行
			// フォームモデル、ページングビーンを引数で渡す
			List<Log> productList = shoppingService.searchLogPage(form, pagingBean);

			// フォームモデル(検索条件)とページングビーンをセッションに保持
			session.setAttribute(SessionConstants.SEARCH_PARAM, form);
			session.setAttribute(SessionConstants.LOG_LIST, productList);
			session.setAttribute(SessionConstants.PAGE, pagingBean);

			// バインドされたフォームモデルはリクエストスコープオブジェクトとしてEL式でアクセスできるので、
			// ページングビーンと検索結果のみaddObjectする
			model.addAttribute(SessionConstants.SEARCH_PARAM, form);
			model.addAttribute(SessionConstants.LOG_LIST, productList);
			model.addAttribute(SessionConstants.PAGE, pagingBean);

		} catch (Exception e) {
			String message = messageSource.getMessage("content.MSG013", null, locale);
			model.addAttribute("MSG013", message);
		}

		return "/function/REC101";
	}

	/**
	 * 購入履歴結果表示（検索フォームから遷移）
	 * @param formModel
	 * @param session
	 * @return
	 * ページングボタンからの遷移 → リクエストパラメータにpageがある
	 * 商品詳細からの遷移 → リクエストパラメータにpageが無い
	 * 400 But Request にならないように、required = falseを指定
	 */
	@RequestMapping(value = "/REC101.html", method = RequestMethod.GET)
	public String searchMember(@RequestParam(required = false) Integer page, HttpSession session, Model model) {
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

		// 検索実行
		// フォームモデル、ページングビーンを引数で渡す
		try {
			List<Log> productList = shoppingService.searchLogPage(formModel, pagingBean);

			// フォームモデル(検索条件)とページングビーンをセッションに保持
			session.setAttribute(SessionConstants.SEARCH_PARAM, formModel);
			session.setAttribute(SessionConstants.LOG_LIST, productList);
			session.setAttribute(SessionConstants.PAGE, pagingBean);

			// フォームモデルはバインドされていないのでaddObjectする
			model.addAttribute(SessionConstants.SEARCH_PARAM, formModel);
			model.addAttribute(SessionConstants.LOG_LIST, productList);
			model.addAttribute(SessionConstants.PAGE, pagingBean);
		} catch (Exception e) {
			return "/common/ERR101";
		}

		return "/function/REC101";
	}

	/**
	 * 履歴詳細(GET)
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/REC102.html", method = RequestMethod.GET)
	public String productsDetail(Model model, @RequestParam(name = "id", required = false) String id) {
		try {
			Log log = shoppingService.searchLogDetail(id);
			model.addAttribute("detailLog", log);
			List<OrderProduct> orderProductList = shoppingService.searchOrderProducts(log.getCollectNo());
			model.addAttribute("orderProductLog", orderProductList);
		} catch (Exception e) {
			e.printStackTrace();
			return "/common/ERR101";
		}
		return "/function/REC102";
	}

	/**
	 * 購入履歴削除確認
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/REC201.html", method = RequestMethod.POST)
	public String productsDeleteConf(@ModelAttribute DeleteFormModel form, Model model) {
		try {
			Log log = shoppingService.searchLogDetail(form.getOrderNo());
			model.addAttribute("detailLog", log);

			List<OrderProduct> orderProductList = shoppingService.searchOrderProducts(log.getCollectNo());
			model.addAttribute("orderProductLog", orderProductList);
		} catch (Exception e) {
			e.printStackTrace();
			return "/common/ERR101";
		}
		return "/function/REC201";
	}

	/**
	 * 購入履歴削除結果
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/REC202.html", method = RequestMethod.POST)
	public String productsDelete(@ModelAttribute DeleteFormModel form, Model model) {
		try {
			shoppingService.logDelete(form.getCollectNo());
		} catch (Exception e) {
			e.printStackTrace();
			return "/common/ERR101";
		}
		return "/function/REC202";
	}

	/**
	 * 購入履歴検索へ戻る
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "research", method = RequestMethod.POST)
	public String researchLog(Model model, HttpSession session, Locale locale) {
		// POSTで検索条件を受け取る → 検索条件が変わるので、ページングを初期化
		PagingBean pagingBean = (PagingBean) session.getAttribute(SessionConstants.PAGE);
		if (pagingBean == null) {
			pagingBean = new PagingBean();
		}

		// セッションにフォームモデルが存在する場合はセッションから取得
		// セッションに存在しない場合は、初期化したフォームモデルを生成する
		SearchFormModel formModel = (SearchFormModel) session.getAttribute(SessionConstants.SEARCH_PARAM);
		if (formModel == null) {
			formModel = new SearchFormModel();
		}

		try {

			// 検索実行
			// フォームモデル、ページングビーンを引数で渡す
			List<Log> productList = shoppingService.searchLogPage(formModel, pagingBean);

			// フォームモデル(検索条件)とページングビーンをセッションに保持
			session.setAttribute(SessionConstants.SEARCH_PARAM, formModel);
			session.setAttribute(SessionConstants.LOG_LIST, productList);
			session.setAttribute(SessionConstants.PAGE, pagingBean);

			// バインドされたフォームモデルはリクエストスコープオブジェクトとしてEL式でアクセスできるので、
			// ページングビーンと検索結果のみaddObjectする
			model.addAttribute(SessionConstants.SEARCH_PARAM, formModel);
			model.addAttribute(SessionConstants.LOG_LIST, productList);
			model.addAttribute(SessionConstants.PAGE, pagingBean);

		} catch (Exception e) {
			String message = messageSource.getMessage("content.MSG013", null, locale);
			model.addAttribute("MSG013", message);
		}

		return "/function/REC101";
	}

	/**
	 * 共通エラー画面
	 * @return
	 */
	@RequestMapping("/common/ERR101.html")
    public String showError() {
        return "/common/ERR101";
    }
}
