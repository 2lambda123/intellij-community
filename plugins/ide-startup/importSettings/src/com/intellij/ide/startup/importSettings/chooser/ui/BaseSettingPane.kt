// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.startup.importSettings.chooser.ui

import com.intellij.ide.startup.importSettings.data.BaseSetting
import com.intellij.ide.startup.importSettings.data.Configurable
import com.intellij.ide.startup.importSettings.data.Multiple
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.UnscaledGaps
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JComponent
import javax.swing.JPanel

fun createSettingPane(setting: BaseSetting, configurable: Boolean): BaseSettingPane {
  return if(setting is Multiple) {
    MultipleSettingPane(createMultipleItem(setting, configurable))
  } else {
    BaseSettingPane(SettingItem(setting, configurable))
  }
}

private fun createMultipleItem(setting: Multiple, configurable: Boolean): SettingItem {
  val list: MutableList<ChildItem> = mutableListOf()

  setting.list.forEach { cs ->
    if (cs.isNotEmpty()) {
      val elements = cs.map { ChildItem(it) }

      if (list.isNotEmpty()) {
        elements[0].separatorNeeded = true
      }
      list.addAll(elements)
    }
  }

  return SettingItem(setting, configurable, childItems = list )
}

open class BaseSettingPane(val item: SettingItem) {
  val setting = item.setting

  private val pane by lazy {
    panel {
      row {
        icon(setting.icon).align(AlignY.TOP).customize(UnscaledGaps(0, 0, 0, 8))
        panel {
          row {
            text(setting.name).customize(UnscaledGaps(0, 0, 2, 0)).resizableColumn()
            if (item.configurable) {
              checkBox("").bindSelected(item::selected) { item.selected = it }.customize(UnscaledGaps(0, 0, 2, 0))
            }
          }

          setting.comment?.let { addTxt ->
            row {
              comment(addTxt).customize(UnscaledGaps(0)).resizableColumn()
            }
          }

          addComponents(this)
        }
      }
    }.apply {
      border = JBUI.Borders.empty(6, 18)
    }
  }

  open fun addComponents(pn: Panel) {

  }

  fun component(): JComponent {
    return pane
  }
}


class MultipleSettingPane(item: SettingItem): BaseSettingPane(item) {

  val configurable = item.configurable && setting is Configurable

  private lateinit var actionLink: ActionLink

  override fun addComponents(pn: Panel) {
    item.childItems ?: return

    if (item.childItems.isNotEmpty()) {
      pn.row {
        val text = if (configurable) {
          "Configure"
        }
        else {
          "Show all"
        }

        actionLink = ActionLink(object : AbstractAction(text) {
          override fun actionPerformed(e: ActionEvent) {
            showPopup()
          }
        }).apply {
          setDropDownLinkIcon()
        }

        cell(actionLink).customize(UnscaledGaps(3, 0, 0, 0))
      }
    }
  }

  private fun showPopup() {
    item.childItems ?: return

    val component = ChildSettingsList(item.childItems, configurable)

    val panel = JPanel(BorderLayout())
    panel.border = JBUI.Borders.empty()

    val scrollPane = JBScrollPane(component)
    panel.add(scrollPane, BorderLayout.CENTER)
    scrollPane.border = JBUI.Borders.empty(5)
    val chooserBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, component)
    chooserBuilder.createPopup().showUnderneathOf(actionLink)
  }
}

data class SettingItem(val setting: BaseSetting, val configurable: Boolean, var selected: Boolean = true, val childItems: List<ChildItem>? = null)
