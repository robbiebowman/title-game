package com.robbiebowman

import com.robbiebowman.com.robbiebowman.PretendFilmGenerator

fun main() {
    val generator = PretendFilmGenerator(
        claudeApiKey = System.getenv("CLAUDE_API_KEY"), // Used for blurb generation
        openApiKey = System.getenv("OPEN_AI_KEY") // Used for embeddings of new vs old title
    )

    val film = generator.generatePretendFilm()

    println(film.blurb.blurb)
    println(film.blurb.newTitle)
    println(film.info?.title)
    println(film.info?.releaseDate)
}
