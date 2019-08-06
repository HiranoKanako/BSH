package jp.co.pscsrv.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class DIContainerFilter implements Filter {

	private static final String MEM201_PATH = "/MEM201";
	private static final String MEM202_PATH = "/MEM202";
	private static final String MEM203_PATH = "/MEM203";
	private static final String MEM204_PATH = "/MEM204";
	private static final String MEM301_PATH = "/MEM301";
	private static final String MEM302_PATH = "/MEM302";
	private static final String KGO103_PATH = "/KGO103";

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// ここでフィルタークラスの初期化を行います。
		;
	}

	@Override
	public void destroy() {
		// ここでフィルタークラスの後始末を行います。
		;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// filterはHttpServletXxxxxxの親クラスであるServletXxxxxで引数を受け取るので、
		// ServletRequestをHttpServletRequestにキャストする
		// ServletResponseをHttpServletResponseにキャストする
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// リクエストURLを取得する
		String requestURI = httpRequest.getRequestURI();

		// HttpSessionを取得する
		HttpSession session = httpRequest.getSession();

		// パス部分とフィルタリングしたいパス文字列がマッチしたらフィルタ実行
		if (requestURI != null && requestURI.matches("^" + MEM201_PATH + ".*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				session.setAttribute("prevPage", "MEM201");
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG101.html");
				return;
			}
		}

		// パス部分とフィルタリングしたいパス文字列がマッチしたらフィルタ実行
		if (requestURI != null && requestURI.matches("^" + MEM202_PATH + ".*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG101.html");
				return;
			}
		}

		// パス部分とフィルタリングしたいパス文字列がマッチしたらフィルタ実行
		if (requestURI != null && requestURI.matches("^" + MEM203_PATH + ".*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG101.html");
				return;
			}
		}

		// パス部分とフィルタリングしたいパス文字列がマッチしたらフィルタ実行
		if (requestURI != null && requestURI.matches("^" + MEM204_PATH + ".*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG101.html");
				return;
			}
		}

		if (requestURI != null && requestURI.matches("^" + MEM301_PATH + ".*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG101.html");
				return;
			}
		}

		if (requestURI != null && requestURI.matches("^" + MEM302_PATH + ".*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG101.html");
				return;
			}
		}

		if (requestURI != null && requestURI.matches("^" + KGO103_PATH + ".*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				session.setAttribute("prevPage", "KGO102");
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG101.html");
				return;
			}
		}

		// 次のフィルターチェーンに進む。
		chain.doFilter(request, response);

	}
}
