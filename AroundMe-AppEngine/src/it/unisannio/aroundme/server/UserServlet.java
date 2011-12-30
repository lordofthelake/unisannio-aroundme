package it.unisannio.aroundme.server;

import it.unisannio.aroundme.model.SerializerUtils;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;



/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, final HttpServletResponse resp)	throws ServletException, IOException {
		try {
			UserQuery query = UserQuery.SERIALIZER.fromXML(SerializerUtils.getDocumentBuilder().parse(req.getInputStream()));
			Collection<? extends User> users = query.call();
			resp.setContentType("text/xml");
			Node xml = SerializerUtils.getCollectionSerializer(User.class).toXML(users);
			SerializerUtils.writeXML(xml, resp.getOutputStream());
		} catch (Exception e) {
			resp.sendError(500);
		}				
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			User user = (UserImpl) User.SERIALIZER.fromXML(SerializerUtils.getDocumentBuilder().parse(req.getInputStream()));
			Objectify ofy = ObjectifyService.begin();
			ofy.put(user);
		} catch (SAXException e) {
			resp.sendError(500);
		}
	}
}

