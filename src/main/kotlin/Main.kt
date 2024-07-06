package org.example

import com.robbiebowman.Dictionary
import org.example.com.robbiebowman.SemanticAnaliser
import org.example.com.robbiebowman.TitleChanger
import org.example.com.robbiebowman.Utils

fun main() {
    val dictionary = Dictionary()
    val titleChanger = TitleChanger(dictionary)
    var title = ""
    var titles = emptyList<String>()
    while (titles.isEmpty()) {
        title = getRandomFilm()
        titles = titleChanger.getCandidateTitles(title).toList()
        if (titles.isEmpty()) println("No good candidates found for: $title")
    }
    println("Chose: $title. Variations: $titles")
    val semanticAnaliser = SemanticAnaliser(System.getenv("OPEN_AI_KEY"))
    val qualifiedNewTitles = semanticAnaliser.filterOutSimilarStrings(title, titles)
    println(qualifiedNewTitles)
}

fun getRandomFilm(): String {
    return Utils.getRowsFromFile("top-1000-films.txt").random()
}