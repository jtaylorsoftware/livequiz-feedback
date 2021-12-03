package com.jtaylorsoftware.livequiz.api.feedback.service

import com.jtaylorsoftware.livequiz.api.feedback.mapping.FeedbackDto
import com.jtaylorsoftware.livequiz.api.feedback.mapping.toModel
import com.jtaylorsoftware.livequiz.api.feedback.model.DifficultyRating
import com.jtaylorsoftware.livequiz.api.feedback.model.Feedback
import com.jtaylorsoftware.livequiz.api.feedback.repository.FeedbackRepository
import com.jtaylorsoftware.livequiz.api.feedback.service.result.ServiceResult
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class FeedbackServiceImpl(private val feedbackRepository: FeedbackRepository) : FeedbackService {
    override fun getForQuiz(quizId: String): ServiceResult<Flow<Feedback>> = ServiceResult.unpaged { pageable ->
        feedbackRepository.findByQuizId(quizId, pageable)
    }

    override fun getForQuiz(quizId: String, username: String): ServiceResult<Flow<Feedback>> =
        ServiceResult.unpaged { pageable ->
            feedbackRepository.findByQuizIdAndUsername(quizId, username, pageable)
        }

    override fun getForQuiz(quizId: String, difficultyRating: DifficultyRating): ServiceResult<Flow<Feedback>> =
        ServiceResult.unpaged { pageable ->
            feedbackRepository.findByQuizIdAndDifficultyRatingIs(quizId, difficultyRating.value, pageable)
        }

    override fun getForQuizQuestion(quizId: String, questionNumber: Int): ServiceResult<Flow<Feedback>> =
        ServiceResult.unpaged { pageable ->
            feedbackRepository.findByQuizIdAndQuestionNumber(quizId, questionNumber, pageable)
        }

    override fun getForQuizQuestion(quizId: String, questionNumber: Int, username: String): ServiceResult<Flow<Feedback>> =
        ServiceResult.unpaged { pageable ->
            feedbackRepository.findByQuizIdAndQuestionNumberAndUsername(quizId, questionNumber, username, pageable)
        }

    override fun getForQuizQuestion(
        quizId: String,
        questionNumber: Int,
        difficultyRating: DifficultyRating
    ): ServiceResult<Flow<Feedback>> = ServiceResult.unpaged { pageable ->
        feedbackRepository.findByQuizIdAndQuestionNumberAndDifficultyRatingIs(
            quizId,
            questionNumber,
            difficultyRating.value,
            pageable
        )
    }

    override suspend fun countFeedbackForQuiz(quizId: String): ServiceResult<Int> = ServiceResult.single {
        feedbackRepository.countByQuizId(quizId)
    }

    override suspend fun countFeedbackForQuiz(quizId: String, difficultyRating: DifficultyRating): ServiceResult<Int> =
        ServiceResult.single {
            feedbackRepository.countByQuizIdAndDifficultyRatingIs(quizId, difficultyRating.value)
        }

    override suspend fun countFeedbackForUser(username: String): ServiceResult<Int> = ServiceResult.single {
        feedbackRepository.countByUsername(username)
    }

    override suspend fun getAverageDifficultyRating(quizId: String): ServiceResult<Float> = ServiceResult.single {
        feedbackRepository.getAverageDifficultyRating(quizId)
    }

    override suspend fun getAverageDifficultyRatingForQuestion(quizId: String, questionNumber: Int): ServiceResult<Float> =
        ServiceResult.single {
            feedbackRepository.getAverageDifficultyRatingForQuestionNumber(quizId, questionNumber)
        }

    override suspend fun save(feedback: FeedbackDto): ServiceResult<Feedback> = ServiceResult.single {
        feedbackRepository.save(feedback.toModel())
    }

    override suspend fun removeAllForQuiz(quizId: String): ServiceResult<Int> = ServiceResult.single {
        feedbackRepository.deleteByQuizId(quizId)
    }

    override suspend fun removeAllByUser(username: String): ServiceResult<Int> = ServiceResult.single {
        feedbackRepository.deleteByUsername(username)
    }

    override suspend fun removeAllForQuizQuestion(quizId: String, questionNumber: Int): ServiceResult<Int> =
        ServiceResult.single {
            feedbackRepository.deleteByQuizIdAndQuestionNumber(quizId, questionNumber)
        }
}