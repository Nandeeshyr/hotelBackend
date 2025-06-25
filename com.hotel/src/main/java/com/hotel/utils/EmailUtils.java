package com.hotel.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

	public void forgotMail(String to, String subject, String password) throws MessagingException {
		MimeMessage message = eMailSender.createMimeMessage(); // Creates a blank email object using JavaMail’s
																// MimeMessage.
		MimeMessageHelper helper = new MimeMessageHelper(message, true); // This wrapper simplifies setting content and headers on the email. The true flag
																			// enables multipart (needed if you're sending attachments or HTML).
		helper.setFrom("nandyr183@gmail.com");
		helper.setTo(to);
		helper.setSubject(subject);
		String htmlMsg = "<p><b>Your Login details for Hotel Management System</b><br><b>Email: </b> " + to
				+ " <br><b>Password: </b> " + password
				+ "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
		message.setContent(htmlMsg, "text/html");
		eMailSender.send(message);

	}
}
