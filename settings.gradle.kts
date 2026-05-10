pluginManagement {
    repositories {
        // 建议顺序：先走镜像，再走官方
        maven("https://maven.aliyun.com/repository/google") // 【新增】阿里云的 Google 镜像
        maven("https://maven.aliyun.com/repository/central") // 阿里云的 Central 镜像
        maven("https://maven.aliyun.com/repository/gradle-plugin") // 【建议新增】Gradle 插件镜像

        // 官方源放在后面作为备选
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 同样的逻辑，先走镜像
        maven("https://maven.aliyun.com/repository/google") // 【关键】这里必须加 Google 镜像
        maven("https://maven.aliyun.com/repository/central")

        google()
        mavenCentral()
    }
}

rootProject.name = "LayoutLearn"
include(":app")