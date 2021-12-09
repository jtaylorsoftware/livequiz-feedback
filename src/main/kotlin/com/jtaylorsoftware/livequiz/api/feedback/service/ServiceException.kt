package com.jtaylorsoftware.livequiz.api.feedback.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class ServiceException : RuntimeException {
    constructor(): super()
    constructor(throwable: Throwable) : super(throwable)
}