package org.jftone.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

public final class HttpServletRespWrapper extends HttpServletResponseWrapper {
	
	public HttpServletRespWrapper(HttpServletRequest request, HttpServletResponse response) {
		super(response);
		HttpSession session = request.getSession();
		String sid = session.getId();
		//response.setHeader(HttpSessionRepository.TOKEN_HEADER_NAME, sid);
		if (sid != null) {
			Cookie mycookies = new Cookie(HttpSessionRepository.SESSION_ID, sid);
			mycookies.setMaxAge(-1);	//浏览器进程
			if (HttpSessionRepository.COOKIE_DOMAIN != null && HttpSessionRepository.COOKIE_DOMAIN.length() > 0) {
				mycookies.setDomain(HttpSessionRepository.COOKIE_DOMAIN);
			}
			mycookies.setPath(HttpSessionRepository.COOKIE_PATH);
			response.addCookie(mycookies);
		}
	}

}
