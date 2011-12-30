package it.unisannio.aroundme.server.c2dm;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class C2DMSenderTask extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final String RETRY_COUNT = "X-AppEngine-TaskRetryCount";
	private final int MAX_RETRY = 3;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String retryCount = req.getHeader(RETRY_COUNT);
		if (retryCount != null) {
			int retryCnt = Integer.parseInt(retryCount);
			if (retryCnt > MAX_RETRY) {
				resp.setStatus(200);
				return; // La task non verrà ulteriormente riprovata
			}
		}
		
		String registrationId = req.getParameter("registrationId");
		long userId = Long.parseLong(req.getParameter("userId"));

		try {
			boolean sent = C2DMNotificationSender.send(registrationId, userId);
			if (sent) {
				resp.setStatus(200);
				resp.getOutputStream().write("OK".getBytes());
			} else {
				resp.setStatus(500);
				// La Task Queue è implementata in modo tale da riprovare il task nel caso di errore 500
			}
		} catch (IOException e) {
			resp.setStatus(200);
			resp.getOutputStream().write(("Non-retriable error:" + e.toString()).getBytes());
		}
	}
}