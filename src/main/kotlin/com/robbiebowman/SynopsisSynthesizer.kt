package com.robbiebowman

import com.robbiebowman.claude.ClaudeClientBuilder
import com.robbiebowman.claude.MessageContent
import com.robbiebowman.claude.Role
import com.robbiebowman.claude.SerializableMessage

internal class SynopsisSynthesizer(claudeApiKey: String) {

    private val claudeClient = ClaudeClientBuilder()
        .withApiKey(claudeApiKey)
        .withModel("claude-3-5-sonnet-20240620")
        .withTool(::writeImaginaryFilmBlurb)
        .withSystemPrompt("""
            You are a movie expert helping the user generate pretend film synopses. The user will provide two film titles:
            a real title of an actual film, followed by that same film with one letter changed. Your job is to write
            a very brief, new synopsis bearing in mind the changed title such that someone reading just the new blurb
            could make a guess as to the new title.
            
            Make sure to include strong hints to the original film so the title is guessable.
            
            For example: "Fight Club -> Night Club"
            A good answer would be: "An insomniac office worker and Tyler Durden discovers an underground society where white-collar
            frustrations are released through nocturnal revelry and anarchic philosophy."
            
            Make sure not to mention the new or original title, as the game will be someone trying to guess it based
            on the new blurb.
            
            Keep the blurbs to 2 or 3 sentences max.
        """.trimIndent())
        .build()

    fun generateSynopsis(candidateTitle: CandidateTitle): Blurb {
        val response = claudeClient.getChatCompletion(
            listOf(
                SerializableMessage(
                    Role.User,
                    listOf(MessageContent.TextContent(
                        """
                            Original title: ${candidateTitle.original},
                            New title: ${candidateTitle.changedTitle}
                        """.trimIndent()
                    ))
                )
            )
        )
        val toolUse = response.content.first { it is MessageContent.ToolUse } as MessageContent.ToolUse
        val clues = claudeClient.derserializeToolUse(toolUse.input["newBlurb"]!!, Blurb::class.java)
        return clues
    }

    private fun writeImaginaryFilmBlurb(newBlurb: Blurb) {
        TODO()
    }

    companion object {
    }
}
