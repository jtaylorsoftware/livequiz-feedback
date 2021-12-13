package com.jtaylorsoftware.livequiz.api.feedback.repository

import com.jtaylorsoftware.livequiz.api.feedback.model.QuizResponse
import com.jtaylorsoftware.livequiz.api.feedback.model.UserWithScore
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface QuizResponseRepository : CoroutineCrudRepository<QuizResponse, Int> {
    fun findByQuizId(quizId: String, pageable: Pageable): Flow<QuizResponse>

    fun findByQuizIdAndUsername(quizId: String, username: String, pageable: Pageable): Flow<QuizResponse>

    suspend fun findByQuizIdAndQuestionNumberAndUsername(
        quizId: String,
        questionNumber: Int,
        username: String
    ): QuizResponse?

    fun findByQuizIdAndQuestionNumber(
        quizId: String,
        questionNumber: Int,
        pageable: Pageable
    ): Flow<QuizResponse>

    suspend fun countByQuizId(quizId: String): Int

    suspend fun countByQuizIdAndQuestionNumber(quizId: String, questionNumber: Int): Int

    suspend fun countByQuizIdAndQuestionNumberAndValueIs(quizId: String, questionNumber: Int, value: String): Int

    /**
     * Returns the highest scoring usernames and their scores for a quiz.
     */
    @Query(
        """
        SELECT      response.username, COALESCE(SUM(response.score), 0) as total_score
        FROM        response
        WHERE       response.quiz_id = :quizId
        GROUP BY    response.username
        ORDER BY    total_score DESC, response.username ASC
        
    """
    )
    fun getTotalScoresByQuizIdDesc(quizId: String, pageable: Pageable): Flow<UserWithScore>

    /**
     * Returns a user's total score for a quiz.
     */
    @Query(
        """
        SELECT  COALESCE(SUM(response.score), 0)
        FROM    response
        WHERE   response.quiz_id = :quizId AND response.username = :username
    """
    )
    suspend fun getTotalScoreByQuizIdAndUsername(quizId: String, username: String): Int

    /**
     * Returns the average score for a quiz question.
     */
    @Query(
        """
        SELECT  CAST(COALESCE(SUM(response.score), 0) AS DECIMAL) / GREATEST(1, COUNT(*))
        FROM    response
        WHERE   response.quiz_id = :quizId AND response.question_number = :questionNumber
    """
    )
    suspend fun getAverageScoreByQuizIdAndQuestionNumber(quizId: String, questionNumber: Int): Float

    /**
     * Returns the user's average score for a quiz.
     */
    @Query(
        """
        SELECT  CAST(COALESCE(SUM(response.score), 0) AS DECIMAL) / GREATEST(1, COUNT(*))
        FROM    response
        WHERE   response.quiz_id = :quizId AND response.username = :username
    """
    )
    suspend fun getAverageScoreByQuizIdAndUsername(quizId: String, username: String): Float

    suspend fun deleteByQuizId(quizId: String): Int

    suspend fun deleteByQuizIdAndUsername(quizId: String, username: String): Int

    suspend fun deleteByQuizIdAndQuestionNumber(quizId: String, questionNumber: Int): Int
}