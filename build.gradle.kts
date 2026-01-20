import java.nio.file.Files
import java.security.MessageDigest

/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

buildscript {
    repositories {
        mavenLocal()
        /*maven(url = "http://192.168.1.12:18081/repository/public/") {
            isAllowInsecureProtocol = true
        }*/
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        maven("https://plugins.gradle.org/m2/")
        maven("https://repo.spring.io/plugins-release/")
    }
}

group = "com.truthbean"

allprojects {
    repositories {
        mavenLocal()
        /*maven(url = "http://192.168.1.12:18081/repository/public/") {
            isAllowInsecureProtocol = true
        }*/
        maven("https://mirrors.huaweicloud.com/repository/maven/")
    }
}

plugins {
    `maven-publish`
    signing

    idea
    eclipse
}

val projectVersion: String by project
// 根据我们在gradle.properties中声明的版本名称,来分辨是release版本还是snapshots版本
val isReleaseBuild = projectVersion.endsWith("RELEASE")

// 声明变量记录maven库地址，判断是发布到正式库,还是snapshots库
val mavenRepositoryUrl =
        if (isReleaseBuild) {
            "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
        } else {
            "https://ossrh-staging-api.central.sonatype.com/content/repositories/snapshots/"
        }

fun calculateHash(file: File, algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm)
    val bytes = Files.readAllBytes(file.toPath())
    val hashBytes = digest.digest(bytes)
    return bytesToHex(hashBytes)
}

fun bytesToHex(bytes: ByteArray): String {
    return bytes.joinToString("") {"%02x".format(it)}
}

subprojects {

    repositories {
        mavenLocal()
        /*maven(url = "http://192.168.1.12:18081/repository/public/") {
            isAllowInsecureProtocol = true
        }*/
        maven("https://mirrors.huaweicloud.com/repository/maven/")
    }

    buildscript {
        repositories {
            mavenLocal()
            /*maven(url = "http://192.168.1.12:18081/repository/public/") {
                isAllowInsecureProtocol = true
            }*/
            maven("https://mirrors.huaweicloud.com/repository/maven/")
            maven("https://plugins.gradle.org/m2/")
        }
    }

    version = projectVersion

    apply(plugin = "java")
    apply(plugin = "java-library")

    apply(plugin = "version-catalog")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    apply(plugin = "idea")
    apply(plugin = "eclipse")

    tasks.withType<Test> {
        // useJUnitPlatform()
    }

    afterEvaluate {
        val originName = project.name.substring(7)

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-Xlint:unchecked")
            options.compilerArgs.add("-Xlint:deprecation")
            // options.compilerArgs.add("-parameters")
            options.isDebug = true
            options.isFork = true
        }

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        plugins.withType<JavaPlugin>().configureEach {
            configure<JavaPluginExtension> {
                modularity.inferModulePath.set(true)
            }
        }

        tasks.withType<Jar> {
            archiveVersion.set(projectVersion)
            manifest.attributes["Implementation-Title"] = project.name
            manifest.attributes["Implementation-Version"] = projectVersion
            manifest.attributes["Created-By"] =
                    "${System.getProperty("java.version")} (${System.getProperty("java.specification.vendor")})"
        }

        val sourcesJar by tasks.registering(Jar::class) {
            group = "jar"
            dependsOn(JavaPlugin.CLASSES_TASK_NAME)
            archiveClassifier.set("sources")
            from(project.the<SourceSetContainer>()["main"].allSource)
        }

        // javadoc
        if (project.name != "debbie-dependencies") {
            tasks.withType<Javadoc> {
                isFailOnError = false
                options {
                    encoding = "UTF-8"
                    charset("UTF-8")
                }

                if (JavaVersion.current().isJava9Compatible) {
                    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
                }
            }

            val javadocJar by tasks.registering(Jar::class) {
                group = "jar"
                dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
                archiveClassifier.set("javadoc")
                from(tasks["javadoc"])
            }

            artifacts {
                add("archives", javadocJar)
                add("archives", sourcesJar)
            }
        }

        tasks.withType<Delete> {
            delete(File("$rootDir/$originName/bin"))
            delete(File("$rootDir/$originName/out"))
            delete(File("$rootDir/$originName/build"))
            delete(File("$rootDir/$originName/target"))
        }

        publishing {
            publications {
                create<MavenPublication>("uploadToMavenRepository") {
                    artifactId = project.name
                    group = "com.truthbean"
                    version = projectVersion
                    from(components["java"])
                    artifact(tasks["sourcesJar"])
                    artifact(tasks["javadocJar"])
                    versionMapping {
                        usage("java-api") {
                            fromResolutionOf("runtimeClasspath")
                        }
                        usage("java-runtime") {
                            fromResolutionResult()
                        }
                    }
                    pom {
                        artifactId = project.name
                        name.set(project.name)
                        group = "com.truthbean"
                        version = projectVersion

                        description.set("a java microservice project")
                        url.set("http://www.truthbean.com/debbie")
                        licenses {
                            license {
                                name.set("Mulan PSL v2")
                                url.set("https://github.com/truthbean/debbie/blob/master/LICENSE")
                            }
                        }
                        developers {
                            developer {
                                id.set("truthbean")
                                name.set("Rogar·Q (TruthBean)")
                                email.set("truthbean@outlook.com")
                            }
                            developer {
                                id.set("qu")
                                name.set("璩诗斌")
                                email.set("truthbean@foxmail.com")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/TruthBean/debbie.git")
                            developerConnection.set("scm:git:ssh://github.com/TruthBean/debbie.git")
                            url.set("https://github.com/TruthBean/debbie")
                        }
                        issueManagement {
                            system.set("github")
                            url.set("https://github.com/truthbean/debbie/issues")
                        }
                    }
                }
            }

            repositories {
                maven {
                    url = uri(mavenRepositoryUrl)
                    credentials {
                        val sonatypeUsername: String? by project
                        username = if (sonatypeUsername == null || sonatypeUsername == "null") "anonymous" else sonatypeUsername

                        val sonatypePassword: String? by project
                        password = if (sonatypePassword == null || sonatypeUsername == "null") "anonymous" else sonatypePassword
                    }
                }
            }

            val collectBuildFileAfteruploadToMavenRepository by tasks.registering {
                group = "truthbean"
                description = "collectBuildFile"

                dependsOn("publishUploadToMavenRepositoryPublicationToMavenLocal")

                doLast {
                    println("do copy task: $originName $version")
                    val distDir = "$rootDir/target/upload/com/truthbean/debbie-$originName/$version"
                    val dist = File(distDir)
                    if (!dist.exists()) {
                        dist.mkdirs()
                    }
                    println(dist.canonicalPath)
                    val files = listOf(".jar", ".jar.asc", ".pom", ".pom.asc", "-javadoc.jar", "-javadoc.jar.asc", "-sources.jar", "-sources.jar.asc")
                    copy {
                        for (f in files) {
                            from(File("$rootDir/$originName/build/libs/debbie-$originName-$version$f"))
                            into(dist)
                        }
                    }
                    File("$rootDir/$originName/build/publications/uploadToMavenRepository/pom-default.xml")
                        .copyTo(File("$dist/debbie-$originName-$version.pom"))
                    File("$rootDir/$originName/build/publications/uploadToMavenRepository/pom-default.xml.asc")
                        .copyTo(File("$dist/debbie-$originName-$version.pom.asc"))
                    for (f in files) {
                        if (!f.endsWith(".asc")) {
                            val file = File("$distDir/debbie-$originName-$version$f")
                            val md5 = calculateHash(file, "MD5")
                            val sha1 = calculateHash(file, "SHA-1")
                            val md5File = File("$distDir/debbie-$originName-$version$f.md5")
                            val sha1File = File("$distDir/debbie-$originName-$version$f.sha1")
                            md5File.writeText(md5)
                            sha1File.writeText(sha1)
                        }
                    }
                }
            }
        }

        // 进行数字签名
        signing {
            if(isReleaseBuild) {
                sign(publishing.publications["uploadToMavenRepository"])
            }
        }

        eclipse {
            classpath {
                isDownloadJavadoc = true
                isDownloadSources = true
            }
        }

        idea {
            module {
                isDownloadJavadoc = true
                isDownloadSources = true
            }
        }

        // collectBuildFile.finalizeBy("zipFiles")
        // val build by tasks.getting
        // build.finalizedBy("collectBuildFile")
    }

    tasks.withType<Delete>() {
        delete(File("$rootDir/target"))
        delete(File("$rootDir/bin"))
        delete(File("$rootDir/out"))
        delete(File("$rootDir/build"))
        delete(File("$rootDir/debbie.zip"))
    }

}

