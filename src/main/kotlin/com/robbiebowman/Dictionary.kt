package com.robbiebowman

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.example.com.robbiebowman.Utils.getRowsFromFile

internal class Dictionary {

    val words: Set<String>
    val ignoredWords: Set<String>

    init {
        words = getCOCAWordsFromFile("BNC_COCA_lists.csv")
        ignoredWords = getRowsFromFile("ignore-words.txt").toSet()
    }

    private fun getCOCAWordsFromFile(path: String): Set<String> {
        val minFrequency = 40
        val resource = this::class.java.classLoader.getResource(path)
        val words = csvReader().readAll(resource!!.readText()).drop(1)
            .flatMap { (listName, headword, relatedForms, totalFrequency, blank) ->
                val forms = relatedForms.trim('"').split(',').mapNotNull { str ->
                    val (word, numInParen) = str.trim().split(' ')
                    val num = numInParen.trim('(', ')').toInt()
                    if (num < minFrequency) null else word
                }
                forms
            }.toSet()
        return words
    }

}