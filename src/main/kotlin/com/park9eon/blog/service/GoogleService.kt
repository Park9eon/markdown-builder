package com.park9eon.blog.service

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.blogger.BloggerScopes
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.io.File


/**
 * Initial version by: park9eon
 * Initial version created on: 08/01/2018
 */
open class GoogleService {

    companion object {
        val APPLICATION_NAME = "BLOGGER-v1.0.0"
        val HTTP_TRANSPORT = NetHttpTransport()
        val JSON_FACTORY = JacksonFactory()
        private val SECRET_FILE_NAME = "/client_secrets.json"
        private val SCOPES = listOf(BloggerScopes.BLOGGER, *DriveScopes.all()
                .toTypedArray())
        private val DATA_STORE_DIR = File(System.getProperty("user.home"), ".store/google")
        private val DATA_STORE_FACTORY = FileDataStoreFactory(DATA_STORE_DIR)
        private val LOCAL_SERVICE_RECEIVER = LocalServerReceiver.Builder()
                .setPort(8080)
                .build()
        val credential by lazy<Credential> {
            val clientSecretStream = this.javaClass.getResourceAsStream(SECRET_FILE_NAME)
                    .reader()
            val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretStream)
            val flow = GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(DATA_STORE_FACTORY)
                    .setAccessType("offline")
                    .build()
            AuthorizationCodeInstalledApp(flow, LOCAL_SERVICE_RECEIVER)
                    .authorize("user")
        }
    }
}