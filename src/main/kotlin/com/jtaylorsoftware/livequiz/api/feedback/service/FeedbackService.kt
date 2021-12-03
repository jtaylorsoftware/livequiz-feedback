package com.jtaylorsoftware.livequiz.api.feedback.service

import com.jtaylorsoftware.livequiz.api.feedback.mapping.FeedbackDto
import com.jtaylorsoftware.livequiz.api.feedback.model.DifficultyRating
import com.jtaylorsoftware.livequiz.api.feedback.model.Feedback
import com.jtaylorsoftware.livequiz.api.feedback.service.result.ServiceResult
import kotlinx.coroutines.flow.Flow

/**
 * Service that enables persistence and management of persisted `Feedback` objects.
 */
interface FeedbackService {
    /**
     * Returns all `Feedback` for a quiz with the given id.
     * @param quizId Quiz to get `Feedback` for.
     * @return `ServiceResult` for any `Feedback` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getForQuiz(quizId: String): ServiceResult<Flow<Feedback>>

    /**
     * Returns all `Feedback` for a quiz with the given id where a specific user submitted it.
     * @param quizId Quiz to get `Feedback` for.
     * @param username User to find `Feedback` for.
     * @return `ServiceResult` for any `Feedback` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getForQuiz(quizId: String, username: String): ServiceResult<Flow<Feedback>>

    /**
     * Returns all `Feedback` for a quiz with the given id and difficulty rating.
     * @param quizId Quiz to get `Feedback` for.
     * @param difficultyRating Difficulty rating to use as a filter on `Feedback`.
     * @return `ServiceResult` for any `Feedback` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getForQuiz(quizId: String, difficultyRating: DifficultyRating): ServiceResult<Flow<Feedback>>

    /**
     * Returns all `Feedback` for a quiz question.
     * @param quizId Quiz to get `Feedback` for.
     * @param questionNumber Question to filter on.
     * @return `ServiceResult` for any `Feedback` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getForQuizQuestion(quizId: String, questionNumber: Int): ServiceResult<Flow<Feedback>>

    /**
     * Returns all `Feedback` for a quiz with the given id and difficulty rating submitted by a certain user.
     * @param quizId Quiz to get `Feedback` for.
     * @param username Username to filter on.
     * @param questionNumber Question to filter on.
     * @return `ServiceResult` for any `Feedback` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getForQuizQuestion(quizId: String, questionNumber: Int, username: String): ServiceResult<Flow<Feedback>>

    /**
     * Returns all `Feedback` for a quiz with the given id and difficulty rating.
     * @param quizId Quiz to get `Feedback` for.
     * @param questionNumber Question to filter on.
     * @param difficultyRating Difficulty to filter on.
     * @return `ServiceResult` for any `Feedback` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getForQuizQuestion(
        quizId: String,
        questionNumber: Int,
        difficultyRating: DifficultyRating
    ): ServiceResult<Flow<Feedback>>

    /**
     * Gets a count of how many `Feedback` exist for a quiz.
     * @param quizId Quiz to count `Feedback` for.
     * @return `ServiceResult` containing the count of `Feedback`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun countFeedbackForQuiz(quizId: String): ServiceResult<Int>

    /**
     * Gets a count of how many `Feedback` exist for a quiz and also have a specific DifficultyRating.
     * @param quizId Quiz to count `Feedback` for.
     * @return `ServiceResult` containing the count of `Feedback`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun countFeedbackForQuiz(quizId: String, difficultyRating: DifficultyRating): ServiceResult<Int>

    /**
     * Gets a count of how many `Feedback` a user has submitted.
     * @param username User to count `Feedback` for.
     * @return `ServiceResult` containing the count of `Feedback`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun countFeedbackForUser(username: String): ServiceResult<Int>

    /**
     * Gets the average difficulty rating of all `Feedback` submitted for a quiz.
     * @param quizId Quiz to calculate average difficulty for.
     * @return `ServiceResult` containing the average difficulty of `Feedback`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun getAverageDifficultyRating(quizId: String): ServiceResult<Float>

    /**
     * Gets the average difficulty rating of all `Feedback` submitted for a quiz question.
     * @param quizId Quiz to calculate average difficulty for.
     * @param questionNumber Specific question to calculate difficulty for.
     * @return `ServiceResult` containing the average difficulty of `Feedback`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun getAverageDifficultyRatingForQuestion(quizId: String, questionNumber: Int): ServiceResult<Float>

    /**
     * Saves a new `Feedback` using data from the given Dto.
     * @param feedback `FeedbackDto` to persist.
     * @return `ServiceResult` containing the saved `Feedback`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun save(feedback: FeedbackDto): ServiceResult<Feedback>

    /**
     * Removes (permanently deletes) all `Feedback` submitted for a quiz.
     * @param quizId Quiz to remove `Feedback` for.
     * @return `ServiceResult` containing the count of `Feedback` removed. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun removeAllForQuiz(quizId: String): ServiceResult<Int>

    /**
     * Removes (permanently deletes) all `Feedback` submitted by a specific user.
     * @param username User to remove `Feedback` for.
     * @return `ServiceResult` containing the count of `Feedback` removed. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun removeAllByUser(username: String): ServiceResult<Int>

    /**
     * Removes (permanently deletes) all `Feedback` submitted for a quiz question.
     * @param quizId Quiz to remove `Feedback` for.
     * @param questionNumber Question to remove `Feedback` for.
     * @return `ServiceResult` containing the count of `Feedback` removed. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun removeAllForQuizQuestion(quizId: String, questionNumber: Int): ServiceResult<Int>
}