package com.park9eon.blog

import com.park9eon.blog.service.GoogleDriveService
import org.junit.Test
import com.google.api.client.http.FileContent
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission


/**
 * Initial version by: park9eon
 * Initial version created on: 08/01/2018
 */
class GoogleDriveTest {

    private val googleDriveService by lazy { GoogleDriveService() }

    @Test
    fun `get drive info`() {
        val about = googleDriveService.getAbout()
        println(about)
    }

    @Test
    fun `get file list`() {
        val files = googleDriveService.getFileList().files
        files.forEach {
            println("${it.name} / ${it.properties} / ${it.shared} / ${it.mimeType} / ${it.parents}")
        }
    }

    @Test
    fun `white board`() {
        val dir = googleDriveService.getFileList {
            this.q = "name = 'BLOGGER_API'"
        }
        println(dir)
    }

    @Test
    fun `create file`() {

        val dir = googleDriveService.getFileList {
            this.q = "name = 'BLOGGER_API'"
        }
        println(dir)

        val fileMetadata = File()
        fileMetadata.name = "test.jpg"
        fileMetadata.parents = listOf(dir.files.first().id)

        val filePath = java.io.File("posts/images/DSC_0342.JPG")
        val mediaContent = FileContent("image/jpeg", filePath)

        val file = googleDriveService.insert(fileMetadata, mediaContent)
        val userPermission = Permission()
                .setRole("reader")
                .setType("anyone")
                .setAllowFileDiscovery(true)
        googleDriveService.createPermission(file.id, userPermission)
        println(file)
        googleDriveService.delete(file.id)
    }
}