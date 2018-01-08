---
title: Markdown으로 Blogger게시글 작성하기!
date: 2018-01-08
tags:
    - kotlin
    - markdown
    - blogger
---

Blogger는 Markdown editor를 지원하지 않는다. 그래서 직접 markdown으로 작성한 게시글을
html로 변환하여 블로그에 게시하는 프로그램을 구상하게 되었다.

<!-- more -->

### Markdown을 Html로 변환하는 스크립트를 작성한다.

1. Gradle로 라이브러리 추가

`build.gradle.kts`

```kotlin
dependencies {
    ...
    compile("com.atlassian.commonmark", "commonmark", "0.10.0")
    compile("com.atlassian.commonmark", "commonmark-ext-autolink", "0.10.0")
    compile("com.atlassian.commonmark", "commonmark-ext-yaml-front-matter", "0.10.0")
    ...
}
```

atlassian에서 만든 Markdown parser를 사용할 것이다. 위 라이브러리는 자바어플리케이션 뿐만아니라 Android에서
사용가능하다. 그래서 안드로이드에서 Markdown을 처리한다면 위 라이브러리를 사용해도 무관한 것 같다.

2. DAO(data access object)를 만든다.

`src/main/kotlin/com.park9eon.blog.dao.Markdown`

```kotlin
class Markdown private constructor(val html: String,
                                   val yamlData: YamlData,
                                   val path: String,
                                   val filename: String)
{
    ...
    companion object {
        fun loadFromFile(file: File): Markdown {
            val yamlVisitor = YamlFrontMatterVisitor()
            val extensions = listOf(
                    AutolinkExtension.create(),
                    YamlFrontMatterExtension.create()
            )
            val parser: Parser = Parser.builder()
                    .let {
                        it.extensions(extensions)
                        it.build()
                    }
    
            val document: Node = parser.parse(file.readText())
                    .apply {
                        this.accept(yamlVisitor)
                    }
    
            val renderer: HtmlRenderer = HtmlRenderer.builder()
                    .let {
                        it.extensions(extensions)
                        it.build()
                    }
    
            val html = renderer.render(document)
            return Markdown(html, yamlVisitor.data, file.path, file.name)
        }
        ...
    }
}
```

위처럼 `companion object` 으로 자바에서 **static method**와 같은 객체생성함수를 만든다. 
YamlFrontMatterVisitor은 마크다운 상단에 정의된 Yaml을 읽을 수 있는 클레스로 제목이나 게시일, Labe을
입려가능하다.

### Blogger포스팅 하기!

```kotlin
dependencies {
    ...
    compile("com.atlassian.commonmark", "commonmark", "0.10.0")
    compile("com.atlassian.commonmark", "commonmark-ext-autolink", "0.10.0")
    compile("com.atlassian.commonmark", "commonmark-ext-yaml-front-matter", "0.10.0")
    compile("com.google.apis", "google-api-services-blogger", "v3-rev55-1.23.0")
    compile("com.google.oauth-client", "google-oauth-client", "1.23.0")
    compile("com.google.oauth-client", "google-oauth-client-servlet", "1.23.0")
    compile("com.google.oauth-client", "google-oauth-client-java6", "1.23.0")
    compile("com.google.oauth-client", "google-oauth-client-jetty", "1.23.0")
    ...
}
```

1. `build.gradle.kts`에 구글인증 라이브러리와 Blogger라이브러리를 추가한다.

2. 블로그게시글을 작성 및 관리할 수 있는 서비스를 만든다. 편의상 DAO로 분류했다.

인증방식은 2가지가 있다. 단순 API키를 이용하여 읽기전용 및 작성을 하기위한 OAuth2인증이 있다.

```kotlin
val blogger = Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
        .setApplicationName(APPLICATION_NAME)
        .setBloggerRequestInitializer(BloggerRequestInitializer(apiKey))
        .build()
val blog = blogger.blogs()
        .getByUrl(url)
        .execute()
```

단순 API인증은 위처럼 정말 단순하다. 하지만 OAuth2 인증은 브라우져를 통해 인증하기 때문에 상당히 복잡하다.
**Command line**으로 동작하도록하였지만 AppEngine을 이용하거나 웹서버에서 이용하기 위해선 밑에 공식문서를 참고바란다.

https://developers.google.com/api-client-library/java/google-api-java-client/oauth2

3. 작성을 위한 OAuth2인증하기

