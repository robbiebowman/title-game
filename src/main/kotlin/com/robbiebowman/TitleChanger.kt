package com.robbiebowman

import com.robbiebowman.Dictionary


internal class TitleChanger(private val dictionary: Dictionary) {

    fun getCandidateTitles(title: String): Set<CandidateTitle> {
        val words = selectWordsToChange(title)
        val newWords = words.map { word -> word to getVariations(word) }
        val newTitles =
            newWords.flatMap { (word, variations) ->
                variations.map { variation ->
                    val newWord = variation.capitalize()
                    val newTitle = title.replace(word, newWord)
                    CandidateTitle(
                        original = title,
                        changedTitle = newTitle,
                        originalWord = word,
                        changedWord = newWord
                    )
                }
            }.toSet()
        return newTitles
    }

    private fun selectWordsToChange(title: String): List<String> {
        val words = title.split(' ', ',', '-', '.', ';', ':').map { it.trim { !it.isLetter() } }
        return words.filter { !dictionary.ignoredWords.contains(it.lowercase()) && it.isNotEmpty() }
    }

    private fun getVariations(word: String): List<String> {
        return dictionary.words.filter { levenshteinDistance(word.lowercase(), it.lowercase()) == 1 }
    }

    private fun levenshteinDistance(a: String, b: String): Int {
        val m = a.length
        val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) {
            dp[i][0] = i
        }
        for (j in 0..n) {
            dp[0][j] = j
        }

        for (i in 1..m) {
            for (j in 1..n) {
                if (a[i - 1] == b[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1]
                } else {
                    dp[i][j] = 1 + minOf(
                        dp[i - 1][j],    // Deletion
                        dp[i][j - 1],    // Insertion
                        dp[i - 1][j - 1] // Substitution
                    )
                }
            }
        }

        return dp[m][n]
    }

}