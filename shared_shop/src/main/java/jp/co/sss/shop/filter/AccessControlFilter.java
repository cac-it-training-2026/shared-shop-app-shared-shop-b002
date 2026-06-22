package jp.co.sss.shop.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;

/**
 * アクセス制御用フィルタ
 */
public class AccessControlFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		String requestURI = httpRequest.getRequestURI();
		String contextPath = httpRequest.getContextPath();

		// /admin/ 配下のURLへのアクセスをチェック
		if (requestURI.startsWith(contextPath + "/admin/")) {
			UserBean user = (UserBean) session.getAttribute("user");

			if (user == null) {
				// 未ログインの場合はログイン画面へリダイレクト
				httpResponse.sendRedirect(contextPath + "/login");
				return;
			} else if (!"ADMIN".equals(user.getRole())) {
				// ADMIN権限を持たない場合はトップページへリダイレクト（アクセス拒否）
				httpResponse.sendRedirect(contextPath + "/");
				return;
			}
		}

		chain.doFilter(request, response);
	}
}
