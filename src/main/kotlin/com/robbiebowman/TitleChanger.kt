package com.robbiebowman


internal class TitleChanger(private val dictionary: WordList) {

    fun getCandidateTitles(title: String, distance: Int = 2): Set<TitleVariation> {
        val words = selectWordsToChange(title)
        val newTitles = bfsTitle(TitleVariation(title, words, emptyList()), 0, distance)
        return newTitles.toSet()
    }

    private fun bfsTitle(node: TitleVariation, depth: Int, maxDistance: Int): List<TitleVariation> {
        if (depth >= node.originalWords.size) {
            return emptyList()
        }
        val variations = getVariations(node.originalWords[depth], maxDistance - node.distance)
        val (finished, unfinished) = variations.map { (variation, distance) ->
            node.copy(
                variations = node.variations +
                        Variation(
                            originalWord = node.originalWords[node.variations.size],
                            newWord = variation,
                            distance = distance
                        )
            )
        }.partition { it.distance >= maxDistance }

        return finished + unfinished.flatMap { bfsTitle(it, depth + 1, maxDistance) }
    }

    private fun selectWordsToChange(title: String): List<String> {
        val words = title.split(' ', ',', '-', '.', ';', ':').map { it.trim { !it.isLetter() } }
        return words.filter {
            !dictionary.ignoredWords.contains(it.lowercase())
                    && it.length > 3
        }
    }

    private fun getVariations(word: String, maxDistance: Int): List<Pair<String, Int>> {
        return dictionary.words.mapNotNull {
            val distance = levenshteinDistance(word.lowercase(), it.lowercase())
            val inDistanceRange = distance <= maxDistance
            if (inDistanceRange) it to distance else null
        }
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