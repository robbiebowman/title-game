package com.robbiebowman.com.robbiebowman

import com.robbiebowman.CandidateTitle
import com.robbiebowman.TitleVariation
import com.robbiebowman.claude.ClaudeClientBuilder
import com.robbiebowman.claude.MessageContent
import com.robbiebowman.claude.Role
import com.robbiebowman.claude.SerializableMessage

internal class TitleRater(claudeApiKey: String) {

    private val claudeClient = ClaudeClientBuilder()
        .withApiKey(claudeApiKey)
        .withModel("claude-3-5-sonnet-20240620")
        .withTool(::saveTitlesRatings)
        .withSystemPrompt("""
            You are a film expert helping the user rate a bunch of altered movie titles based on how good the new titles
            are. They are going to use it for a party game where they use that new title to describe a new plot and have
            their party guess the name of the new film. The title should be rated mainly based on if it forms a funny or
            interesting new title.
            
            Here are some examples and what I would rate them as:
            Don't Look Up -> Don't Hook Up = EXCELLENT (it's funny and it makes sense)
            The Secret Life of Pets -> The Secret Life of Pts = BAD (Pts is a weird word and the title is nonsensical)
            Dune: Part Two -> Dune: Port Two = FINE (Makes sense but doesn't make a fun new film)
            Apocalypse Now -> Apocalypse Snow = GOOD (Sounds like a film and makes sense. Isn't hilarious but that's fine)
        """.trimIndent())
        .build()

    fun rateTitles(titles: List<TitleVariation>): List<TitleRating> {
        val response = claudeClient.getChatCompletion(
            listOf(
                SerializableMessage(
                    Role.User,
                    listOf(
                        MessageContent.TextContent(
                        """
                            Can you rate these title transformations?
                            ${titles.joinToString("\\n") { "Original: '${it.originalTitle}', new title: '${it.newTitle}'. " }}
                        """.trimIndent()
                    ))
                )
            )
        )
        val toolUse = response.content.first { it is MessageContent.ToolUse } as MessageContent.ToolUse
        val input = claudeClient.derserializeToolUse(toolUse.input["input"]!!, TitleRatingList::class.java)
        return input.titleRatings
    }

    private fun saveTitlesRatings(input: TitleRatingList) {
        //
    }
}