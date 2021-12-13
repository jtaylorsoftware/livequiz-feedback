package com.jtaylorsoftware.livequiz.api.feedback.service

import com.jtaylorsoftware.livequiz.api.feedback.model.QuizResponse
import com.jtaylorsoftware.livequiz.api.feedback.model.UserWithScore
import com.jtaylorsoftware.livequiz.api.feedback.repository.QuizResponseRepository
import com.jtaylorsoftware.livequiz.api.feedback.service.result.collectToList
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.domain.Pageable
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextUInt

@DataR2dbcTest
class QuizResponseServiceTests @Autowired constructor(
    private val responseRepository: QuizResponseRepository
) {
    private lateinit var responseService: QuizResponseService
    private val responses = mutableListOf<QuizResponse>()
    private val numResponses = 100

    // Value used to get a valid but random example Response
    private lateinit var random: QuizResponse

    /** Returns a random integer that's also a valid index into `responses` */
    private fun randomIndex() = Random.nextInt(0, numResponses)

    @BeforeEach
    fun setup() = runBlocking {
        responseService = QuizResponseServiceImpl(responseRepository)
        responses.clear()
        responseRepository.deleteAll()

        val quizIdPool = (0..3).map { UUID.randomUUID().toString() }
        val usernamePool = (0..20).map { UUID.randomUUID().toString().slice(0 until 24) }

        // Keep track of usernames and quiz questions so users only appear
        // once per quiz question
        val quizQuestionUserResponses = quizIdPool.associateBy({ it }, { mutableSetOf<Pair<Int, String>>() })

        // Generate semi-realistic quiz data
        (0 until 100).forEach { _ ->
            var quizId: String
            var username: String
            var questionNumber: Int
            while (true) {
                quizId = quizIdPool[Random.nextInt(quizIdPool.indices)]
                username = usernamePool[Random.nextInt(usernamePool.indices)]
                questionNumber = Random.nextUInt(0u..30u).toInt()
                val questionUsers = quizQuestionUserResponses[quizId]!!
                if ((questionNumber to username) !in questionUsers) {
                    questionUsers += questionNumber to username
                    break
                }
            }

            responses += responseRepository.save(
                QuizResponse(
                    quizId = quizId,
                    username = username,
                    questionNumber = questionNumber,
                    value = UUID.randomUUID().toString(),
                    score = Random.nextUInt(0u..10u).toInt()
                )
            )
        }
        random = responses[randomIndex()]
    }

    @Test
    fun `getForQuiz should return all Responses for quiz with given id`() = runBlocking {
        val actual = responseService.getForQuiz(random.quizId).collectToList()
        val expected = responses.filter { it.quizId == random.quizId }

        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun `getForQuiz(username) should return all Responses by a given user`() = runBlocking {
        val actual = responseService.getForQuiz(random.quizId, random.username).collectToList()
        val expected = responses.filter {
            it.quizId == random.quizId && it.username == random.username
        }

        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun `getAllForQuestion should return all Responses for a quiz question`() = runBlocking {
        val actual = responseService.getAllForQuestion(random.quizId, random.questionNumber).collectToList()
        val expected = responses.filter {
            it.quizId == random.quizId && it.questionNumber == random.questionNumber
        }

        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun `getForQuestionByUser should return the Response by a user for the given question`() = runBlocking {
        val actual = responseService.getForQuestionByUser(
            random.quizId,
            random.questionNumber,
            random.username
        ).result.getOrNull()!!
        val expected = random

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `countResponsesForQuiz should return correct number of Responses for a quiz`() = runBlocking {
        val actual = responseService.countResponsesForQuiz(random.quizId).result.getOrNull()!!
        val expected = responses.count { it.quizId == random.quizId }

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `countResponsesForQuestion should return correct number of Responses for a question where value matches`() =
        runBlocking {
            val actual =
                responseService.countResponsesForQuestion(random.quizId, random.questionNumber).result.getOrNull()!!
            val expected = responses.count {
                it.quizId == random.quizId && it.questionNumber == random.questionNumber
            }

            assertThat(actual, `is`(expected))
        }

    @Test
    fun `countResponsesForQuestion(withValue) should return correct number of Responses for a question where value matches`() =
        runBlocking {
            val actual = responseService.countResponsesForQuestion(
                random.quizId,
                random.questionNumber,
                random.value
            ).result.getOrNull()!!
            val expected = responses.count {
                it.quizId == random.quizId && it.questionNumber == random.questionNumber && it.value == random.value
            }

            assertThat(actual, `is`(expected))
        }

    @Test
    fun `getHighestUserScores should return the highest total scores for a quiz in order`() = runBlocking {
        val actual = responseService.getHighestUserScores(random.quizId).collectToList()
        val expected =
            responses.filter { it.quizId == random.quizId }.groupBy { it.username }.map { (username, responses) ->
                UserWithScore(username, responses.sumOf { it.score })
            }.sortedWith(compareBy<UserWithScore> { -it.totalScore }.thenBy { it.username })

        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun `getTotalQuizScoreForUser should return a user's total score for a quiz`() = runBlocking {
        val actual = responseService.getTotalQuizScoreForUser(random.quizId, random.username).result.getOrNull()!!
        val expected = responses.filter {
            it.quizId == random.quizId && it.username == random.username
        }.sumOf {
            it.score
        }

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `getAverageScoreForQuestion should return the average score for a question`() = runBlocking {
        val actual =
            responseService.getAverageScoreForQuestion(random.quizId, random.questionNumber).result.getOrNull()!!
        val expected = responses.filter {
            it.quizId == random.quizId && it.questionNumber == random.questionNumber
        }.let { quizResponses ->
            quizResponses.sumOf { it.score }.toFloat() / quizResponses.size
        }

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `getAverageScoreForUser should return a single user's average score for a quiz`() = runBlocking {
        val actual = responseService.getAverageScoreForUser(random.quizId, random.username).result.getOrNull()!!
        val expected = responses.filter {
            it.quizId == random.quizId && it.username == random.username
        }.let { quizResponses ->
            quizResponses.sumOf { it.score }.toFloat() / quizResponses.size
        }

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `removeAllForQuiz should remove all Responses for a quiz with given id and return count removed`() =
        runBlocking {
            val actual = responseService.removeAllForQuiz(random.quizId).result.getOrNull()!!
            val expected = responses.count { it.quizId == random.quizId }
            assertThat(actual, `is`(expected))

            assertThat(responseRepository.countByQuizId(random.quizId), `is`(0))
        }

    @Test
    fun `removeAllByUser should delete all Responses by a user for a quiz and return count removed`() = runBlocking {
        val actual = responseService.removeAllByUser(random.quizId, random.username).result.getOrNull()!!
        val expected = responses.count { it.quizId == random.quizId && it.username == random.username }
        assertThat(actual, `is`(expected))

        val remaining =
            responseRepository.findByQuizIdAndUsername(random.quizId, random.username, Pageable.unpaged()).run {
                val list = mutableListOf<QuizResponse>()
                toList(list)
                list.size
            }
        assertThat(remaining, `is`(0))
    }

    @Test
    fun `removeAllForQuestion should delete all Responses to a question on a particular quiz and return count removed`() =
        runBlocking {
            val actual = responseService.removeAllForQuestion(random.quizId, random.questionNumber).result.getOrNull()!!
            val expected = responses.count { it.quizId == random.quizId && it.questionNumber == random.questionNumber }
            assertThat(actual, `is`(expected))

            val remaining = responseRepository.findByQuizIdAndQuestionNumber(
                random.quizId,
                random.questionNumber,
                Pageable.unpaged()
            ).run {
                val list = mutableListOf<QuizResponse>()
                toList(list)
                list.size
            }
            assertThat(remaining, `is`(0))
        }
}