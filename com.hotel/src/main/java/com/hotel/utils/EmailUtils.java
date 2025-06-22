package com.hotel.utils;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailUtils {

	@Autowired
	private JavaMailSender eMailSender;

	public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("nandyr183@gmail.com");
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		if (list != null && list.size() > 0) {
			message.setCc(getCcArray(list));
		}
		eMailSender.send(message);
		try {
			eMailSender.send(message);
		} catch (MailException ex) {
		    System.out.printf("Email sending failed: {}", ex.getMessage());
		}

	}

	private String[] getCcArray(List<String> ccList) {
		String[] cc = new String[ccList.size()];
		for (int i = 0; i < ccList.size(); i++) {
			cc[i] = ccList.get(i);

		}
		return cc;
	}
}
