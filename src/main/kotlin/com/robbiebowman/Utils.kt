package com.robbiebowman

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.robbiebowman.com.robbiebowman.FilmInfo

object Utils {
    fun getRowsFromFile(path: String): List<String> {
        val resource = this::class.java.classLoader.getResource(path)
        val lines = resource!!.readText().split("\\n".toRegex())
        return lines
    }

    fun getFilmInfoFromCsv(path: String): List<FilmInfo> {
        return csvReader().readAll(
            this::class.java.classLoader.getResource(path).openStream()
        ).map {
            val (title, releaseDate, budget, crew, genre) = it
            val userRating = it.last() // can't do the above with more than 5 assignments :(
            FilmInfo(title, releaseDate, budget, crew, genre, userRating)
        }
    }
}