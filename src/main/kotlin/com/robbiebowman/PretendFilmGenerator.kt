package com.robbiebowman.com.robbiebowman

import com.robbiebowman.*
import com.robbiebowman.SemanticAnaliser
import com.robbiebowman.TitleChanger
import com.robbiebowman.WordList

class PretendFilmGenerator(
    claudeApiKey: String,
    openApiKey: String
) {
    private val dictionary = WordList()
    private val titleChanger = TitleChanger(dictionary)
    private val synopsisSynthesizer = SynopsisSynthesizer(claudeApiKey)
    private val semanticAnaliser = SemanticAnaliser(openApiKey)

    fun generatePretendFilm(originalFilmTitle: String? = null): Blurb {
        val original = originalFilmTitle ?: getRandomFilm().title

        // Generate alternate titles for random film
        var title = original
        var theChosenOne: CandidateTitle? = null
        while (theChosenOne == null) {
            val titles = titleChanger.getCandidateTitles(title).toList()
            val qualifiedNewTitles = if (titles.isNotEmpty()) semanticAnaliser.filterOutSimilarStrings(titles) else emptyList()

            if (qualifiedNewTitles.isEmpty()) {
                if (originalFilmTitle != null) {
                    throw Exception("Couldn't find a variation on $originalFilmTitle")
                }
                title = getRandomFilm().title
            } else {
                theChosenOne = qualifiedNewTitles.random()
            }
        }
        val response = synopsisSynthesizer.generateSynopsis(theChosenOne)
        return response
    }

    private fun getRandomFilm(): FilmInfo {
        return Utils.getFilmInfoFromCsv("top-1000-with-imdb-info.csv")
            .take(500) // File is organised by popularity, so selecting top 500 most popular
            .random()
    }
}