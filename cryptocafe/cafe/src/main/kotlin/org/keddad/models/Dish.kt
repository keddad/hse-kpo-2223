package org.keddad.models

import kotlinx.serialization.Serializable

@Serializable
data class Dish(val id: Int, val amount: Int)