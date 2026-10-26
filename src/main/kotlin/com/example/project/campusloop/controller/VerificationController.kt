package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.request.SendVerificationRequest
import com.example.project.campusloop.domain.request.VerifyAccountRequest
import com.example.project.campusloop.repository.MessageResponse
import com.example.project.campusloop.service.AccountVerificationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth/verification")
class VerificationController(
    private val verificationService: AccountVerificationService
) {

    @PostMapping("/send")
    fun sendVerificationEmail(@RequestBody @Valid request: SendVerificationRequest): ResponseEntity<MessageResponse> {
        verificationService.sendVerificationCode(request.email)
        return ResponseEntity.ok(MessageResponse("Verification code sent successfully"))
    }

    @PostMapping("/verify")
    fun verify(@RequestBody @Valid request: VerifyAccountRequest): ResponseEntity<MessageResponse> {
        val isVerify = verificationService.verify(request.email, request.code)

        if (!isVerify) {
            return ResponseEntity.ok(MessageResponse("Invalid or expired verification code"))
        }

        return ResponseEntity.ok(MessageResponse("Account successfully verified"))
    }

}