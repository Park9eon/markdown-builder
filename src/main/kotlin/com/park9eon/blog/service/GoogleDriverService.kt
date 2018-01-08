package com.park9eon.blog.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.About
import com.google.api.services.drive.model.File


/**
 * Initial version by: park9eon
 * Initial version created on: 08/01/2018
 */
class GoogleDriverService : GoogleService() {

    companion object {
        private val DRIVE_ABOUT_FIELDS = "user/emailAddress,user/me,user/displayName"
        private val DRIVE_FILE_FIELDS = "id,name,mimeType,description,parents,properties,appProperties,spaces,shared,permissions"
        private val DRIVE_FILE_LIST_FIELDS = "nextPageToken,files($DRIVE_FILE_FIELDS)"
    }

    val driver = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()

    fun about(): About {
        return driver.about()
                .get()
                .setFields(DRIVE_ABOUT_FIELDS)
                .execute()
    }

    fun someDirectory(name: String): File {
        return driver.files()
                .list()
                .setFields(DRIVE_FILE_LIST_FIELDS)
                .setQ("name = '${name}' and mimeType = 'application/vnd.google-apps.folder'")
                .execute()
                .files
                .first()
    }

    fun list(): List<File> {
        return driver.files()
                .list()
                .setPageSize(100)
                .setCorpus("user")
                .setSpaces("drive")
                .setQ("fullText contains 'blogger'")
                .setSupportsTeamDrives(false)
                .setFields(DRIVE_FILE_LIST_FIELDS)
                .execute()
                .files
    }

    fun findByFileId(fileId: String,
                     fields: String = DRIVE_FILE_FIELDS): File {
        return driver.files()
                .get(fileId)
                .setFields(fields)
                .execute()
    }
}