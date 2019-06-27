package com.ibm.cwt.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ibm.cwt.dto.ReportRowDTO;
import com.ibm.cwt.exceptions.CWTEmailException;

public class CWTMailer {
	private static final Logger LOGGER = Logger.getLogger(CWTMailer.class.getName());
	private final static String file = "mail.properties";
	private static Properties props;
	
	static {
		try {
			InputStream fis = CWTMailer.class.getClassLoader().getResourceAsStream(file);
			props = new Properties();
			props.load(fis);
			LOGGER.config(props.toString());
		} catch (IOException e) {
			LOGGER.severe("ERROR: '"+file + "' cannot be opened to get notification wording. Notifications are not available");
			LOGGER.severe(e.getMessage());
		} 
	}
	
	public void sendMissingNotifications(Collection<ReportRowDTO> missingPersons) throws CWTEmailException {
		assert props!=null;

		Map<String, Collection<ReportRowDTO>> mapa = groupRowsByEmail(missingPersons);
		
		for(Entry<String, Collection<ReportRowDTO>> row : mapa.entrySet()){
			try {
				MimeMessage message = createMimeMessage();
				message.setSubject(props.getProperty("notifications.subject.wording"));
				String body= props.getProperty("notifications.body.wording");
				
				String weeks = "";
				for(ReportRowDTO dto : row.getValue()){
					body = body.replaceAll("@@cc", dto.getCc());
					body = body.replaceAll("@@dept", dto.getDept());
					body = body.replaceAll("@@serial", dto.getSerial());
					body = body.replaceAll("@@status", dto.getStatus());
					body = body.replaceAll("@@name", dto.getName());
					body = body.replaceAll("@@email", row.getKey());
					weeks += dto.getWeek()+"<br/>";
				}
				body = body.replaceAll("@@weeks", weeks);
				
				message.setContent(body, "text/html");
				LOGGER.finest("email body" + body);
				Address addressFrom = new InternetAddress(props.getProperty("mail.from"));
				message.setFrom(addressFrom);
				
	
				if(areNotificationsEnabled()) {
					Address addressTo;
					if(isRedirectEnabled()){
						LOGGER.info("Sending notification to: " + props.getProperty("mail.redirect.to") + " instead of: " + row.getKey().trim() + ", and manager "+ row.getValue().iterator().next().getManagerEmail());
						addressTo =  new InternetAddress(props.getProperty("mail.redirect.to"));
					} else {
						LOGGER.info("Sending notification to " + row.getKey().trim() + " and manager "+ row.getValue().iterator().next().getManagerEmail());
						addressTo =  new InternetAddress(row.getKey().trim());
						if(row.getValue().iterator().next().getManagerEmail() != null) {
							message.addRecipient(Message.RecipientType.CC  , new InternetAddress(row.getValue().iterator().next().getManagerEmail()));
						}
					}
					message.addRecipient(Message.RecipientType.TO  , addressTo);
					Transport.send(message);
				} else {
					LOGGER.warning("Notification to " + row.getKey().trim() + " and manager "+ row.getValue().iterator().next().getManagerEmail() +" disabled per configuration");
				}
			} catch (MessagingException e) {
				LOGGER.warning("ERROR sending notification to " + row.getKey().trim() + ": " + e.getMessage());
				throw new CWTEmailException();
			}
		}
	}

	private boolean isRedirectEnabled() {
		return Boolean.parseBoolean(props.getProperty("mail.redirect"));
	}

	private Map<String, Collection<ReportRowDTO>> groupRowsByEmail(Collection<ReportRowDTO> missingPersons) {
		Map<String, Collection<ReportRowDTO>> mapa = new HashMap<String, Collection<ReportRowDTO>>();
		
		for(ReportRowDTO row : missingPersons){
			String email = row.getEmail();
			if(email != null && !"".equals(email.trim())) {
				email = email.trim();
				if(mapa.get(email) == null){
					mapa.put(email, new ArrayList<ReportRowDTO>());
				}
				mapa.get(email).add(row);
			} else {
				LOGGER.finer("Skipping mail to " + row.getSerial() + " due to missing email");
			}
		}
		return mapa;
	}

	private boolean areNotificationsEnabled() {
		return Boolean.parseBoolean(props.getProperty("notifications.enabled"));
	}

	private static MimeMessage createMimeMessage() {
    	props.put("mail.smtp.host", props.getProperty("mail.smtp.host"));
    	props.put("mail.smtp.port", props.getProperty("mail.smtp.port"));
    	Session session = Session.getInstance(props, null);
    	MimeMessage message = new MimeMessage(session);
		return message;
	}
}
