package com.park9eon.blog

import org.junit.Test
import spark.Spark.setPort
import java.util.Arrays
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants.AUTHORIZATION_SERVER_URL
import com.google.api.client.auth.oauth2.ClientParametersAuthentication
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants.TOKEN_SERVER_URL
import com.google.api.client.http.GenericUrl
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequest
import com.google.api.client.json.JsonObjectParser
import java.io.IOException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.util.store.FileDataStoreFactory
import java.io.File
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Key
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerRequestInitializer
import com.park9eon.blog.model.Markdown
import com.park9eon.blog.model.getOne


/**
 * Initial version by: park9eon
 * Initial version created on: 06/01/2018
 */
class GoogleAuthTest {

    @Test
    fun `oauth2 authenticate`() {

        val config = Markdown.load("config.md").yamlData
        val CLIENT_ID = config.getOne("client_id")
        val CLIENT_SECRET = config.getOne("client_secret")
        val DATA_STORE_DIR = File(System.getProperty("user.home"), ".store/blog")
        val DATA_STORE_FACTORY = FileDataStoreFactory(DATA_STORE_DIR)
        val HTTP_TRANSPORT = NetHttpTransport()
        val JSON_FACTORY = JacksonFactory()
        // set up authorization code flow
        val flow = AuthorizationCodeFlow.Builder(BearerToken
                .authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT,
                JSON_FACTORY,
                GenericUrl(TOKEN_SERVER_URL),
                ClientParametersAuthentication(
                        CLIENT_ID, CLIENT_SECRET),
                CLIENT_ID,
                AUTHORIZATION_SERVER_URL).setScopes(Arrays.asList("read, write"))
                .setDataStoreFactory(DATA_STORE_FACTORY).build()
        // authorize
        val receiver = LocalServerReceiver.Builder().setHost("8080").build()
        val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        val requestFactory = HTTP_TRANSPORT.createRequestFactory { request ->
            credential.initialize(request)
            request.parser = JsonObjectParser(JSON_FACTORY)
        }

        Blogger.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory()) {
            println(it)
        }
                .setApplicationName("Test")
                .setHttpRequestInitializer(requestFactory.initializer)
                .build()

    }
}