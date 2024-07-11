package com.robbiebowman

import com.theokanning.openai.embedding.EmbeddingRequest
import com.theokanning.openai.service.OpenAiService
import java.time.Duration
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

internal class SemanticAnaliser(openAiKey: String) {

    private val gpt = OpenAiService(openAiKey, Duration.ofSeconds(30))

    fun filterOutSimilarStrings(
        candidates: List<CandidateTitle>,
        maximumSimilarity: Double = 0.5
    ): List<CandidateTitle> {
        val originals = candidates.map { it.originalWord.lowercase() }.distinct()
        val inputs = originals + candidates.map { it.changedWord.lowercase() }
        val response = gpt.createEmbeddings(
            EmbeddingRequest(
                "text-embedding-3-large",
                inputs,
                "title-game"
            )
        )
        val sourceEmbeddings = response.data.take(originals.size).zip(originals).associate { it.second to it.first }
        val candidateEmbeddings = candidates.zip(response.data.drop(originals.size))
        val candidateDiffs = candidateEmbeddings.map { (word, embedding) ->
            word to cosineSimilarity(
                vec1 = sourceEmbeddings[word.originalWord.lowercase()]!!.embedding,
                vec2 = embedding.embedding
            )
        }
        val qualifiers = candidateDiffs.mapNotNull { (word, similarity) ->
            if (similarity > maximumSimilarity) null else word
        }
        return qualifiers
    }

    private fun cosineSimilarity(vec1: List<Double>, vec2: List<Double>): Double {
        val dotProduct = vec1.zip(vec2).sumOf { it.first * it.second }
        val norm1 = sqrt(vec1.sumOf { it * it })
        val norm2 = sqrt(vec2.sumOf { it * it })
        return dotProduct / (norm1 * norm2)
    }

    private fun euclideanDistance(vec1: List<Double>, vec2: List<Double>): Double {
        return sqrt(vec1.zip(vec2).sumOf { (v1, v2) -> (v1 - v2).pow(2) })
    }

    private fun manhattanDistance(vec1: List<Double>, vec2: List<Double>): Double {
        return vec1.zip(vec2).sumOf { (v1, v2) -> abs(v1 - v2) }
    }
}