package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Account
import com.example.project.campusloop.domain.entity.User
import com.example.project.campusloop.domain.enum.AuthProvider
import com.example.project.campusloop.repository.AccountRepository
import com.example.project.campusloop.service.AccountService
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AccountServiceImpl(
    private val repository: AccountRepository
) : AccountService {

    @Transactional
    override fun createAccount(email: String, login: String, provider: AuthProvider, password: String, user: User): Account {
        if (repository.existsByEmail(email)) {
            throw IllegalArgumentException("Account with email $email already exists.")
        }

        if (repository.existsByLogin(login)) {
            throw IllegalArgumentException("Account with login $login already exists.")
        }

        val account = Account(
            provider = provider,
            email = email,
            login = login,
            passwordHash = password,
            user = user
        )

        return repository.save(account)
    }

    override fun getAccountById(userId: UUID): Account? {
        return repository.findByIdOrNull(userId)
    }

    override fun findByCredentials(credential: String): Account? {
        return repository.findByLogin(credential) ?: repository.findByEmail(credential)
    }

    override fun markAsVerified(accountId: UUID) {
        val account = repository.findByIdOrNull(accountId)
            ?: throw IllegalArgumentException("Account with id $accountId does not exist.")

        account.isVerified = true
        repository.save(account)
    }
}