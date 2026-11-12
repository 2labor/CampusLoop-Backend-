package com.example.project.campusloop.controller

import com.example.project.campusloop.security.domain.request.CreateTokenRequest
import com.example.project.campusloop.security.domain.dto.ActionTokenResponse
import com.example.project.campusloop.security.domain.mapper.TokenMapper
import com.example.project.campusloop.security.domain.request.ConsumeTokenRequest
import com.example.project.campusloop.security.token.service.impl.ShareableTokenFacadeImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/tokens")
class ShareableTokenController(
    private val facade: ShareableTokenFacadeImpl,
    private val mapper: TokenMapper
) {

    @PostMapping("/generate")
    fun createToken(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: CreateTokenRequest
    ): ResponseEntity<ActionTokenResponse> {
        val token = facade.createToken(
            operatorId = userId,
            type = request.type,
            targetEntityId = request.targetEntityId,
            durationInMinutes = request.durationInMinutes,
            maxUses = request.maxUses
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(token))
    }

    @PostMapping("/consume")
    fun consumeToken(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: ConsumeTokenRequest
    ): ResponseEntity<Void?> {
        facade.consumeAndProcess(
            currentUserId = userId,
            tokenValue = request.token,
            type = request.type
        )

        return ResponseEntity.noContent().build()
    }
}