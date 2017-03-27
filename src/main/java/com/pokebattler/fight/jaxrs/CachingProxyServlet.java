package com.pokebattler.fight.jaxrs;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

public class CachingProxyServlet extends ProxyServlet {
	private static final long serialVersionUID = -7287465673247392307L;
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String P_CACHE_CONTROL = "cacheControl";
	private String cacheControl = null;

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		cacheControl = getConfigParam(P_CACHE_CONTROL);
		if (cacheControl != null && cacheControl.length() == 0) {
			cacheControl = null;
		}

	}

	@Override
	protected void copyResponseHeaders(HttpResponse proxyResponse, HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		for (Header header : proxyResponse.getAllHeaders()) {
			if (!header.getName().equals(CACHE_CONTROL))
				copyResponseHeader(servletRequest, servletResponse, header);
		}
		if (cacheControl != null) {
			servletResponse.addHeader(CACHE_CONTROL, cacheControl);
		}

	}

}
