package com.mika.webclientsoap

data class CalculatorRequest(
    val x: Int,
    val y: Int
)

data class CalculatorResponse(
    val value: Int
)