package com.park9eon.blog

import com.park9eon.blog.service.BloggerService
import org.junit.Test


/**
 * Initial version by: park9eon
 * Initial version created on: 09/01/2018
 */
class BloggerTest {

    private val bloggerService by lazy {
        BloggerService.create("https://dev9eon.blogspot.kr",
                "posts",
                "BLOGGER_API")
    }

    @Test
    fun `update test`() {
        bloggerService.uploadPost("2018-01-01-test.md")
    }
}