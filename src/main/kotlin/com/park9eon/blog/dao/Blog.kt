package com.park9eon.blog.dao

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.ClientParametersAuthentication
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonObjectParser
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerRequestInitializer
import com.google.api.services.blogger.BloggerScopes
import com.google.api.services.blogger.model.PostList
import java.io.File


/**
 * Initial version by: park9eon
 * Initial version created on: 07/01/2018
 */
class Blog private constructor(val blogger: Blogger, val blog: com.google.api.services.blogger.model.Blog) {

    companion object {

        private val APPLICATION_NAME = "BLOG"
        private val BLOGGER_SCOPES = listOf(BloggerScopes.BLOGGER)
        private val DATA_STORE_DIR = File(System.getProperty("user.home"), ".store/blogger")
        private val DATA_STORE_FACTORY = FileDataStoreFactory(DATA_STORE_DIR)
        private val HTTP_TRANSPORT = NetHttpTransport()
        private val JSON_FACTORY = JacksonFactory()

        private fun builder(): Blogger.Builder {
            return Blogger.Builder(
                    HTTP_TRANSPORT,
                    JSON_FACTORY
            ) {
                println(it)
            }
                    .setApplicationName(APPLICATION_NAME)
        }

        // Thread not safe
        fun load(apiKey: String, url: String, blog: Blog.() -> Unit): Blog {
            val blogger = builder()
                    .setBloggerRequestInitializer(BloggerRequestInitializer(apiKey))
                    .build()
            val blog = blogger.blogs()
                    .getByUrl(url)
                    .execute()
            return Blog(blogger, blog)
                    .apply {
                        blog(this)
                    }
        }

        fun loadWithAuthorization(clientId: String, clientSecret: String, url: String, blog: Blog.() -> Unit): Blog {

            // set up authorization code flow
            val flow = AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    GenericUrl(GoogleOAuthConstants.TOKEN_SERVER_URL),
                    ClientParametersAuthentication(clientId, clientSecret),
                    clientId,
                    GoogleOAuthConstants.AUTHORIZATION_SERVER_URL
            )
                    .setScopes(BLOGGER_SCOPES)
                    .setDataStoreFactory(DATA_STORE_FACTORY)
                    .build()
            // authorize
            val receiver = LocalServerReceiver.Builder()
                    .setHost("localhost")
                    .setPort(8080)
                    .build()
            val credential = AuthorizationCodeInstalledApp(flow, receiver)
                    .authorize("user")
            val requestFactory = HTTP_TRANSPORT.createRequestFactory { request ->
                credential.initialize(request)
                request.parser = JsonObjectParser(JSON_FACTORY)
            }
            // callback url is http://localhost:8080/Callback
            val blogger = builder()
                    .setHttpRequestInitializer(requestFactory.initializer)
                    .build()
            val blog = blogger.blogs()
                    .getByUrl(url)
                    .execute()
            return Blog(blogger, blog)
                    .apply {
                        blog(this)
                    }
        }
    }
}

fun blog(apiKey: String, url: String, blog: Blog.() -> Unit): Blog {
    return Blog.load(apiKey, url, blog)
}

fun authenticatedBlog(clientId: String, clientSecret: String, url: String, blog: Blog.() -> Unit): Blog {
    return Blog.loadWithAuthorization(clientId, clientSecret, url, blog)
}

fun Blog.findAllPosts(): PostList = blogger.posts()
        .list(blog.id)
        .setMaxResults(blog.posts.totalItems.toLong() * 2)
        .setStatus(listOf("draft", "live"))
        .execute()