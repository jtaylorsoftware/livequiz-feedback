package com.jtaylorsoftware.livequiz.api.feedback.service

import com.jtaylorsoftware.livequiz.api.feedback.mapping.QuizResponseDto
import com.jtaylorsoftware.livequiz.api.feedback.mapping.toModel
import com.jtaylorsoftware.livequiz.api.feedback.model.QuizResponse
import com.jtaylorsoftware.livequiz.api.feedback.model.UserWithScore
import com.jtaylorsoftware.livequiz.api.feedback.repository.QuizResponseRepository
import com.jtaylorsoftware.livequiz.api.feedback.service.result.ServiceResult
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class QuizResponseServiceImpl constructor(private val quizResponseRepository: QuizResponseRepository) :
    QuizResponseService {
    override fun getForQuiz(quizId: String): ServiceResult<Flow<QuizResponse>> = ServiceResult.unpaged { pageable ->
        quizResponseRepository.findByQuizId(quizId, pageable)
    }

    override fun getForQuiz(quizId: String, username: String): ServiceResult<Flow<QuizResponse>> =
        ServiceResult.unpaged { pageable ->
            quizResponseRepository.findByQuizIdAndUsername(quizId, username, pageable)
        }

    override fun getAllForQuestion(quizId: String, questionNumber: Int): ServiceResult<Flow<QuizResponse>> =
        ServiceResult.unpaged { pageable ->
            quizResponseRepository.findByQuizIdAndQuestionNumber(quizId, questionNumber, pageable)
        }

    override suspend fun getForQuestionByUser(
        quizId: String,
        questionNumber: Int,
        username: String
    ): ServiceResult<QuizResponse?> = ServiceResult.single {
        quizResponseRepository.findByQuizIdAndQuestionNumberAndUsername(quizId, questionNumber, username)
    }

    override suspend fun countResponsesForQuiz(quizId: String): ServiceResult<Int> = ServiceResult.single {
        quizResponseRepository.countByQuizId(quizId)
    }

    override suspend fun countResponsesForQuestion(quizId: String, questionNumber: Int): ServiceResult<Int> =
        ServiceResult.single {
            quizResponseRepository.countByQuizIdAndQuestionNumber(quizId, questionNumber)
        }

    override suspend fun countResponsesForQuestion(
        quizId: String,
        questionNumber: Int,
        value: String
    ): ServiceResult<Int> = ServiceResult.single {
        quizResponseRepository.countByQuizIdAndQuestionNumberAndValueIs(quizId, questionNumber, value)
    }

    override fun getHighestUserScores(quizId: String): ServiceResult<Flow<UserWithScore>> =
        ServiceResult.unpaged { pageable ->
            quizResponseRepository.getTotalScoresByQuizIdDesc(quizId, pageable)
        }

    override suspend fun getTotalQuizScoreForUser(quizId: String, username: String): ServiceResult<Int> =
        ServiceResult.single {
            quizResponseRepository.getTotalScoreByQuizIdAndUsername(quizId, username)
        }

    override suspend fun getAverageScoreForQuestion(quizId: String, questionNumber: Int): ServiceResult<Float> =
        ServiceResult.single {
            quizResponseRepository.getAverageScoreByQuizIdAndQuestionNumber(quizId, questionNumber)
        }

    override suspend fun getAverageScoreForUser(quizId: String, username: String): ServiceResult<Float> =
        ServiceResult.single {
            quizResponseRepository.getAverageScoreByQuizIdAndUsername(quizId, username)
        }

    override suspend fun save(responseDto: QuizResponseDto): ServiceResult<QuizResponse> = ServiceResult.single {
        quizResponseRepository.save(responseDto.toModel())
    }

    override suspend fun removeAllForQuiz(quizId: String): ServiceResult<Int> = ServiceResult.single {
        quizResponseRepository.deleteByQuizId(quizId)
    }

    override suspend fun removeAllByUser(quizId: String, username: String): ServiceResult<Int> = ServiceResult.single {
        quizResponseRepository.deleteByQuizIdAndUsername(quizId, username)
    }

    override suspend fun removeAllForQuestion(quizId: String, questionNumber: Int): ServiceResult<Int> =
        ServiceResult.single {
            quizResponseRepository.deleteByQuizIdAndQuestionNumber(quizId, questionNumber)
        }

}