package com.millionseleshi

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.millionseleshi")
		.start()
}

