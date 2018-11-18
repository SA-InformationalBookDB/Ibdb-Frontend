package szarch.bme.hu.ibdb.network.models.review

import java.util.*

data class ReviewResponse(
    val id: String,
    val points: Double,
    val date: Date,
    val bookId: String,
    val comment: String?,
    val userNickName: String
)