plugins {
    id("io.freefair.lombok") version "6.3.0"
    id("maven-publish")
    java
}


group = "me.nekocloud.nms"
version = "1.0"

repositories {
    mavenLocal()
    maven(url = "https://jitpack.io")
}

dependencies {
    compileOnly("me.nekocloud.api:default-lib:1.0")
    compileOnly("me.nekocloud.nms:nms-basic:1.0")
    compileOnly("me.nekocloud.api:public-api:1.0")
    compileOnly("org.projectlombok:lombok:1.18.22")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.nekocloud.nms"
            artifactId = "v1_18_R2"
            version = "1.0"

            from(components["java"])
            artifact(tasks.reobfJar)
        }
    }
}


tasks {
    build {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}
