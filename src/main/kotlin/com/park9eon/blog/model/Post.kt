package com.park9eon.blog.model

import java.util.*

/**
 * Initial version by: park9eon
 * Initial version created on: 01/01/2018
 */
data class Post(
        var title: String,
        var content: String,
        var tags: List<String>,
        var createDate: Date
)