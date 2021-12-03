package com.jtaylorsoftware.livequiz.api.feedback.repository

import com.jtaylorsoftware.livequiz.api.feedback.model.DifficultyRating
import com.jtaylorsoftware.livequiz.api.feedback.model.Feedback
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import kotlin.random.Random

@DataR2dbcTest
class FeedbackRepositoryTests {
    @Autowired
    lateinit var feedbackRepository: FeedbackRepository

    private val quizId = "TEST_QUIZ"
    private val feedback = mutableListOf<Feedback>()

    @BeforeEach
    fun setup() = runBlocking {
        feedback.clear()
        feedbackRepository.deleteAll()
        (0 until 100).forEach {
            feedback += feedbackRepository.save(
                Feedback(
                    quizId,
                    "username-$it",
                    Random.nextInt(0, 100),
                    // force value averages to always be non-zero for testing (eliminates need for sanity checks)
                    difficultyRating = Random.nextInt(DifficultyRating.EASY.value + 1, DifficultyRating.IMPOSSIBLE.value + 1),
                    "message-$it"
                )
            )
        }
    }

    @Test
    fun `getAverageDifficultyRating should calculate the correct average`() = runBlocking {
        val average = feedbackRepository.getAverageDifficultyRating(quizId)
        val expected = feedback.fold(0f) { acc, feedback ->
            acc + feedback.difficultyRating
        } / feedback.size

        assertThat(average, `is`(expected))
    }

    @Test
    fun `getAverageDifficultyRatingForQuestionNumber should calculate the correct average`() = runBlocking {
        val questionNumber = feedback[Random.nextInt(0, 100)].questionNumber
        val average = feedbackRepository.getAverageDifficultyRatingForQuestionNumber(quizId, questionNumber)
        val expected = feedback.fold(0f) { acc, feedback ->
            acc + if (feedback.questionNumber == questionNumber) feedback.difficultyRating.toFloat() else 0f
        } / feedback.count { it.questionNumber == questionNumber }

        assertThat(average, `is`(expected))
    }
}