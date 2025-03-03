package com.postvue.feelogserver.app.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String serviceEmail;

	public void sendVerificationEmail(String toEmail, String verificationLink) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(serviceEmail);
			helper.setTo(toEmail);
			helper.setSubject("회원가입 이메일 인증");

			// HTML 내용 작성
			String htmlContent = "<html>" +
				"<body>" +
				"<h1>이메일 인증</h1>" +
				"<p>아래 링크를 클릭하여 이메일 인증을 완료하세요:</p>" +
				"<a href=\"" + verificationLink + "\">이메일 인증하기</a>" +
				"<p>감사합니다.</p>" +
				"</body>" +
				"</html>";

			helper.setText(htmlContent, true); // true로 설정하면 HTML로 렌더링됨

			javaMailSender.send(message);
		}
		catch (MessagingException e){
			throw new InternalServerErrorException("서버 오류로 인증 메일이 보내지지 않았습니다.");
		}
		catch (Exception e){
			throw new InternalServerErrorException("서버 오류로 인증 메일이 보내지지 않았습니다.");
		}
	}
}
