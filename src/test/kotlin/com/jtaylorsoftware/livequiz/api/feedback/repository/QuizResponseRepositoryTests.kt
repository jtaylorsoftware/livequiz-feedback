package com.jtaylorsoftware.livequiz.api.feedback.repository

import com.jtaylorsoftware.livequiz.api.feedback.model.QuizResponse
import com.jtaylorsoftware.livequiz.api.feedback.model.UserWithScore
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.domain.Pageable
import kotlin.random.Random
import kotlin.random.nextInt

@DataR2dbcTest
class QuizResponseRepositoryTests {
    @Autowired
    lateinit var responseRepository: QuizResponseRepository

    private val quizId = "TEST_QUIZ"
    private val responses = mutableListOf<QuizResponse>()

    @BeforeEach
    fun setup() = runBlocking {
        responses.clear()
        responseRepository.deleteAll()
        (0 until 100).forEach {
            responses += responseRepository.save(
                QuizResponse(
                    quizId,
                    "username-$it",
                    Random.nextInt(0, 100),
                    "response-$it",
                    Random.nextInt(1..10)
                )
            )
        }
    }

    @Test
    fun `getTotalScoresByQuizIdDesc should return highest total scores in order`() = runBlocking {
        val actual = run {
            val list = mutableListOf<UserWithScore>()
            responseRepository.getTotalScoresByQuizIdDesc(quizId, Pageable.unpaged()).toList(list)
            list.toList()
        }
        val expected = responses.groupBy { it.username }.map { (username, responses) ->
            UserWithScore(username, responses.sumOf { it.score })
        }.sortedWith(compareBy<UserWithScore> { -it.totalScore }.thenBy { it.username })

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `getTotalScoresByQuizIdDesc with no responses should return empty list`() = runBlocking {
        responseRepository.deleteAll()
        val actual = run {
            val list = mutableListOf<UserWithScore>()
            responseRepository.getTotalScoresByQuizIdDesc(quizId, Pageable.unpaged()).toList(list)
            list.toList()
        }

        assertThat(actual, `is`(empty()))
    }

    @Test
    fun `getTotalScoreByQuizAndUsername should calculate the correct sum`() = runBlocking {
        val username = responses[Random.nextInt(0, 100)].username
        val actual = responseRepository.getTotalScoreByQuizIdAndUsername(quizId, username)
        val expected = responses.sumOf { if (it.username == username) it.score else 0 }

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `getTotalScoreByQuizAndUsername with no matches should return 0`() = runBlocking {
        val username = "invalid-username"
        val actual = responseRepository.getTotalScoreByQuizIdAndUsername(quizId, username)
        val expected = 0

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `getAverageScoreByQuizAndQuestion should calculate the correct average`() = runBlocking {
        val questionNumber = responses[Random.nextInt(0, 100)].questionNumber
        val actual = responseRepository.getAverageScoreByQuizIdAndQuestionNumber(quizId, questionNumber)
        val expected = responses.sumOf {
            if (it.questionNumber == questionNumber) it.score else 0
        }.toFloat() / responses.count { it.questionNumber == questionNumber }

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `getAverageScoreByQuizAndQuestion with no matches should return 0`() = runBlocking {
        val questionNumber = 100_000
        val actual = responseRepository.getAverageScoreByQuizIdAndQuestionNumber(quizId, questionNumber)
        val expected = 0f

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `getAverageScoreByQuizAndUsername should calculate the correct average`() = runBlocking {
        val username = responses[Random.nextInt(0, 100)].username
        val actual = responseRepository.getAverageScoreByQuizIdAndUsername(quizId, username)
        val expected = responses.sumOf {
            if (it.username == username) it.score else 0
        }.toFloat() / responses.count { it.username == username }

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `getAverageScoreByQuizAndUsername with no matches should return 0`() = runBlocking {
        val username = "invalid-username"
        val actual = responseRepository.getAverageScoreByQuizIdAndUsername(quizId, username)
        val expected = 0f

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `countByQuizId with no matching rows should return 0`() = runBlocking {
        val actual = responseRepository.countByQuizId("abc")
        val expected = 0

        assertThat(actual, `is`(expected))
    }
}