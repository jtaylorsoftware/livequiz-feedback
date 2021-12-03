package com.jtaylorsoftware.livequiz.api.feedback.service

class ServiceException : RuntimeException {
    constructor(): super()
    constructor(throwable: Throwable) : super(throwable)
}