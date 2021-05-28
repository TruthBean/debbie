/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
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
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        jcenter()
        maven("https://plugins.gradle.org/m2/")
        maven("https://repo.spring.io/plugins-release/")
    }
}

group = "com.truthbean"

allprojects {
    repositories {
        mavenLocal()
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
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        } else {
            "https://oss.sonatype.org/content/repositories/snapshots/"
        }

subprojects {

    repositories {
        mavenLocal()
        maven("https://mirrors.huaweicloud.com/repository/maven/")
    }

    buildscript {
        repositories {
            mavenLocal()
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
        useJUnitPlatform()
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

        configure<JavaPluginConvention> {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
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
            delete(File("$rootDir/$originName/out"))
            delete(File("$rootDir/$originName/build"))
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
    }

}
