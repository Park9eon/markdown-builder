import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "blog-writer"
version = "0.0.1"

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.10"

    repositories {
        mavenCentral()
    }
    
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }
    
}

apply {
    plugin("java")
    plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    compile("com.atlassian.commonmark", "commonmark", "0.10.0")
    compile("com.atlassian.commonmark", "commonmark-ext-autolink", "0.10.0")
    compile("com.atlassian.commonmark", "commonmark-ext-yaml-front-matter", "0.10.0")
    compile("com.google.apis", "google-api-services-blogger", "v3-rev55-1.23.0")
    compile("com.google.oauth-client", "google-oauth-client", "1.23.0")
    compile("com.google.oauth-client", "google-oauth-client-servlet", "1.23.0")
    compile("com.google.oauth-client", "google-oauth-client-java6", "1.23.0")
    compile("com.google.oauth-client", "google-oauth-client-jetty", "1.23.0")
    compile("com.sparkjava", "spark-core", "2.7.1")
    compile("com.sparkjava", "spark-kotlin", "1.0.0-alpha")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

