package com.park9eon.blog.model


/**
 * Initial version by: park9eon
 * Initial version created on: 06/01/2018
 */

typealias YamlData = Map<String, List<String>?>

fun YamlData.getOne(key: String): String? {
    return this[key]?.first()
}

fun YamlData.getAll(key: String): List<String?>? {
    return this[key]
}