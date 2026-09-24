import com.fasterxml.jackson.core.type.TypeReference
import com.robbiebowman.claude.MessageContent
import com.robbiebowman.claudeMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ClaudeMapperTest {

    @Test
    fun ignoresThinkingBeforeToolUse() {
        val content = claudeMapper().readValue(
            """[
                {"type":"thinking","thinking":"reasoning","signature":"signature"},
                {"type":"tool_use","id":"tool-1","name":"saveTitlesRatings","input":{"input":{"titleRatings":[]}}}
            ]""".trimIndent(),
            object : TypeReference<List<MessageContent?>>() {}
        )

        assertEquals("saveTitlesRatings", content.filterIsInstance<MessageContent.ToolUse>().single().name)
    }
}
