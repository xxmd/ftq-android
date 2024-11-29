import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    kotlin("android")
    kotlin("kapt")
    id("com.android.application")
    id("com.google.gms.google-services")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":service"))
    implementation(project(":design"))
    implementation(project(":common"))

    implementation(libs.kotlin.coroutine)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.coordinator)
    implementation(libs.androidx.recyclerview)
    implementation(libs.google.material)
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")
//    implementation ("com.dinuscxj:circleprogressbar:1.3.6")
    implementation ("com.github.bumptech.glide:glide:4.15.1")  // Glide 库
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")  // Glide 注解处理器
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.getByName("clean", type = Delete::class) {
    delete(file("release"))
}

val geoFilesDownloadDir = "src/main/assets"

task("downloadGeoFiles") {

    val geoFilesUrls = mapOf(
        "https://github.com/MetaCubeX/meta-rules-dat/releases/download/latest/geoip.metadb" to "geoip.metadb",
        "https://github.com/MetaCubeX/meta-rules-dat/releases/download/latest/geosite.dat" to "geosite.dat",
        // "https://github.com/MetaCubeX/meta-rules-dat/releases/download/latest/country.mmdb" to "country.mmdb",
    )

    doLast {
//        geoFilesUrls.forEach { (downloadUrl, outputFileName) ->
//            val url = URL(downloadUrl)
//            val outputPath = file("$geoFilesDownloadDir/$outputFileName")
//            outputPath.parentFile.mkdirs()
//            url.openStream().use { input ->
//                Files.copy(input, outputPath.toPath(), StandardCopyOption.REPLACE_EXISTING)
//                println("$outputFileName downloaded to $outputPath")
//            }
//        }
    }
}

afterEvaluate {
    val downloadGeoFilesTask = tasks["downloadGeoFiles"]

    tasks.forEach {
        if (it.name.startsWith("assemble")) {
            it.dependsOn(downloadGeoFilesTask)
        }
    }
}

tasks.getByName("clean", type = Delete::class) {
    delete(file(geoFilesDownloadDir))
}