package com.jtaylorsoftware.livequiz.api.feedback.service.result

import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import kotlin.math.absoluteValue
import kotlin.random.Random

class ServiceResultTests {
    @Test
    fun `ServiceResult-single result is failure if getResult method throws`() = runBlocking {
        val serviceResult = ServiceResult.single { throw IllegalArgumentException() }
        assertThat(serviceResult.result.isFailure, `is`(true))
    }

    @Test
    fun `ServiceResult-unpaged result is failure if getResult method throws`() = runBlocking {
        val serviceResult = ServiceResult.unpaged { throw IllegalArgumentException() }
        assertThat(serviceResult.result.isFailure, `is`(true))
    }

    @Test
    fun `ServiceResult-unpaged-withSize result uses page with given size and offset 0`() {
        lateinit var capturedPageable: Pageable
        val expectedSize = Random.nextInt().absoluteValue
        val serviceResult = ServiceResult
            .unpaged { pageable -> capturedPageable = pageable }
            .withSize(expectedSize)
        val _r = serviceResult.result.getOrNull() // compute and discard
        assertThat(capturedPageable.pageSize, `is`(expectedSize))
        assertThat(capturedPageable.pageNumber, `is`(0))
    }

    @Test
    fun `ServiceResult-withSize result is failure if getResult method throws`() = runBlocking {
        val serviceResult = ServiceResult.withSize(1) { throw IllegalArgumentException() }
        assertThat(serviceResult.result.isFailure, `is`(true))
    }

    @Test
    fun `ServiceResult-withSize result uses page with given size and offset 0`() {
        lateinit var capturedPageable: Pageable
        val expectedSize = Random.nextInt().absoluteValue
        val serviceResult = ServiceResult
            .withSize(expectedSize) { pageable -> capturedPageable = pageable }

        val _r = serviceResult.result.getOrNull() // compute and discard
        assertThat(capturedPageable.pageSize, `is`(expectedSize))
        assertThat(capturedPageable.pageNumber, `is`(0))
    }

    @Test
    fun `ServiceResult-withSize-withPage result uses page with given size and given offset`() {
        lateinit var capturedPageable: Pageable
        val expectedSize = Random.nextInt().absoluteValue
        val expectedPage = Random.nextInt().absoluteValue
        val serviceResult = ServiceResult
            .withSize(expectedSize) { pageable -> capturedPageable = pageable }
            .withPage(expectedPage)

        val _r =serviceResult.result.getOrNull() // compute and discard
        assertThat(capturedPageable.pageSize, `is`(expectedSize))
        assertThat(capturedPageable.pageNumber, `is`(expectedPage))
    }
}