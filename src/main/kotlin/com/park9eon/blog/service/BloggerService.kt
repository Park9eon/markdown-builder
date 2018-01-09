package com.park9eon.blog.service

import com.google.api.client.http.FileContent
import com.google.api.client.util.DateTime
import com.google.api.services.blogger.model.Post
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import com.park9eon.blog.model.*
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Image
import java.nio.file.Files
import java.text.SimpleDateFormat

/**
 * Initial version by: park9eon
 * Initial version created on: 09/01/2018
 */
class BloggerService constructor(private val blogId: String,
                                 private val postPath: java.io.File,
                                 private val photosDirectionId: String,
                                 private val googleBlogService: GoogleBlogService = GoogleBlogService(),
                                 private val googleDriveService: GoogleDriveService = GoogleDriveService()) {

    companion object {
        fun create(blogUrl: String,
                   postPath: String,
                   photosDirectionName: String,
                   googleBlogService: GoogleBlogService = GoogleBlogService(),
                   googleDriveService: GoogleDriveService = GoogleDriveService()): BloggerService {
            val blog = googleBlogService.getByUrl(blogUrl)
            val postPathFile = java.io.File(postPath)
            if (!postPathFile.exists()) {
                postPathFile.mkdir()
            }
            val photosDir = googleDriveService.getFileList {
                this.q = "name = '$photosDirectionName'"
                this.pageSize = 1
            }.files.first()
            return BloggerService(blog.id,
                    postPathFile,
                    photosDir.id,
                    googleBlogService,
                    googleDriveService)
        }
    }

    private val autoImageUploadVisitor = AutoImageUploadVisitor()
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val markdownService = MarkdownService()
            .apply {
                addVisitor(autoImageUploadVisitor)
            }

    fun uploadPost(markdownFilename: String): Post {
        val markdown: Markdown = markdownService.loadFromFile(java.io.File(postPath, markdownFilename))
        val post = createPostByMarkdown(markdown)
        return googleBlogService.insert(blogId, post) {
            this.isDraft = post.status == GoogleBlogService.BLOG_STATUS_DRAFT
        }
    }

    fun updatePost() {
        TODO("로컬 디비를 사용하던 아직 안됨!")
    }

    fun deletePost(postId: String) {
        googleBlogService.delete(blogId, postId)
    }

    fun uploadImage(childPath: String): File {
        val image = java.io.File(postPath, childPath)
        val driveFile = getOneImageByName(image.name)
        return driveFile ?: if (image.exists()) {
            val mimeType = Files.probeContentType(image.toPath())
            val fileMetadata = File()

            fileMetadata.name = image.name
            fileMetadata.parents = listOf(photosDirectionId)

            val mediaContent = FileContent(mimeType, image)
            googleDriveService.insert(fileMetadata, mediaContent).apply {
                googleDriveService.createPermission(this.id,
                        Permission()
                                .setType("anyone")
                                .setRole("reader")
                                .setAllowFileDiscovery(true))
            }
        } else {
            File().apply { webContentLink = childPath }
        }
    }

    private fun createPostByMarkdown(markdown: Markdown): Post = Post().apply {
        val data = markdown.yamlData
        this.title = data.title
        this.labels = data.tags
        this.status = data.status
        this.published = DateTime(simpleDateFormat.parse(data.date))
        this.content = markdown.html
    }

    private fun getOneImageByName(name: String): File? {
        return googleDriveService.getFileList {
            this.q = "name = '$name' and '$photosDirectionId' in parents"
            this.pageSize = 1
        }.files.firstOrNull()
    }

    private inner class AutoImageUploadVisitor : AbstractVisitor() {
        override fun visit(image: Image?) {
            if (image?.destination != null) {
                image.destination = image.destination
                        ?.let {
                            uploadImage(it)
                        }
                        ?.webContentLink
            }
            super.visit(image)
        }
    }
}