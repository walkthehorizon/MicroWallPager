package com.horizon.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class GreetingPlugin : Plugin<Project> {
    override
    fun apply(project: Project) {
        project.tasks
            .register("greet") { task -> task.doLast { println("Hello from plugin 'com.example.plugin.greeting'") } }
    }
}