package com.jtaylorsoftware.livequiz.api.feedback.repository

import com.jtaylorsoftware.livequiz.api.feedback.model.Feedback
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeedbackRepository : CoroutineCrudRepository<Feedback, Int> {
    fun findByQuizId(quizId: String, pageable: Pageable): Flow<Feedback>

    fun findByQuizIdAndQuestionNumber(quizId: String, questionNumber: Int, pageable: Pageable): Flow<Feedback>

    fun findByQuizIdAndUsername(quizId: String, username: String, pageable: Pageable): Flow<Feedback>

    fun findByQuizIdAndQuestionNumberAndUsername(
        quizId: String,
        questionNumber: Int,
        username: String,
        pageable: Pageable
    ): Flow<Feedback>

    fun findByQuizIdAndDifficultyRatingIs(quizId: String, difficultyRating: Int, pageable: Pageable): Flow<Feedback>

    fun findByQuizIdAndQuestionNumberAndDifficultyRatingIs(
        quizId: String,
        questionNumber: Int,
        difficultyRating: Int,
        pageable: Pageable
    ): Flow<Feedback>

    suspend fun countByQuizId(quizId: String): Int

    suspend fun countByUsername(username: String): Int

    suspend fun countByQuizIdAndDifficultyRatingIs(quizId: String, difficultyRating: Int): Int

    /**
     * Calculates a (numeric) average difficulty rating of all feedback for an entire quiz.
     */
    @Query(
        """
        SELECT  CAST(COALESCE(SUM(feedback.difficulty_rating), 0) AS DECIMAL) / GREATEST(1, COUNT(*))
        FROM    feedback
        WHERE   feedback.quiz_id = :quizId
    """
    )
    suspend fun getAverageDifficultyRating(quizId: String): Float

    /**
     * Calculates a (numeric) average difficulty rating of feedback for a specific question of a quiz.
     */
    @Query(
        """
        SELECT  CAST(COALESCE(SUM(feedback.difficulty_rating), 0) AS DECIMAL) / GREATEST(1, COUNT(*))
        FROM    feedback
        WHERE   feedback.quiz_id = :quizId AND feedback.question_number = :questionNumber
    """
    )
    suspend fun getAverageDifficultyRatingForQuestionNumber(quizId: String, questionNumber: Int): Float

    suspend fun deleteByQuizId(quizId: String): Int

    suspend fun deleteByUsername(username: String): Int

    suspend fun deleteByQuizIdAndQuestionNumber(quizId: String, questionNumber: Int): Int
}