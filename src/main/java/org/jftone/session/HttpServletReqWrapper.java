package org.jftone.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.jftone.config.PropertyConfigurer;
import org.jftone.util.StringUtil;

public final class HttpServletReqWrapper extends HttpServletRequestWrapper {
	private String sessionId = null;
	private HttpServletRequest request;
	private HttpSessionRepository repository;
	private HttpSession curSession = null;

	public HttpServletReqWrapper(HttpServletRequest request) {
		super(request);
		this.sessionId = getSessionId(request); // 创建sessionid
		this.request = request;
		this.repository = HttpSessionRepository.getInstance();
		this.curSession = null;
	}

	public HttpSession getSession(boolean create) {
		if (curSession != null) {
			return curSession;
		}
		if (!StringUtil.isBlank(this.sessionId)) {
			curSession = repository.getSession(sessionId, request.getServletContext());
			if (curSession != null) {
				return curSession;
			}
		}
		if (!create) {
			return null;
		}
		curSession = repository.newSession(request.getServletContext());
		sessionId = curSession.getId();
		return curSession;
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public String getRequestedSessionId() {
		return this.sessionId;
	}

	private String getSessionId(HttpServletRequest request) {
		//优先从请求参数获取session数据KEY
		String sid = null;
		String paramEnabled = PropertyConfigurer.get(PropertyConfigurer.SESSION_PARAMETER, "false");
		if (null != paramEnabled && paramEnabled.equals("true")) {
			sid = request.getParameter(HttpSessionRepository.SID_TOKEN);
			if(null != sid) {
				return sid;
			}
		}
		sid = request.getHeader(HttpSessionRepository.SID_TOKEN);
		if (sid != null)
			return sid;
		Cookie cookies[] = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(HttpSessionRepository.SESSION_ID)) {
					sid = cookie.getValue();
					break;
				}
			}
		}
		return sid;
	}
}
