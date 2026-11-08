package com.example.project.campusloop.infra.geo.domain.dto

data class GeoClusterResponse(
    val isClustered: Boolean,
    val centerLat: Double,
    val centerLng: Double,
    val count: Int,
    val users: List<UserGeoMarkerDto>
)
