package com.park9eon.blog.service

import com.google.api.client.http.FileContent
import com.google.api.services.blogger.Blogger
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.About
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.Permission

/**
 * Initial version by: park9eon
 * Initial version created on: 08/01/2018
 */
class GoogleDriveService : GoogleService() {

    companion object {
        private val DRIVE_ABOUT_FIELDS = "user/emailAddress,user/me,user/displayName"
        private val DRIVE_FILE_FIELDS = "id,name,mimeType,description,parents,properties,appProperties,spaces,shared,permissions,webViewLink,webContentLink,thumbnailLink"
        private val DRIVE_FILE_LIST_FIELDS = "nextPageToken,files($DRIVE_FILE_FIELDS)"
        private val DRIVE_FILE_INSERT_FIELDS = "id"
    }

    val drive = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()

    fun getAbout(aboutBlock: (Drive.About.Get.() -> Unit)? = null): About
            = drive.about()
            .get()
            .apply {
                this.fields = DRIVE_ABOUT_FIELDS
                aboutBlock?.invoke(this)
            }
            .execute()

    fun getFileList(fileListBlock: (Drive.Files.List.() -> Unit)? = null): FileList
            = drive.files()
            .list()
            .apply {
                this.fields = DRIVE_FILE_LIST_FIELDS
                fileListBlock?.invoke(this)
            }
            .execute()

    fun getFile(fileId: String,
                fileBlock: (Drive.Files.Get.() -> Unit)? = null): File
            = drive.files()
            .get(fileId)
            .apply {
                this.fields = DRIVE_FILE_FIELDS
                fileBlock?.invoke(this)
            }
            .execute()

    fun insert(fileMetaData: File,
               fileContent: FileContent,
               fileBlock: (Drive.Files.Create.() -> Unit)? = null): File
            = drive.files()
            .create(fileMetaData, fileContent)
            .apply {
                this.fields = DRIVE_FILE_INSERT_FIELDS
                fileBlock?.invoke(this)
            }
            .execute()

    fun delete(fileId: String,
               fileBlock: (Drive.Files.Delete.() -> Unit)? = null) {
        drive.files()
                .delete(fileId)
                .apply {
                    this.fields = DRIVE_FILE_INSERT_FIELDS
                    fileBlock?.invoke(this)
                }
                .execute()
    }

    fun createPermission(fileId: String,
                         permission: Permission,
                         permissionBlock: (Drive.Permissions.Create.() -> Unit)? = null): Permission
            = drive.permissions()
            .create(fileId, permission)
            .apply {
                permissionBlock?.invoke(this)
            }
            .execute()
}