tasks.register<Zip>("zipFiles") {
    group = "truthbean"
    description = "zipFiles"

    println("zip files $rootDir/target")
    // doLast {
        from("$rootDir/target/upload")
        archiveFileName.set("debbie.zip")
        destinationDirectory.set(file("$rootDir/target"))
    // }
}

// 添加新任务实现curl请求
val uploadToCentralSonatype by tasks.registering(Exec::class) {
    group = "upload"
    description = "Upload central-bundle.zip to Central Sonatype"
    val sonatypeBearer: String? by project
    val uid = commandLine("curl", "--request", "POST", "--verbose", "--header",
        "Authorization: Bearer $sonatypeBearer",
        "--form", "bundle=@$rootDir/target.zip", "https://central.sonatype.com/api/v1/publisher/upload")
    println(uid)
    // curl --request POST \
    //  --verbose \
    //  --header 'Authorization: Bearer ZXhhbXBsZV91c2VybmFtZTpleGFtcGxlX3Bhca3N3b3JkCg==' \
    //  'https://central.sonatype.com/api/v1/publisher/status?id=28570f16-da32-4c14-bd2e-c1acc0782365' \
    //  | jq
    val json = commandLine("curl", "--request", "POST", "--verbose", "--header",
        "Authorization: Bearer $sonatypeBearer",
        "https://central.sonatype.com/api/v1/publisher/status?id=$uid")
    println(json)
    // curl --request POST \
    //  --verbose \
    //  --header 'Authorization: Bearer ZXhhbXBsZV91c2VybmFtZTpleGFtcGxlX3Bhc3N3b3Jk' \
    //  'https://central.sonatype.com/api/v1/publisher/deployment/28570f16-da32-4c14-bd2e-c1acc0782365
    val res = commandLine("curl", "--request", "POST", "--verbose", "--header",
        "Authorization: Bearer $sonatypeBearer",
        "https://central.sonatype.com/api/v1/publisher/deployment/$uid")
    println(res)
}