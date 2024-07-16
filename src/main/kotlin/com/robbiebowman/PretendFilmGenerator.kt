package com.robbiebowman.com.robbiebowman

import com.robbiebowman.*
import com.robbiebowman.SemanticAnaliser
import com.robbiebowman.TitleChanger
import com.robbiebowman.WordList

class PretendFilmGenerator(
    claudeApiKey: String,
    openApiKey: String,
    customPrompt: String? = null
) {
    private val dictionary = WordList()
    private val titleChanger = TitleChanger(dictionary)
    private val synopsisSynthesizer = SynopsisSynthesizer(claudeApiKey, customPrompt)
    private val semanticAnaliser = SemanticAnaliser(openApiKey)
    private val titleRater = TitleRater(claudeApiKey)

    fun generatePretendFilm(originalFilmTitle: String? = null): BlurbAndInfo {
        var info: FilmInfo? = if (originalFilmTitle == null) getRandomFilm() else null
        val original = originalFilmTitle ?: info!!.title

        // Generate alternate titles for random film
        var title = original
        var theChosenOne: CandidateTitle? = null
        while (theChosenOne == null) {
            val titles = titleChanger.getCandidateTitles(title).toList()
            val qualifiedNewTitles =
                if (titles.isNotEmpty()) semanticAnaliser.filterOutSimilarStrings(titles) else emptyList()

            if (qualifiedNewTitles.isEmpty()) {
                if (originalFilmTitle != null) {
                    throw Exception("Couldn't find a variation on $originalFilmTitle")
                }
                info = getRandomFilm()
                title = info.title
            } else {
                val ratings = titleRater.rateTitles(qualifiedNewTitles).zip(qualifiedNewTitles)
                val sufficientlyExcellentTitles =
                    ratings.filter { (rating, _) -> rating.rating == Rating.GOOD || rating.rating == Rating.EXCELLENT }
                if (sufficientlyExcellentTitles.isEmpty()) {
                    info = getRandomFilm()
                    title = info.title
                } else {
                    theChosenOne = sufficientlyExcellentTitles.maxBy { it.first.rating.ordinal }.second
                }
            }
        }
        val response = synopsisSynthesizer.generateSynopsis(theChosenOne)
        return BlurbAndInfo(response, info)
    }

    private fun getRandomFilm(): FilmInfo {
        return Utils.getFilmInfoFromCsv("top-1000-with-imdb-info.csv")
            .take(700) // File is organised by popularity, so selecting most popular
            .random()
    }
}