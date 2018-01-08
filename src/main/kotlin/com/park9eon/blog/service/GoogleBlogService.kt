package com.park9eon.blog.service

import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.model.Blog
import com.google.api.services.blogger.model.Post

/**
 * Initial version by: park9eon
 * Initial version created on: 08/01/2018
 */
class GoogleBlogService : GoogleService() {

    companion object {
        val BLOG_VIEW_ADMIN = "ADMIN"
        val BLOG_VIEW_AUTHOR = "AUTHOR"
        val BLOG_VIEW_READER = "READER"
        val BLOG_STATUS_DRAFT = "DRAFT"
        val BLOG_STATUS_LIVE = "LIVE"
        private val BLOG_FIELDS = "id,name,posts/totalItems,selfLink"
        private val POST_FIELDS = "id,author/displayName,content,published,title,url,status,etag,labels"
        private val POST_LIST_FIELDS = "items($POST_FIELDS)"
    }

    val blogger = Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()

    fun findByUrl(url: String, view: String = BLOG_VIEW_ADMIN,
                  fields: String = BLOG_FIELDS): Blog {
        return blogger.blogs()
                .getByUrl(url)
                .setView(view)
                .setFields(fields)
                .execute()
    }

    fun findAllPosts(blog: Blog,
                     view: String = BLOG_VIEW_ADMIN,
                     fields: String = POST_LIST_FIELDS,
                     status: List<String> = listOf(BLOG_STATUS_DRAFT, BLOG_STATUS_LIVE)): List<Post> {
        return blogger.posts()
                .list(blog.id)
                .setView(view)
                .setFields(fields)
                .setMaxResults(blog.posts.totalItems.toLong())
                .setStatus(status)
                .execute()
                .items
    }

    fun findPostById(blog: Blog, postId: String,
                     view: String = BLOG_VIEW_ADMIN,
                     field: String = POST_FIELDS): Post {
        return blogger.posts()
                .get(blog.id, postId)
                .setView(view)
                .setFields(field)
                .execute()
    }

    fun insert(blog: Blog,
               post: Post,
               isDraft: Boolean = false): Post {
        return blogger.posts()
                .insert(blog.id, post)
                .setIsDraft(isDraft)
                .execute()
    }

    fun update(blog: Blog,
               post: Post,
               postId: String = post.id,
               isDraft: Boolean = false): Post {
        return blogger.posts()
                .update(blog.id, postId, post)
                .setPublish(!isDraft)
                .execute()
    }

    fun delete(blog: Blog,
               postId: String) {
        blogger.posts()
                .delete(blog.id, postId)
                .execute()
    }

}