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



/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserQueryServlet extends HttpServlet{
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
}

