package it.unisannio.aroundme.server.servlet;

import it.unisannio.aroundme.server.UserImpl;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class ServletFilter implements Filter {
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if (req.getProtocol().equals("HTTP/1.1")) {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			if (request.getMethod().equalsIgnoreCase("PUT"))
				chain.doFilter(req, res);
			else {
				String facebookAuthToken = request.getHeader("X-AccessToken");
				Objectify ofy = ObjectifyService.begin();
				if(ofy.query(UserImpl.class).filter("authToken", facebookAuthToken).count()==1)
					chain.doFilter(req, res);
				else{
					response.sendError(403);
				//	System.out.println("Filtered request: "+ request.getPathInfo() + " method: "+ request.getMethod());
				}
			}
		}
	}

	@Override
	public void destroy() {}
}
