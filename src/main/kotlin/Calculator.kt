package com.mika.webclientsoap

import reactor.core.publisher.Mono

interface Calculator {
    fun sum(x: Int, y: Int): Mono<Int>
    fun multiply(x: Int, y: Int): Mono<Int>
}