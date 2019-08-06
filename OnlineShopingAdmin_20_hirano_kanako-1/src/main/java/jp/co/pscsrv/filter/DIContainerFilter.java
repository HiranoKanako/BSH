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

	private static final String FUNCTION_PATH = "/REC";
	private static final String MENU_PATH = "/MEN";

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
		if (requestURI != null && requestURI.matches("^" + FUNCTION_PATH + "*.*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG102.html");
				return;
			}
		}

		if (requestURI != null && requestURI.matches("^" + MENU_PATH + "*.*$")) {
			// ここにリクエスト時にフィルターで実行したいことを記述します。
			// ログイン情報が無い場合は、ログイン画面にリダイレクトする
			Object loginInfoObj = session.getAttribute("loginUser");
			if (loginInfoObj == null) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/LOG102.html");
				return;
			}
		}

		// 次のフィルターチェーンに進む。
		chain.doFilter(request, response);

	}
}
