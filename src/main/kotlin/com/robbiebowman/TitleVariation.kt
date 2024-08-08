package com.robbiebowman

internal data class TitleVariation(
    val originalTitle: String,
    val originalWords: List<String>,
    val variations: List<Variation>,
) {
    val distance: Int = variations.sumOf { it.distance }
    val newTitle: String = variations.fold(originalTitle) { title, variation ->
        title.replace(variation.originalWord, variation.newWord.capitalize())
    }
}