package com.park9eon.blog.service

import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.model.Blog
import com.google.api.services.blogger.model.Post
import com.google.api.services.blogger.model.PostList

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

    fun getByUrl(url: String,
                 blogBlock: (Blogger.Blogs.GetByUrl.() -> Unit)? = null): Blog
            = blogger.blogs()
            .getByUrl(url)
            .apply {
                this.view = BLOG_VIEW_ADMIN
                this.fields = BLOG_FIELDS
                blogBlock?.invoke(this)
            }
            .execute()

    fun getPostList(blogId: String,
                    postListBlock: (Blogger.Posts.List.() -> Unit)? = null): PostList
            = blogger.posts()
            .list(blogId)
            .apply {
                this.view = BLOG_VIEW_ADMIN
                this.fields = POST_LIST_FIELDS
                this.status = listOf(BLOG_STATUS_DRAFT, BLOG_STATUS_LIVE)
                postListBlock?.invoke(this)
            }
            .execute()

    fun getPost(blogId: String,
                postId: String,
                postBlock: (Blogger.Posts.Get.() -> Unit)? = null): Post
            = blogger.posts()
            .get(blogId, postId)
            .apply {
                this.view = BLOG_VIEW_ADMIN
                this.fields = POST_FIELDS
                postBlock?.invoke(this)
            }
            .execute()

    fun insert(blogId: String,
               post: Post,
               postBlock: (Blogger.Posts.Insert.() -> Unit)? = null): Post
            = blogger.posts()
            .insert(blogId, post)
            .apply {
                postBlock?.invoke(this)
            }
            .execute()

    fun update(blogId: String,
               postId: String,
               post: Post,
               postBlock: (Blogger.Posts.Update.() -> Unit)? = null): Post
            = blogger.posts()
            .update(blogId, postId, post)
            .apply {
                postBlock?.invoke(this)
            }
            .execute()

    fun delete(blogId: String,
               postId: String,
               postBlock: (Blogger.Posts.Delete.() -> Unit)? = null) {
        blogger.posts()
                .delete(blogId, postId)
                .apply {
                    postBlock?.invoke(this)
                }
                .execute()
    }

}