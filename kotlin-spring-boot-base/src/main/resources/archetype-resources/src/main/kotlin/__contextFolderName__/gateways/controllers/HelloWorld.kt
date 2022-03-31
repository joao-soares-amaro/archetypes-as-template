package ${package}.${contextFolderName}.gateways.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorld {
    @GetMapping("hello")
    fun hello(): String {
        return "Hi! I am on air."
    }
}