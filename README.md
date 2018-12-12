# KotlinApp


### 组件化方案
1. 建立新的业务 module 时, `build.gradle` `apply from: rootProject.file("gradle/module-config.gradle")`
2. 在新的业务 module 中,`gradle.properties` 中 `application` 属性
    * `true` 时, 独立编译
    * `false` 时, 作为 `library` 被 `:app` 依赖