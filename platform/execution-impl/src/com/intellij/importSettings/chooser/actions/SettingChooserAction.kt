// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.importSettings.chooser.actions

import com.intellij.ide.DataManager
import com.intellij.ide.ui.laf.darcula.ui.OnboardingDialogButtons
import com.intellij.importSettings.data.Product
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.ui.popup.ListPopupStep
import com.intellij.openapi.wm.impl.ToolbarComboButton
import com.intellij.ui.popup.list.ListPopupImpl
import java.awt.event.ActionEvent
import javax.swing.*

abstract class SettingChooserAction : DumbAwareAction(), CustomComponentAction {

  override fun actionPerformed(event: AnActionEvent) {
    val widget = event.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT) as? ToolbarComboButton?
    val step = createStep(DefaultActionGroup(), event.dataContext, widget)
    createPopup(step)
  }

  override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
    val btn = OnboardingDialogButtons.createButton(presentation.text, presentation.icon)
    btn.action = object : AbstractAction(presentation.text, presentation.icon) {
      override fun actionPerformed(e: ActionEvent) {
        val ctx = DataManager.getInstance().getDataContext(btn)
        val event =
          AnActionEvent.createFromAnAction(
            this@SettingChooserAction,
            null,
            ActionPlaces.IMPORT_SETTINGS_DIALOG,
            ctx
          )
        ActionUtil.performDumbAwareWithCallbacks(this@SettingChooserAction, event) {
          this@SettingChooserAction.actionPerformed(event)
        }
      }
    }
    return btn
  }

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  private fun createPopup(step: ListPopupStep<Any>): ListPopup {
    val result = object : ListPopupImpl(null, step) {
      override fun getListElementRenderer(): ListCellRenderer<*> {
        return super.getListElementRenderer()
      }

    }
    result.setRequestFocus(false)
    return result
  }

  private fun createStep(actionGroup: ActionGroup, context: DataContext, widget: JComponent?): ListPopupStep<Any> {
    return JBPopupFactory.getInstance().createActionsStep(actionGroup, context, ActionPlaces.PROJECT_WIDGET_POPUP, false, false,
                                                          null, widget, false, 0, false)
  }

  override fun updateCustomComponent(component: JComponent, presentation: Presentation) {
    (component as? JButton)?.apply {
      text = presentation.text
      icon = presentation.icon
    }
  }
}

data class ImportSettingPopupData(val title: String, val list: List<Product>)


class SettingChooser {
  private val panel = JPanel()

  fun getComponent(): JComponent {
    return panel
  }
}
