package it.unisannio.server.test.servlet;

import it.unisannio.aroundme.server.servlet.ServletFilter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Una Servlet creata ai fini del testing di {@link ServletFilter}
 * che ritorna una risposta con status code 200 ad ogni richiesta
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class MockServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
	}
}
