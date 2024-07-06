package org.example.com.robbiebowman

import com.theokanning.openai.embedding.Embedding
import com.theokanning.openai.embedding.EmbeddingRequest
import com.theokanning.openai.service.OpenAiService
import java.time.Duration
import kotlin.math.sqrt

class SemanticAnaliser(openAiKey: String) {

    private val gpt = OpenAiService(openAiKey, Duration.ofSeconds(30))

    fun filterOutSimilarStrings(source: String, candidates: List<String>, maximumSimilarity: Double = 0.75): List<String> {
        val response = gpt.createEmbeddings(
            EmbeddingRequest(
                "text-embedding-3-large",
                listOf(source) + candidates,
                "title-game"
            )
        )
        val sourceEmbedding = response.data.first()
        val candidateEmbeddings = candidates.zip(response.data.drop(1))
        val candidateDiffs = candidateEmbeddings.map { (word, embedding) ->
            word to cosineSimilarity(
                vec1 = sourceEmbedding.embedding.toDoubleArray(),
                vec2 = embedding.embedding.toDoubleArray()
            )
        }
        val qualifiers = candidateDiffs.mapNotNull { (word, similarity) ->
            if (similarity > maximumSimilarity) null else word
        }
        return qualifiers
    }

    private fun cosineSimilarity(vec1: DoubleArray, vec2: DoubleArray): Double {
        var dotProduct = 0.0
        var normVec1 = 0.0
        var normVec2 = 0.0
        for (i in vec1.indices) {
            dotProduct += vec1[i] * vec2[i]
            normVec1 += vec1[i] * vec1[i]
            normVec2 += vec2[i] * vec2[i]
        }
        return dotProduct / (sqrt(normVec1) * sqrt(normVec2))
    }
}