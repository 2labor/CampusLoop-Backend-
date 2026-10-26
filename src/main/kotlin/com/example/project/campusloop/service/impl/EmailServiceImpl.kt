package com.example.project.campusloop.service.impl

import com.example.project.campusloop.service.EmailService
import jakarta.mail.internet.MimeMessage
import org.springframework.core.io.ResourceLoader
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    private val resourceLoader: ResourceLoader
) : EmailService {
    override fun sendVerificationEmail(toEmail: String, code: String) {
        try {
            val mimeMessage: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name())

            val htmlContent = loadTemplateEmail(code)

            helper.setTo(toEmail)
            helper.setSubject("CampusLoop - Account Verification Code")
            helper.setText(htmlContent, true)

            mailSender.send(mimeMessage)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to send verification email to $toEmail", e)
        }
    }

    private fun loadTemplateEmail(code: String): String {
        val resource = resourceLoader.getResource("classpath:templates/email/verification-template.html")
        val templateText = resource.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }

        return templateText.replace("{{code}}", code)
    }
}