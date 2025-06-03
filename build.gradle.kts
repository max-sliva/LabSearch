import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("com.couchbase.lite:couchbase-lite-java:3.2.1")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.google.code.gson:gson:2.8.9")
//    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(kotlin("reflect"))
    implementation("org.apache.poi:poi:5.2.0")
//    implementation("org.apache.poi:poi-ooxml:5.2.0")
    implementation("org.apache.poi:poi-ooxml:5.4.0")
//    implementation ("androidx.compose.material3:material3:1.2.0")
    // https://mvnrepository.com/artifact/org.dhatim/fastexcel-reader
    implementation("org.dhatim:fastexcel-reader:0.19.0")
}

//to make exe run in terminal:
// ./gradlew runDistributable
compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "LabSearch"
            packageVersion = "1.0.0"
        }
    }
}
