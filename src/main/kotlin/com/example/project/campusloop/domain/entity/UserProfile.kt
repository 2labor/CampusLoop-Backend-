package com.example.project.campusloop.domain.entity

import com.example.project.campusloop.domain.enum.Interest
import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "user_profiles")
class UserProfile(
    @Id
    @Column(name = "id")
    var id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", nullable = false)
    @MapsId
    val user: User,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(name = "full_name", nullable = false)
    var fullName: String,

    @Column(name = "bio")
    var bio: String? = null,

    @Column(name = "course")
    var course: String? = null,

    @Column(name = "academic_year")
    var academicYear: Int? = null,

    @Column(name = "campus")
    var campus: String? = null,

    @ElementCollection(targetClass = Interest::class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_interests", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "interest", nullable = false)
    var interests: MutableSet<Interest> = mutableSetOf(),

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}