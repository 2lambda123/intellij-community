// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.tools.projectWizard.ir.buildsystem

import kotlinx.collections.immutable.PersistentList
import org.jetbrains.kotlin.tools.projectWizard.ir.buildsystem.gradle.GradleIR
import org.jetbrains.kotlin.tools.projectWizard.plugins.printer.BuildFilePrinter
import org.jetbrains.kotlin.tools.projectWizard.plugins.printer.GradlePrinter
import org.jetbrains.kotlin.tools.projectWizard.settings.buildsystem.Sourceset
import org.jetbrains.kotlin.tools.projectWizard.settings.buildsystem.SourcesetType
import java.nio.file.Path
import java.util.*

enum class SourcesetSourceType {
    RESOURCES, KOTLIN, JAVA
}

sealed class SourcesetIR : BuildSystemIR {
    abstract val sourcesetType: SourcesetType
    abstract val original: Sourceset
    abstract val sourcePaths: Map<SourcesetSourceType, Path>
}

data class SingleplatformSourcesetIR(
    override val sourcesetType: SourcesetType,
    override val sourcePaths: Map<SourcesetSourceType, Path>,
    override val irs: PersistentList<BuildSystemIR>,
    override val original: Sourceset
) : SourcesetIR(), IrsOwner {
    override fun withReplacedIrs(irs: PersistentList<BuildSystemIR>): SingleplatformSourcesetIR = copy(irs = irs)
    override fun BuildFilePrinter.render() = Unit
}

data class MultiplatformSourcesetIR(
    override val sourcesetType: SourcesetType,
    override val sourcePaths: Map<SourcesetSourceType, Path>,
    val targetName: String,
    override val irs: PersistentList<BuildSystemIR>,
    override val original: Sourceset
) : SourcesetIR(), IrsOwner, GradleIR {
    override fun withReplacedIrs(irs: PersistentList<BuildSystemIR>): MultiplatformSourcesetIR = copy(irs = irs)

    override fun GradlePrinter.renderGradle() {
        getting(sourcesetName, prefix = null) {
            val dependencies = irsOfType<DependencyIR>()
            val needBody = dependencies.isNotEmpty() || dsl == GradlePrinter.GradleDsl.GROOVY
            if (needBody) {
                +" "
                inBrackets {
                    if (dependencies.isNotEmpty()) {
                        indent()
                        sectionCall("dependencies", dependencies)
                    }
                }
            }
        }
        for (dependsOn in original.dependsOnModules) {
            if (dependsOn.sourceSets.any { it.sourcesetType == sourcesetType }) {
                nl()
                indent()
                val capitalSuffix = sourcesetType.name.capitalize(Locale.US) // for example: "Main" or "Test"
                sourcesetName.dependsOn(dependsOn.name + capitalSuffix) // for example: iosSimulatorArm64Main.dependsOn(iosMain)
            }
        }
    }
}

val MultiplatformSourcesetIR.sourcesetName
    get() = targetName + sourcesetType.name.capitalize(Locale.US)
