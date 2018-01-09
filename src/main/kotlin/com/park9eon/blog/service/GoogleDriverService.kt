package com.park9eon.blog.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.About
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList


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

    fun getAbout(aboutBlock: (Drive.About.Get.() -> Unit)? = null): About
            = driver.about()
            .get()
            .apply {
                this.fields = DRIVE_ABOUT_FIELDS
                aboutBlock?.invoke(this)
            }
            .execute()

    fun getFileList(fileListBlock: (Drive.Files.List.() -> Unit)? = null): FileList
            = driver.files()
            .list()
            .apply {
                this.fields = DRIVE_FILE_LIST_FIELDS
                fileListBlock?.invoke(this)
            }
            .execute()

    fun getFile(fileId: String,
                fileBlock: (Drive.Files.Get.() -> Unit)? = null): File
            = driver.files()
            .get(fileId)
            .apply {
                this.fields = DRIVE_FILE_FIELDS
                fileBlock?.invoke(this)
            }
            .execute()
}