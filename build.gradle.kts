plugins {
    kotlin("multiplatform") version "1.8.22"
    id("com.android.library")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

ext["PUBLISH_GROUP_ID"] = "com.davidarvelo"
ext["PUBLISH_VERSION"] = "3.2.0"
ext["PUBLISH_ARTIFACT_ID"] = "fractional-indexing"

apply(from = "${rootDir}/scripts/publish-root.gradle")
apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle")

repositories {
    google()
    mavenCentral()
}

//group = "com.davidarvelo"
//version = "3.2.0"
group = ext["PUBLISH_GROUP_ID"]!!
version = ext["PUBLISH_VERSION"]!!

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
        nodejs {

        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native")
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
        hostOs == "Mac OS X" -> macosX64()
        hostOs == "Linux" -> linuxX64()
        isMingwX64 -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    macosX64 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    iosArm64 {
        binaries {
            framework {
                baseName = "library"
            }
        }
    }
    iosX64 {
        binaries {
            framework {
                baseName = "library"
            }
        }
    }
//    iosSimulatorArm64()
    iosSimulatorArm64 {
        binaries {
            framework {
                baseName = "library"
            }
        }
    }
    android {
        publishAllLibraryVariants()
    }
    linuxX64()
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
//        val nativeMain by getting
//        val nativeTest by getting
        val iosSimulatorArm64Main by sourceSets.getting
        val iosSimulatorArm64Test by sourceSets.getting
        val macosX64Main by getting
        val macosX64Test by getting
        val iosArm64Main by getting
        val iosArm64Test by getting
        val iosX64Main by getting
        val iosX64Test by getting
        val androidMain by getting
        val androidInstrumentedTest by getting {
//            dependencies {
//                implementation("junit:junit:4.13.2")
//            }
        }
        val linuxX64Main by getting
        val linuxX64Test by getting

        // Set up dependencies between the source sets
        iosSimulatorArm64Main.dependsOn(iosArm64Main)
        iosSimulatorArm64Test.dependsOn(iosArm64Test)
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml") // Create AndroidManifest.xml and provide path to it
    namespace = "com.davidarvelo.fractionalindexing"
    compileSdk = 33

    kotlin {
        jvmToolchain(11)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = 23
        targetSdk = 33
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
        }
    }
}

task("listComponents") {
    afterEvaluate {
        println("Components: " + components.map { it.name })
    }
}