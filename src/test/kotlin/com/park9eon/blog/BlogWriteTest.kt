package com.park9eon.blog

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.ClientParametersAuthentication
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonObjectParser
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerRequestInitializer
import com.google.api.services.blogger.model.Post
import com.park9eon.blog.model.Markdown
import com.park9eon.blog.model.YamlData
import com.park9eon.blog.model.getOne
import org.junit.Test
import java.io.File
import java.util.*


/**
 * Initial version by: park9eon
 * Initial version created on: 05/01/2018
 */
class BlogWriteTest {

    fun getApiKey(): String? {
        val data: YamlData = Markdown.load("config.md").yamlData
        return data.getOne("api_key")
    }

    fun getBlogger(): Blogger {
        println(getApiKey())
        return Blogger.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory()) {
            println(it)
        }
                .setApplicationName("Test")
                .setGoogleClientRequestInitializer(BloggerRequestInitializer(getApiKey()))
                .build()
    }

    @Test
    fun `get blog posts`() {
        val blogger = getBlogger()
        val blog = blogger.blogs().getByUrl("https://dev9eon.blogspot.kr/").execute()
        val posts = blogger.posts().list(blog.id)
                .setMaxResults(blog.posts.totalItems.toLong()).execute()
        posts.items.forEach {
            println(it.title)
        }
    }

    @Test
    fun `write blog post`() {

        val config = Markdown.load("config.md").yamlData
        val CLIENT_ID = config.getOne("client_id")
        val CLIENT_SECRET = config.getOne("client_secret")
        val DATA_STORE_DIR = File(System.getProperty("user.home"), ".store/blog")
        val DATA_STORE_FACTORY = FileDataStoreFactory(DATA_STORE_DIR)
        val HTTP_TRANSPORT = NetHttpTransport()
        val JSON_FACTORY = JacksonFactory()
        // set up authorization code flow
        val flow = AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT,
                JSON_FACTORY,
                GenericUrl(GoogleOAuthConstants.TOKEN_SERVER_URL),
                ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
                CLIENT_ID,
                GoogleOAuthConstants.AUTHORIZATION_SERVER_URL)
                .setScopes(Arrays.asList("https://www.googleapis.com/auth/blogger"))
                .setDataStoreFactory(DATA_STORE_FACTORY).build()
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

        val blogger = Blogger.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory()) {
            println(it)
        }
                .setApplicationName("Test")
                .setHttpRequestInitializer(requestFactory.initializer)
                .build()
        val blog = blogger.blogs()
                .getByUrl("https://dev9eon.blogspot.kr/")
                .execute()

        val content = Post()
        content.blog = Post.Blog().apply { id = blog.id }
        content.title = "Hello, World!"
        content.content = "Hello, World!"

        val posts = blogger.posts()
                .insert(blog.id, content)
                .execute()
        println(posts.title)
    }
}