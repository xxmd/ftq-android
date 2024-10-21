import com.github.kr328.golang.GolangBuildTask
import com.github.kr328.golang.GolangPlugin
import org.jetbrains.kotlin.cli.jvm.main
import java.io.FileOutputStream
import java.net.URL
import java.time.Duration

plugins {
    kotlin("android")
    id("com.android.library")
    id("kotlinx-serialization")
}

val geoipDatabaseUrl =
    "https://raw.githubusercontent.com/Loyalsoldier/geoip/release/Country.mmdb"
val geoipInvalidate = Duration.ofDays(7)!!
val geoipOutput = buildDir.resolve("intermediates/golang_blob")
val golangSource = file("src/main/golang/native")

android {
    productFlavors {
        all {
            externalNativeBuild {
                cmake {
                    arguments("-DGO_SOURCE:STRING=${golangSource}")
                }
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(project(":common"))

    implementation(libs.androidx.core)
    implementation(libs.kotlin.coroutine)
    implementation(libs.kotlin.serialization.json)
}

afterEvaluate {
    tasks.withType(GolangBuildTask::class.java).forEach {
        it.inputs.dir(golangSource)
    }
}// 在任务执行之后监听 afterTask 的事件
afterEvaluate {
    tasks.withType(GolangBuildTask::class.java).forEach {
        println("environment: " + it.environment)
       println("commandLine: " + it.commandLine)
    }
}

task("downloadGeoipDatabase") {
    val databaseFile = geoipOutput.resolve("Country.mmdb")
    val moduleFile = geoipOutput.resolve("go.mod")
    val sourceFile = geoipOutput.resolve("blob.go")

    val moduleContent = """
        module "cfa/blob"
    """.trimIndent()

    val sourceContent = """
        package blob
        
        import _ "embed"
        
        //go:embed Country.mmdb
        var GeoipDatabase []byte
    """.trimIndent()

    outputs.dir(geoipOutput)

    onlyIf {
        System.currentTimeMillis() - databaseFile.lastModified() > geoipInvalidate.toMillis()
    }

    doLast {
        geoipOutput.mkdirs()

        moduleFile.writeText(moduleContent)
        sourceFile.writeText(sourceContent)

        URL(geoipDatabaseUrl).openConnection().getInputStream().use { input ->
            FileOutputStream(databaseFile).use { output ->
                input.copyTo(output)
            }
        }
    }
}

afterEvaluate {
    val downloadTask = tasks["downloadGeoipDatabase"]

    tasks.forEach {
        if (it.name.startsWith("externalGolangBuild")) {
            it.dependsOn(downloadTask)
        }
    }
}
