package com.jtaylorsoftware.livequiz.api.feedback.service

import com.jtaylorsoftware.livequiz.api.feedback.mapping.QuizResponseDto
import com.jtaylorsoftware.livequiz.api.feedback.model.QuizResponse
import com.jtaylorsoftware.livequiz.api.feedback.model.UserWithScore
import com.jtaylorsoftware.livequiz.api.feedback.service.result.ServiceResult
import kotlinx.coroutines.flow.Flow

/**
 * Service that enables persistence and management of `QuizResponse` objects.
 */
interface QuizResponseService {
    /**
     * Returns all `QuizResponse` for a quiz with the given id.
     * @param quizId Quiz to get `QuizResponse` for.
     * @return `ServiceResult` for any `QuizResponse` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getForQuiz(quizId: String): ServiceResult<Flow<QuizResponse>>

    /**
     * Returns all `QuizResponse` by a specific user for a quiz with the given id.
     * @param quizId Quiz to get `QuizResponse` for.
     * @param username User to find `QuizResponse` for.
     * @return `ServiceResult` for any `QuizResponse` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getForQuiz(quizId: String, username: String): ServiceResult<Flow<QuizResponse>>

    /**
     * Returns all `QuizResponse` for a quiz question.
     * @param quizId Quiz to get `QuizResponse` for.
     * @param questionNumber Question number (positive integer index) to filter on.
     * @return `ServiceResult` for any `QuizResponse` found. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getAllForQuestion(quizId: String, questionNumber: Int): ServiceResult<Flow<QuizResponse>>

    /**
     * Returns the `QuizResponse` by a user for a given quiz question.
     * @param quizId Quiz to get `QuizResponse` for.
     * @param questionNumber Question number to search for.
     * @return `ServiceResult` for the `QuizResponse` found. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun getForQuestionByUser(quizId: String, questionNumber: Int, username: String): ServiceResult<QuizResponse?>

    /**
     * Counts the number of `QuizResponse` for a quiz with the given id.
     * @param quizId Quiz to count `QuizResponse` for.
     * @return `ServiceResult`containing the count of `QuizResponse`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun countResponsesForQuiz(quizId: String): ServiceResult<Int>

    /**
     * Counts the number `QuizResponse` for a quiz question.
     * @param quizId Quiz to get `QuizResponse` for.
     * @param questionNumber Question to count number of `QuizResponse` to.
     * @return `ServiceResult` containing the count of `QuizResponse`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun countResponsesForQuestion(quizId: String, questionNumber: Int): ServiceResult<Int>

    /**
     * Counts the number `QuizResponse` for a quiz question where the user responded with a specific value.
     * @param quizId Quiz to get `QuizResponse` for.
     * @param questionNumber Question to count number of `QuizResponse` to.
     * @param value Response value (body) that Responses must have.
     * @return `ServiceResult` containing the count of `QuizResponse`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun countResponsesForQuestion(quizId: String, questionNumber: Int, value: String): ServiceResult<Int>

    /**
     * Calculates and groups all users and their total scores for a quiz, in descending order (highest first).
     * @param quizId Quiz to scores for.
     * @return `ServiceResult` of `UserWithScore` in descending order of `totalScore`. Result will only
     * be failure on internal persistence exceptions.
     */
    fun getHighestUserScores(quizId: String): ServiceResult<Flow<UserWithScore>>

    /**
     * Computes a user's total score for a quiz.
     * @param quizId Quiz to get user's total score for.
     * @param username The name of the user to get the total score of.
     * @return `ServiceResult` containing the user's total score. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun getTotalQuizScoreForUser(quizId: String, username: String): ServiceResult<Int>

    /**
     * Computes the average score of a quiz question.
     * @param quizId Quiz with the desired question number.
     * @param questionNumber Question to get the average for.
     * @return `ServiceResult` containing the average score. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun getAverageScoreForQuestion(quizId: String, questionNumber: Int): ServiceResult<Float>

    /**
     * Calculates a user's average score for a quiz.
     * @param quizId Quiz to get user's average for.
     * @param username The name of the User to calculate an average for.
     * @return `ServiceResult` for any `QuizResponse` found. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun getAverageScoreForUser(quizId: String, username: String): ServiceResult<Float>

    /**
     * Persists a `QuizResponse`.
     * @param responseDto `ResponseDto` to persist as a `QuizResponse`.
     * @return `ServiceResult` containing the saved `QuizResponse`. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun save(responseDto: QuizResponseDto): ServiceResult<QuizResponse>

    /**
     * Removes (permanently deletes) all `QuizResponse` submitted for a quiz.
     * @param quizId Quiz to remove `QuizResponse` for.
     * @return `ServiceResult` containing the count of `QuizResponse` removed. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun removeAllForQuiz(quizId: String): ServiceResult<Int>

    /**
     * Removes all `QuizResponse` submitted by a specific user for a quiz.
     * @param quizId Quiz to remove `QuizResponse` for.
     * @param username Name of the user that will have their responses removed.
     * @return `ServiceResult` containing the count of `QuizResponse` removed. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun removeAllByUser(quizId: String, username: String): ServiceResult<Int>

    /**
     * Removes all `QuizResponse` submitted for a quiz question.
     * @param quizId Quiz to remove `QuizResponse` for.
     * @param questionNumber Question to remove responses for.
     * @return `ServiceResult` containing the count of `QuizResponse` removed. Result will only
     * be failure on internal persistence exceptions.
     */
    suspend fun removeAllForQuestion(quizId: String, questionNumber: Int): ServiceResult<Int>
}