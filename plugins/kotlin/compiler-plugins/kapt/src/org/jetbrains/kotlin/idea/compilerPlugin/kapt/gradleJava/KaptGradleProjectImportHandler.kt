// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.compilerPlugin.kapt.gradleJava

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArguments
import org.jetbrains.kotlin.idea.gradleJava.configuration.GradleProjectImportHandler
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData
import java.io.File
import java.util.*

class KaptGradleProjectImportHandler : GradleProjectImportHandler {
    override fun importBySourceSet(facet: KotlinFacet, sourceSetNode: DataNode<GradleSourceSetData>) {
        modifyCompilerArgumentsForPlugin(facet)
    }

    override fun importByModule(facet: KotlinFacet, moduleNode: DataNode<ModuleData>) {
        modifyCompilerArgumentsForPlugin(facet)
    }

    private fun modifyCompilerArgumentsForPlugin(facet: KotlinFacet) {
        val facetSettings = facet.configuration.settings

        // Can't reuse const in Kapt3CommandLineProcessor, we don't have Kapt in the IDEA plugin
        val compilerPluginId = "org.jetbrains.kotlin.kapt3"
        val compilerArguments = facetSettings.compilerArguments ?: CommonCompilerArguments.DummyImpl()

        val newPluginOptions = (compilerArguments.pluginOptions ?: emptyArray()).filter { !it.startsWith("plugin:$compilerPluginId:") }
        val newPluginClasspath = (compilerArguments.pluginClasspaths ?: emptyArray()).filter { !isKaptCompilerPluginPath(it) }

        fun List<String>.toArrayIfNotEmpty() = takeIf { it.isNotEmpty() }?.toTypedArray()

        compilerArguments.pluginOptions = newPluginOptions.toArrayIfNotEmpty()
        compilerArguments.pluginClasspaths = newPluginClasspath.toArrayIfNotEmpty()

        facetSettings.compilerArguments = compilerArguments
    }

    private fun isKaptCompilerPluginPath(path: String): Boolean {
        val lastIndexOfFile = path.lastIndexOfAny(charArrayOf('/', File.separatorChar)).takeIf { it >= 0 } ?: return false
        val fileName = path.drop(lastIndexOfFile + 1).lowercase(Locale.US)
        return fileName.matches("kotlin\\-annotation\\-processing(\\-gradle|\\-embeddable)?\\-[0-9].*\\.jar".toRegex())
    }
}
