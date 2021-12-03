package com.jtaylorsoftware.livequiz.api.feedback.service.result

import com.jtaylorsoftware.livequiz.api.feedback.service.ServiceException
import org.springframework.data.domain.Pageable

/**
 * `Result` wrapper for service operations. Translates exceptions thrown while
 * computing a wrapped result into `ServiceException` instances. `Result` can be
 * either single or paged.
 */
sealed class ServiceResult<T> {
    /**
     * Lazily computed `Result` wrapper around service method. Computes the result of calling the service
     * method on first access.
     */
    abstract val result: Result<T>

    companion object {
        /**
         * Creates a `ServiceResult` that contains a result that yields a single, unpaged value that may not
         * become paged.
         * @param getResult Function that produces a value from a service.
         * @return A `ServiceResult` with a single value.
         */
        suspend fun <T> single(getResult: suspend () -> T): ServiceResult<T> {
            val result = try {
                Result.success(getResult())
            } catch (e: Exception) {
                Result.failure(ServiceException(e))
            }
            return SingleServiceResult(result)
        }

        /**
         * Creates a `ServiceResult` that contains a result that yields an unpaged value that may be "upgraded" to
         * become paged.
         * @param getResult Function that produces a pageable value from a service.
         * @return A `ServiceResult` for a value that can be paged, but is not initially.
         */
        fun <T> unpaged(getResult: (Pageable) -> T) = UnpagedServiceResult(getResult)

        /**
         * Creates a `ServiceResult` that contains a result that yields a page that has `size` values. Initially
         * yields values from the first page.
         * @param size Number of values to yield per page.
         * @param getResult Function that produces a pageable value from a service.
         * @return A `ServiceResult` for a result that is pageable.
         */
        fun <T> withSize(size: Int, getResult: (Pageable) -> T) = PagedServiceResult(size, page = 0, getResult)
    }
}

/**
 * A `ServiceResult` that contains a result that yields a single, unpaged value. It cannot be "upgraded" to yield pages.
 */
class SingleServiceResult<T> internal constructor(override val result: Result<T>) : ServiceResult<T>()

/**
 * A `ServiceResult` that contains a result that initially just yields all values. It may be "upgraded" to yield
 * only an amount of values from a single page.
 */
class UnpagedServiceResult<T> internal constructor(private val getResult: (Pageable) -> T) : ServiceResult<T>() {
    private var _result: Result<T>? = null

    override val result: Result<T>
        get() {
            return if (_result != null) {
                _result!!
            } else {
                _result = try {
                    Result.success(getResult(Pageable.unpaged()))
                } catch (e: Exception) {
                    Result.failure(ServiceException(e))
                }
                _result!!
            }
        }

    /**
     * "Upgrades" this `UnpagedServiceResult` to a result with a page of a given size.
     * @param size Number of values to yield per page.
     * @return A `ServiceResult` for a result that is pageable.
     */
    fun withSize(size: Int) = PagedServiceResult(size, page = 0, getResult)
}

/**
 * A `ServiceResult` that contains a result that yields only a number of values from a single page. Initially
 * it uses the first page (page 0).
 */
class PagedServiceResult<T> internal constructor(size: Int = 1, page: Int = 0, private val getResult: (Pageable) -> T) :
    ServiceResult<T>() {

    /**
     * Number of elements in the page.
     */
    var size: Int = size
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("Size must be greater than 0 (given: $size)")
            }
            field = value
        }

    /**
     * The page to return as the result.
     */
    var page: Int = page
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("Page must be greater than 0 (given: $page)")
            }
            field = value
        }

    private var _result: Result<T>? = null

    override val result: Result<T>
        get() {
            return if (_result != null) {
                _result!!
            } else {
                _result = try {
                    Result.success(getResult(Pageable.ofSize(size).withPage(page)))
                } catch (e: Exception) {
                    Result.failure(ServiceException(e))
                }
                _result!!
            }
        }

    /**
     * Creates a new `PagedServiceResult` with a different page, reusing the current `size` value.
     * @param page The new page to return from result.
     * @return New `PagedServiceResult` set to current `size` and given `page`.
     */
    fun withPage(page: Int): PagedServiceResult<T> = PagedServiceResult(size, page, getResult)
}