```kotlin
 val flow = AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
        HTTP_TRANSPORT,
        JSON_FACTORY,
        GenericUrl(GoogleOAuthConstants.TOKEN_SERVER_URL),
        ClientParametersAuthentication(clientId, clientSecret),
        clientId,
        GoogleOAuthConstants.AUTHORIZATION_SERVER_URL
)
        .setScopes(BLOGGER_SCOPES)
        .setDataStoreFactory(DATA_STORE_FACTORY)
        .build()
// authorize
val receiver = LocalServerReceiver.Builder()
        .setHost("localhost")
        .setPort(8080)
        .build()
val credential = AuthorizationCodeInstalledApp(flow, receiver)
        .authorize("user")
val requestFactory = HTTP_TRANSPORT.createRequestFactory { request ->
    credential.initialize(request)
    request.parser = JsonObjectParser(JSON_FACTORY)
}
// callback url is http://localhost:8080/Callback
val blogger = ...
        .setHttpRequestInitializer(requestFactory.initializer)
        .build()
```

블로그 생성과 Action 생성은 같지만 `.setHttpRequestInitializer(requestFactory.initializer)` 이부분이 중요하다.
위와같은 HTTP_TRANSPORT와 JSON_FACTORY를 설정하고 **client_id**와 **client_secret**을 입력한다.
후 구글인증라이브러리에서 제공하는 로컬인증클라이언트를 설정한다. 위와같이 설정하면 콜백주소가 `http://localhost:8080/Callback`으로 지정
되어 구글콘솔에서 위 주소로 설정하면 된다.

**oauth2 scopes**같은 경우 Blogger라이브러리에서 제공하는 `BloggerScopes.BLOGGER`를 리스트에 넣어서 사용하면 된다.

위 처럼 기본적인 설정이 끝나고 실행시 브라우져에 구글 로그인창이 나온다.

### 블로그 게시글 가져오기.

Blogger라이브러리는 기본적으로 Builder형식으로 옵션들을 정의 후 `.execute()` 함수로 결과를 실행한다.

```kotlin
fun Blog.findAllPosts(): PostList = blogger.posts()
        .list(blog.id)
        .setMaxResults(blog.posts.totalItems.toLong() * 2)
        .setStatus(listOf("draft", "live"))
        .execute()
```

이 부분은 블로그에 있는 모든 게시글을 가져오는 설정인다. `setStatus()` 부분을 지정해줘야 임시저장파일도 불러오며 *2를 한 이유는 `totalItems`에
임시저장은 포함되어 있지 않기 때문이다. 동작을 아직은 Thread에 안전하지 않기 때문에 편법을 사용해서 가져온다.
위처럼 Action을 정의해서 사용하는 특징을 가진다.

### 블로그 포스팅하기

```Unit 테스트 코드```
```kotlin
@Test
fun `write blog draft post`() {
    authenticatedBlog(clientId, clientSecret, blogUrl) {
        val post = post {
            title = "Hello, World!"
            content = "Hello, World!"
            status = "draft"
        }.insert()
        println(post.title)
    }
}
```

Kotlin으로 Builder와 같은 구조로 만들기로 결심했다. 썩 좋은 구조는 아닌 것 같지만 시도가 중요하다.

```kotlin
fun Blog.post(post: Post.() -> Unit): Post = Post().apply {
    this.blog = Post.Blog()
            .apply {
                this.id = this@post.blog.id
            }
    post(this)
}

fun Blog.post(markdown: Markdown): Post = post {
    val meta = markdown.yamlData
    this.title = meta.title
    this.published = DateTime(Blog.DATE_FORMAT.parse(meta.date))
    this.status = meta.status
    this.content = markdown.html
    this.labels = meta.tags
}
```

게시글을 작성하는 부분은 위와같은 구조로 될 것이다. 다시보면 썩 예쁘진 않다.

### TODO & 샘플

지금은 'Thread not safe'를 고려하지 않고 만들었다. 또한 게시글을 수정하는 부분과 이미지를 첨부하는 부분에서
불편함이 많다<del>구현안됨</del>. 실행방식도 너무 어렵고 번거롭다. 그래서 Gradle plugin기능을 이용한 기능정리와 이미지 업로드기능
LocalDB를 이용한 블로그 게시글관리가 필요하다. 

지금 완성된 코드는 이곳 -> https://github.com/Park9eon/markdown-builder 에서 확인할 수 있다.