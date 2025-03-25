package com.postvue.feelogserver.app.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.postvue.feelogserver.app.openapis.req.DiscordWebhookRequest;
import com.postvue.feelogserver.app.openapis.service.DiscordService;
import com.postvue.feelogserver.global.constant.LogTemplateConst;
import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender javaMailSender;
	private final DiscordService discordService;

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
			log.error(e.getMessage());

			String errorMsg = LogTemplateConst.getErrorLogTemplate(
				"EMAIL_ERROR", "오류로 인해 " + toEmail +" 이메일에 인증메일이 가지 않았습니다.",
				e.getMessage(),this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
				new Object[] {},
				HttpStatus.INTERNAL_SERVER_ERROR.value());
			DiscordWebhookRequest request = new DiscordWebhookRequest(errorMsg);
			discordService.sendMessageToPostReportChannel(request);
			throw new InternalServerErrorException("서버 오류로 인증 메일이 보내지지 않았습니다.");
		}
	}
}
