package com.jtaylorsoftware.livequiz.api.feedback.service

import com.jtaylorsoftware.livequiz.api.feedback.model.DifficultyRating
import com.jtaylorsoftware.livequiz.api.feedback.model.Feedback
import com.jtaylorsoftware.livequiz.api.feedback.repository.FeedbackRepository
import com.jtaylorsoftware.livequiz.api.feedback.service.result.ServiceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import kotlin.random.Random

@DataR2dbcTest
class FeedbackServiceTests @Autowired constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    private lateinit var feedbackService: FeedbackService
    private val feedbackList = mutableListOf<Feedback>()
    private val numFeedback = 100

    // Feedback used to obtain random test values s.t. queries will contain at least 1 result
    private lateinit var testFeedback: Feedback

    @BeforeEach
    fun setup() = runBlocking {
        feedbackService = FeedbackServiceImpl(feedbackRepository)
        feedbackList.clear()
        feedbackRepository.deleteAll()
        (0 until numFeedback).forEach {
            feedbackList += feedbackRepository.save(
                Feedback(
                    randomInt().toString(),
                    randomInt().toString(),
                    randomInt(),
                    difficultyRating = randomDifficultyRating(),
                    "$it"
                )
            )
        }
        testFeedback = randomFeedback()
    }

    @Test
    fun `getForQuiz should return feedback with matching quizId`() = runBlocking {
        val actual = feedbackService.getForQuiz(testFeedback.quizId).collectToList()
        val expected = feedbackList.filter { it.quizId == testFeedback.quizId }.map { it }

        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun `getForQuiz(difficultyRating) should only return feedback for quizId that has difficultyRating`() =
        runBlocking {
            val actual =
                feedbackService.getForQuiz(
                    testFeedback.quizId,
                    DifficultyRating.fromValue(testFeedback.difficultyRating)
                ).collectToList()
            val expected = feedbackList.filter {
                it.quizId == testFeedback.quizId && it.difficultyRating == testFeedback.difficultyRating
            }.map { it }

            assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
        }

    @Test
    fun `getForQuiz(username) should only return feedback for username`() = runBlocking {
        val actual = feedbackService.getForQuiz(testFeedback.quizId, testFeedback.username).collectToList()
        val expected =
            feedbackList.filter { it.quizId == testFeedback.quizId && it.username == testFeedback.username }.map { it }

        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun `getForQuizQuestion should only return feedback for one question of quiz`() = runBlocking {
        val actual =
            feedbackService.getForQuizQuestion(testFeedback.quizId, testFeedback.questionNumber).collectToList()
        val expected = feedbackList.filter {
            it.quizId == testFeedback.quizId && it.questionNumber == testFeedback.questionNumber
        }.map { it }

        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun `getForQuizQuestion(username) should only return feedback for username of quiz's question`() = runBlocking {
        val actual =
            feedbackService.getForQuizQuestion(testFeedback.quizId, testFeedback.questionNumber, testFeedback.username)
                .collectToList()
        val expected = feedbackList.filter {
            it.quizId == testFeedback.quizId && it.username == testFeedback.username && it.questionNumber == testFeedback.questionNumber
        }.map { it }

        assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun `getForQuizQuestion(difficultyRating) should only return feedback for quizId and question that has difficultyRating`() =
        runBlocking {
            val actual =
                feedbackService.getForQuizQuestion(
                    testFeedback.quizId,
                    testFeedback.questionNumber,
                    DifficultyRating.fromValue(testFeedback.difficultyRating)
                ).collectToList()
            val expected = feedbackList.filter {
                it.quizId == testFeedback.quizId && it.difficultyRating == testFeedback.difficultyRating && it.questionNumber == testFeedback.questionNumber
            }.map { it }

            assertThat(actual, containsInAnyOrder(*expected.toTypedArray()))
        }

    @Test
    fun `removeAllForQuiz should delete all feedback with given quizId and return count deleted`() = runBlocking {
        val actual = feedbackService.removeAllForQuiz(testFeedback.quizId).result.getOrNull()!!
        val expectedNumRemoved = feedbackList.count { it.quizId == testFeedback.quizId }
        assertThat(actual, `is`(expectedNumRemoved))

        // also check for actual deletion (numRemoved should be different from numWithId)
        val numWithQuizId = feedbackRepository.countByQuizId(testFeedback.quizId)
        assertThat(numWithQuizId, `is`(not(expectedNumRemoved)))
    }

    @Test
    fun `removeAllByUser should delete all feedback with given username and return count deleted`() = runBlocking {
        val actual = feedbackService.removeAllByUser(testFeedback.username).result.getOrNull()!!
        val expectedNumRemoved = feedbackList.count { it.username == testFeedback.username }
        assertThat(actual, `is`(expectedNumRemoved))

        val numByUser = feedbackRepository.countByUsername(testFeedback.username)
        assertThat(numByUser, `is`(not(expectedNumRemoved)))
    }

    private fun randomFeedback() = feedbackList[randomInt()]
    private fun randomInt() = Random.nextInt(0, numFeedback)
    private fun randomDifficultyRating() =
        Random.nextInt(DifficultyRating.EASY.value, DifficultyRating.IMPOSSIBLE.value)

    private suspend fun <T> ServiceResult<Flow<T>>.collectToList(): List<T> {
        val list = mutableListOf<T>()
        this.result.getOrNull()!!.toList(list)
        return list
    }
}