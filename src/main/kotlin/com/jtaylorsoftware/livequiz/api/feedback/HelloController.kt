package com.jtaylorsoftware.livequiz.api.feedback

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/feedback")
class HelloController {
    @GetMapping
    fun helloMessage() = ResponseEntity.ok(mapOf("Hello" to "World"))
}