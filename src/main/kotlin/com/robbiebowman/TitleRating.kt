package com.robbiebowman.com.robbiebowman

data class TitleRatingList(val titleRatings: List<TitleRating>)

data class TitleRating(
    val originalTitle: String,
    val newTitle: String,
    val rating: Rating
)

enum class Rating {
    BAD, FINE, GOOD, EXCELLENT;
}