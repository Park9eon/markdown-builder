package com.park9eon.blog

import com.park9eon.blog.service.GoogleDriverService
import org.junit.Test


/**
 * Initial version by: park9eon
 * Initial version created on: 08/01/2018
 */
class GoogleDriveTest {

    private val googleDriveService by lazy { GoogleDriverService() }

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
}