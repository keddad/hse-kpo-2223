package org.keddad.models

import kotlinx.serialization.Serializable
@Serializable
data class OrderCreateRequest(val comment: String, val dishes: List<Dish>)