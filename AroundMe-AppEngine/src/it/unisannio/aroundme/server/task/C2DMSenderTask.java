/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.server.task;

import it.unisannio.aroundme.server.c2dm.C2DMNotificationSender;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Task per la Google TaskQueue che viene utilizata per inviare messaggi tramite
 * il Google Cloud to Device Messagging Framework (C2DM).
 * Il task &egrave; implementato in modo tale da gestire il retry nel caso di errori recuperabili.
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMSenderTask extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(C2DMSenderTask.class.getName());
	
	/**
	 * L'URI utilizzata per poter raggiungere e  quindi eseguire il {@link C2DMSenderTask}
	 */
	public static final String URI = "/task/notificationsender";
	
	/**
	 * L'header che contiene il numero di volte in cui una specifica task è stata invocata 
	 */
	private final String RETRY_COUNT = "X-AppEngine-TaskRetryCount";
	/**
	 * Il massimo numero di tentativi nel quale riprovare la task in caso di errore
	 */
	private final int MAX_RETRY = 3;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		log.info("Excecuting C2DMSenderTask");
		String retryCount = req.getHeader(RETRY_COUNT);
		if (retryCount != null) {
			int retryCnt = Integer.parseInt(retryCount);
			if (retryCnt > MAX_RETRY) {
				resp.setStatus(200); // La task non verrà ulteriormente riprovata
				return; 
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
				// La Task Queue è implementata in modo tale da riprovare il task nel caso di errori
			}
		} catch (IOException e) {
			resp.setStatus(200);
			log.severe(e.toString());
			resp.getOutputStream().write(("Non-retriable error:" + e.toString()).getBytes());
		}
	}
}