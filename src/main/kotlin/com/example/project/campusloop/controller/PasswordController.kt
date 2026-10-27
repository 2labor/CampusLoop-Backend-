package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.request.ChangePasswordRequest
import com.example.project.campusloop.domain.request.ResetPasswordRequest
import com.example.project.campusloop.domain.request.ForgotPasswordRequest
import com.example.project.campusloop.domain.dto.MessageResponse
import com.example.project.campusloop.service.AccountPasswordService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auth/password")
class PasswordController(
    private val passwordService: AccountPasswordService,
) {
    @PostMapping("/forgot")
    fun forgotPassword(@RequestBody @Valid request: ForgotPasswordRequest): ResponseEntity<MessageResponse> {
        passwordService.forgotPassword(request.email)
        return ResponseEntity.ok(MessageResponse("Password reset code sent successfully"))
    }

    @PostMapping("/reset")
    fun resetPassword(@RequestBody @Valid request: ResetPasswordRequest): ResponseEntity<MessageResponse> {
        passwordService.resetPassword(
            request.email,
            request.code,
            request.newPassword,
            request.confirmPassword
        )
        return ResponseEntity.ok(MessageResponse("Password successfully reset"))
    }

    @PostMapping("/change-password")
    fun changePassword(
        authentication: Authentication,
        @RequestBody @Valid request: ChangePasswordRequest
    ): ResponseEntity<MessageResponse> {
        val userId = UUID.fromString(authentication.name)
        passwordService.changePassword(
            userId,
            request.oldPassword,
            request.newPassword,
            request.confNewPassword
        )

        return ResponseEntity.ok(MessageResponse("Password changed successfully"))
    }

}