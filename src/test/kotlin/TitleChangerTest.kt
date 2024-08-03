import com.robbiebowman.TitleChanger
import com.robbiebowman.WordList
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TitleChangerTest {

    @Test
    fun `Should work`() {
        val dictionary = WordList()
        val titleChanger = TitleChanger(dictionary)

        val titles = titleChanger.getCandidateTitles("Harry Potter and the Deathly Hallows: Part 2", 2)

        assertTrue(titles.size > 100)
    }
}