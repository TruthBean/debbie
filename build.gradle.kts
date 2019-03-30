buildscript {
    repositories {
        mavenLocal()
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        jcenter()
    }
}

group = "com.truthbean.code"

allprojects {
    repositories {
        mavenLocal()
        maven("https://mirrors.huaweicloud.com/repository/maven/")
    }
}

plugins {
    `maven-publish`
    signing
}

val projectVersion: String by project
// 根据我们在gradle.properties中声明的版本名称,来分辨是Release版本还是 snapshots版本
val isReleaseBuild = !projectVersion.endsWith("SNAPSHOT")

// 声明变量记录maven库地址，判断是发布到正式库,还是snapshots库
val mavenRepositoryUrl =
        if (isReleaseBuild) {
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        } else {
            "https://oss.sonatype.org/content/repositories/snapshots"
        }

subprojects {

    version = projectVersion

    apply(plugin = "java-library")
    apply(plugin = "maven")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<Javadoc> {
        options {
            encoding = "UTF-8"
            charset("UTF-8")
        }
    }

    dependencies {
        val jupiterVersion = "5.4.0"
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
        "testImplementation"("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
        "testImplementation"("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
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

    tasks.withType<Javadoc> {
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

    tasks.withType<Delete> {
        delete(fileTree("$rootDir/dist/com/truthbean/code/debbie").matching {
            include("**/$projectVersion/**")
        })
    }

    publishing {
        publications {
            create<MavenPublication>("uploadToMavenRepository") {
                artifactId = project.name
                group = "com.truthbean.code"
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
                    group = "com.truthbean.code"
                    version = projectVersion
                    description.set("a java microservice project")
                    url.set("http://code.truthbean.com/debbie/")
                    properties.set(mapOf(
                            "key" to "value"
                    ))
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/truthbean/debbie/blob/master/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("truthbean")
                            name.set("Rogar·Q (TruthBean)")
                            email.set("truthbean@outlook.com")
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
                    val sonatypeUsername: String by project
                    username = sonatypeUsername

                    val sonatypePassword: String by project
                    password = sonatypePassword
                }
            }
        }
    }

    // 进行数字签名
    signing {
        isRequired = isReleaseBuild && gradle.taskGraph.hasTask("build")
        sign(publishing.publications["uploadToMavenRepository"])
    }

}