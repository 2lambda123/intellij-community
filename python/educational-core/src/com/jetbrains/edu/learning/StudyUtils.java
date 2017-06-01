package com.jetbrains.edu.learning;

import com.intellij.ide.IdeView;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.NewVirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.wm.*;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.util.DocumentUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.TimeoutUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.MarkdownUtil;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.edu.learning.core.EduAnswerPlaceholderDeleteHandler;
import com.jetbrains.edu.learning.core.EduAnswerPlaceholderPainter;
import com.jetbrains.edu.learning.core.EduNames;
import com.jetbrains.edu.learning.core.EduUtils;
import com.jetbrains.edu.learning.courseFormat.AnswerPlaceholder;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.courseFormat.Lesson;
import com.jetbrains.edu.learning.courseFormat.TaskFile;
import com.jetbrains.edu.learning.courseFormat.tasks.ChoiceTask;
import com.jetbrains.edu.learning.courseFormat.tasks.Task;
import com.jetbrains.edu.learning.courseFormat.tasks.TaskWithSubtasks;
import com.jetbrains.edu.learning.courseFormat.tasks.TheoryTask;
import com.jetbrains.edu.learning.editor.StudyEditor;
import com.jetbrains.edu.learning.stepic.EduStepikUtils;
import com.jetbrains.edu.learning.stepic.OAuthDialog;
import com.jetbrains.edu.learning.stepic.StepicUser;
import com.jetbrains.edu.learning.ui.StudyStepicUserWidget;
import com.jetbrains.edu.learning.ui.StudyToolWindow;
import com.jetbrains.edu.learning.ui.StudyToolWindowFactory;
import com.petebevin.markdown.MarkdownProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.jetbrains.edu.learning.navigation.StudyNavigator.navigateToTask;

public class StudyUtils {
  private StudyUtils() {
  }

  private static final Logger LOG = Logger.getInstance(StudyUtils.class.getName());
  private static final String ourPrefix = "<html><head><script type=\"text/x-mathjax-config\">\n" +
                                          "            MathJax.Hub.Config({\n" +
                                          "                tex2jax: {\n" +
                                          "                    inlineMath: [ ['$','$'], [\"\\\\(\",\"\\\\)\"] ],\n" +
                                          "                    displayMath: [ ['$$','$$'], [\"\\\\[\",\"\\\\]\"] ],\n" +
                                          "                    processEscapes: true,\n" +
                                          "                    processEnvironments: true\n" +
                                          "                },\n" +
                                          "                displayAlign: 'center',\n" +
                                          "                \"HTML-CSS\": {\n" +
                                          "                    styles: {'#mydiv': {\"font-size\": %s}},\n" +
                                          "                    preferredFont: null,\n" +
                                          "                    linebreaks: { automatic: true }\n" +
                                          "                }\n" +
                                          "            });\n" +
                                          "</script><script type=\"text/javascript\"\n" +
                                          " src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS_HTML-full\">\n" +
                                          " </script></head><body><div id=\"mydiv\">";

  private static final String ourPostfix = "</div></body></html>";

  public static void closeSilently(@Nullable final Closeable stream) {
    if (stream != null) {
      try {
        stream.close();
      }
      catch (IOException e) {
        // close silently
      }
    }
  }

  public static boolean isZip(String fileName) {
    return fileName.contains(".zip");
  }

  @Nullable
  public static <T> T getFirst(@NotNull final Iterable<T> container) {
    Iterator<T> iterator = container.iterator();
    if (!iterator.hasNext()) {
      return null;
    }
    return iterator.next();
  }

  public static boolean indexIsValid(int index, @NotNull final Collection collection) {
    int size = collection.size();
    return index >= 0 && index < size;
  }

  @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
  @Nullable
  public static String getFileText(@Nullable final String parentDir, @NotNull final String fileName, boolean wrapHTML,
                                   @NotNull final String encoding) {
    final File inputFile = parentDir != null ? new File(parentDir, fileName) : new File(fileName);
    if (!inputFile.exists()) return null;
    final StringBuilder taskText = new StringBuilder();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding));
      String line;
      while ((line = reader.readLine()) != null) {
        taskText.append(line).append("\n");
        if (wrapHTML) {
          taskText.append("<br>");
        }
      }
      return wrapHTML ? UIUtil.toHtml(taskText.toString()) : taskText.toString();
    }
    catch (IOException e) {
      LOG.info("Failed to get file text from file " + fileName, e);
    }
    finally {
      closeSilently(reader);
    }
    return null;
  }

  public static void updateAction(@NotNull final AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    presentation.setEnabled(false);
    final Project project = e.getProject();
    if (project != null) {
      for (VirtualFile virtualFile : FileEditorManager.getInstance(project).getSelectedFiles()) {
        if (virtualFile != null && getTaskFile(project, virtualFile) != null) {
          presentation.setEnabled(true);
          return;
        }
      }
    }
  }

  public static void updateToolWindows(@NotNull final Project project) {
     final StudyToolWindow studyToolWindow = getStudyToolWindow(project);
    if (studyToolWindow != null) {
      String taskText = getTaskText(project);
      if (taskText != null) {
        studyToolWindow.setTaskText(taskText, null, project);
      }
      else {
        LOG.warn("Task text is null");
      }
      studyToolWindow.updateCourseProgress(project);
    }
  }

  public static void initToolWindows(@NotNull final Project project) {
    final ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
    windowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW).getContentManager().removeAllContents(false);
    StudyToolWindowFactory factory = new StudyToolWindowFactory();
    factory.createToolWindowContent(project, windowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW));

  }

  @Nullable
  public static StudyToolWindow getStudyToolWindow(@NotNull final Project project) {
    if (project.isDisposed()) return null;

    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
    if (toolWindow != null) {
      Content[] contents = toolWindow.getContentManager().getContents();
      for (Content content: contents) {
        JComponent component = content.getComponent();
        if (component != null && component instanceof StudyToolWindow) {
          return (StudyToolWindow)component;
        }
      }
    }
    return null;
  }

  public static void deleteFile(@Nullable final VirtualFile file) {
    if (file == null) {
      return;
    }
    try {
      file.delete(StudyUtils.class);
    }
    catch (IOException e) {
      LOG.error(e);
    }
  }


  /**
   * shows pop up in the center of "check task" button in study editor
   */
  public static void showCheckPopUp(@NotNull final Project project, @NotNull final Balloon balloon) {
    Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (selectedTextEditor == null) {
      return;
    }
    balloon.show(computeLocation(selectedTextEditor), Balloon.Position.above);
    Disposer.register(project, balloon);
  }

  public static RelativePoint computeLocation(Editor editor){

    final Rectangle visibleRect = editor.getComponent().getVisibleRect();
    Point point = new Point(visibleRect.x + visibleRect.width + 10,
                            visibleRect.y + 10);
    return new RelativePoint(editor.getComponent(), point);
  }


  public static boolean isTestsFile(@NotNull Project project, @NotNull final String name) {
    Course course = StudyTaskManager.getInstance(project).getCourse();
    if (course == null) {
      return false;
    }
    EduPluginConfigurator configurator = EduPluginConfigurator.INSTANCE.forLanguage(course.getLanguageById());
    if (configurator == null) {
      return false;
    }
    String testFileName = configurator.getTestFileName();
    return name.equals(testFileName) ||
           name.startsWith(FileUtil.getNameWithoutExtension(testFileName)) && name.contains(EduNames.SUBTASK_MARKER);
  }

  @Nullable
  public static TaskFile getTaskFile(@NotNull final Project project, @NotNull final VirtualFile file) {
    final Course course = StudyTaskManager.getInstance(project).getCourse();
    if (course == null) {
      return null;
    }
    VirtualFile taskDir = getTaskDir(file);
    if (taskDir == null) {
      if (course.isTutorial()) {
        String relativePath = FileUtil.getRelativePath(project.getBaseDir().getPath(), file.getPath(), '/');
        if (relativePath == null) {
          return null;
        }
        return course.getLessons().get(0).getTaskList().get(0).getFile(relativePath);
      }
      return null;
    }
    //need this because of multi-module generation
    if (EduNames.SRC.equals(taskDir.getName())) {
      taskDir = taskDir.getParent();
      if (taskDir == null) {
        return null;
      }
    }
    final String taskDirName = taskDir.getName();
    if (taskDirName.contains(EduNames.TASK)) {
      final VirtualFile lessonDir = taskDir.getParent();
      if (lessonDir != null) {
        int lessonIndex = EduUtils.getIndex(lessonDir.getName(), EduNames.LESSON);
        List<Lesson> lessons = course.getLessons();
        if (!indexIsValid(lessonIndex, lessons)) {
          return null;
        }
        final Lesson lesson = lessons.get(lessonIndex);
        int taskIndex = EduUtils.getIndex(taskDirName, EduNames.TASK);
        final List<Task> tasks = lesson.getTaskList();
        if (!indexIsValid(taskIndex, tasks)) {
          return null;
        }
        final Task task = tasks.get(taskIndex);
        return task.getFile(pathRelativeToTask(file));
      }
    }
    return null;
  }

  public static void drawAllAnswerPlaceholders(Editor editor, TaskFile taskFile) {
    editor.getMarkupModel().removeAllHighlighters();
    final Project project = editor.getProject();
    if (project == null) return;
    final StudyTaskManager taskManager = StudyTaskManager.getInstance(project);
    for (AnswerPlaceholder answerPlaceholder : taskFile.getAnswerPlaceholders()) {
      final JBColor color = taskManager.getColor(answerPlaceholder);
      EduAnswerPlaceholderPainter.drawAnswerPlaceholder(editor, answerPlaceholder, color);
    }

    final Document document = editor.getDocument();
    EditorActionManager.getInstance()
      .setReadonlyFragmentModificationHandler(document, new EduAnswerPlaceholderDeleteHandler(editor));
    EduAnswerPlaceholderPainter.createGuardedBlocks(editor, taskFile);
    editor.getColorsScheme().setColor(EditorColors.READONLY_FRAGMENT_BACKGROUND_COLOR, null);
  }

  @Nullable
  public static StudyEditor getSelectedStudyEditor(@NotNull final Project project) {
    try {
      final FileEditor fileEditor = FileEditorManagerEx.getInstanceEx(project).getSplitters().getCurrentWindow().
        getSelectedEditor().getSelectedEditorWithProvider().getFirst();
      if (fileEditor instanceof StudyEditor) {
        return (StudyEditor)fileEditor;
      }
    }
    catch (Exception e) {
      return null;
    }
    return null;
  }

  @Nullable
  public static Editor getSelectedEditor(@NotNull final Project project) {
    final StudyEditor studyEditor = getSelectedStudyEditor(project);
    if (studyEditor != null) {
      return studyEditor.getEditor();
    }
    return null;
  }

  public static void deleteGuardedBlocks(@NotNull final Document document) {
    if (document instanceof DocumentImpl) {
      final DocumentImpl documentImpl = (DocumentImpl)document;
      List<RangeMarker> blocks = documentImpl.getGuardedBlocks();
      for (final RangeMarker block : blocks) {
        ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> document.removeGuardedBlock(block)));
      }
    }
  }

  public static boolean isRenameableOrMoveable(@NotNull final Project project, @NotNull final Course course, @NotNull final PsiElement element) {
    if (element instanceof PsiFile) {
      VirtualFile virtualFile = ((PsiFile)element).getVirtualFile();
      if (project.getBaseDir().equals(virtualFile.getParent())) {
        return false;
      }
      TaskFile file = getTaskFile(project, virtualFile);
      if (file != null) {
        return false;
      }
      String name = virtualFile.getName();
      return !isTestsFile(project, name) && !isTaskDescriptionFile(name);
    }
    if (element instanceof PsiDirectory) {
      VirtualFile virtualFile = ((PsiDirectory)element).getVirtualFile();
      VirtualFile parent = virtualFile.getParent();
      if (parent == null) {
        return true;
      }
      if (project.getBaseDir().equals(parent)) {
        return false;
      }
      Lesson lesson = course.getLesson(parent.getName());
      if (lesson != null) {
        Task task = lesson.getTask(virtualFile.getName());
        if (task != null) {
          return false;
        }
      }
    }
    return true;
  }

  public static boolean canRenameOrMove(DataContext dataContext) {
    Project project = CommonDataKeys.PROJECT.getData(dataContext);
    PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
    if (element == null || project == null) {
      return false;
    }
    Course course = StudyTaskManager.getInstance(project).getCourse();
    if (course == null || !EduNames.STUDY.equals(course.getCourseMode())) {
      return false;
    }

    return !isRenameableOrMoveable(project, course, element);
  }

  @Nullable
  public static String getTaskTextFromTask(@Nullable final VirtualFile taskDirectory, @Nullable final Task task) {
    if (task == null || task.getLesson() == null || task.getLesson().getCourse() == null) {
      return null;
    }
    final Course course = task.getLesson().getCourse();
    String text = task.getTaskDescription() != null ? task.getTaskDescription() : getTaskTextByTaskName(task, taskDirectory);

    if (text == null) return null;
    if (course.isAdaptive()) text = wrapAdaptiveCourseText(task, text);

    return wrapTextToDisplayLatex(text);
  }

  @NotNull
  private static String constructTaskTextFilename(@NotNull Task task, @NotNull String defaultName) {
    String fileNameWithoutExtension = FileUtil.getNameWithoutExtension(defaultName);
    if (task instanceof TaskWithSubtasks) {
      int activeStepIndex = ((TaskWithSubtasks)task).getActiveSubtaskIndex();
      fileNameWithoutExtension += EduNames.SUBTASK_MARKER + activeStepIndex;
    }
    return addExtension(fileNameWithoutExtension, defaultName);
  }

  private static String wrapAdaptiveCourseText(Task task, @NotNull String text) {
    String finalText = text;
    if (task instanceof TheoryTask) {
      finalText += "\n\n<b>Note</b>: This theory task aims to help you solve difficult tasks. " +
             "Please, read it and press \"Check\" to go further.";
    }
    else if (!(task instanceof ChoiceTask)) {
      finalText += "\n\n<b>Note</b>: Use standard input to obtain input for the task.";
    }
    finalText += getFooterWithLink(task);

    return finalText;
  }

  @NotNull
  private static String getFooterWithLink(Task task) {
    return
    "<div class=\"footer\">" + "<a href=" + EduStepikUtils.getAdaptiveLink(task) + ">Open on Stepik</a>"  + "</div>";
  }

  @NotNull
  private static String addExtension(@NotNull String fileNameWithoutExtension, @NotNull String defaultName) {
    return fileNameWithoutExtension + "." + FileUtilRt.getExtension(defaultName);
  }

  public static String wrapTextToDisplayLatex(String taskTextFileHtml) {
    final String prefix = String.format(ourPrefix, EditorColorsManager.getInstance().getGlobalScheme().getEditorFontSize());
    return prefix + taskTextFileHtml + ourPostfix;
  }

  @Nullable
  private static String getTaskTextByTaskName(@NotNull Task task, @Nullable VirtualFile taskDirectory) {
    if (taskDirectory == null) return null;

    String textFromHtmlFile = getTextByTaskFileFormat(task, taskDirectory, EduNames.TASK_HTML);
    if (textFromHtmlFile != null) {
      return textFromHtmlFile;
    }

    String taskTextFromMd = getTextByTaskFileFormat(task, taskDirectory, EduNames.TASK_HTML);
    return convertToHtml(taskTextFromMd);
  }

  @Nullable
  private static String getTextByTaskFileFormat(@NotNull Task task, @NotNull VirtualFile taskDirectory, @NotNull String taskTextFileName) {
    String textFilename = constructTaskTextFilename(task, taskTextFileName);
    VirtualFile taskTextFile = taskDirectory.findChild(textFilename);

    if (taskTextFile != null) {
      return String.valueOf(LoadTextUtil.loadText(taskTextFile));
    }

    VirtualFile srcDir = taskDirectory.findChild(EduNames.SRC);
    if (srcDir != null) {
      VirtualFile taskTextSrcFile = srcDir.findChild(textFilename);
      if (taskTextSrcFile != null) {
        return String.valueOf(LoadTextUtil.loadText(taskTextSrcFile));
      }
    }

    return null;
  }

  @Nullable
  public static StudyTwitterPluginConfigurator getTwitterConfigurator(@NotNull final Project project) {
    StudyTwitterPluginConfigurator[] extensions = StudyTwitterPluginConfigurator.EP_NAME.getExtensions();
    for (StudyTwitterPluginConfigurator extension: extensions) {
      if (extension.accept(project)) {
        return extension;
      }
    }
    return null;
  }

  @Nullable
  public static String getTaskText(@NotNull final Project project) {
    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (editor == null) {
      return StudyToolWindow.EMPTY_TASK_TEXT;
    }
    Document document = editor.getDocument();
    VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
    if (virtualFile == null) {
      return StudyToolWindow.EMPTY_TASK_TEXT;
    }
    final Task task = getTaskForFile(project, virtualFile);
    if (task != null) {
      return getTaskTextFromTask(task.getTaskDir(project), task);
    }
    return null;
  }

  @Nullable
  public static TaskFile getSelectedTaskFile(@NotNull Project project) {
    VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles();
    TaskFile taskFile = null;
    for (VirtualFile file : files) {
      taskFile = getTaskFile(project, file);
      if (taskFile != null) {
        break;
      }
    }
    return taskFile;
  }

  @Nullable
  public static Task getCurrentTask(@NotNull final Project project) {
    final TaskFile taskFile = getSelectedTaskFile(project);
    return taskFile != null ? taskFile.getTask() : null;
  }

  public static boolean isStudyProject(@NotNull Project project) {
    return StudyTaskManager.getInstance(project).getCourse() != null;
  }

  public static boolean isStudentProject(@NotNull Project project) {
    Course course = StudyTaskManager.getInstance(project).getCourse();
    return course != null && EduNames.STUDY.equals(course.getCourseMode());
  }

  public static boolean hasJavaFx() {
    try {
      Class.forName("javafx.application.Platform");
      return true;
    }
    catch (ClassNotFoundException e) {
      return false;
    }
  }

  @Nullable
  public static Task getTask(@NotNull Project project, @NotNull VirtualFile taskVF) {
    Course course = StudyTaskManager.getInstance(project).getCourse();
    if (course == null) {
      return null;
    }
    VirtualFile lessonVF = taskVF.getParent();
    if (lessonVF == null) {
      return null;
    }
    Lesson lesson = course.getLesson(lessonVF.getName());
    if (lesson == null) {
      return null;
    }
    return lesson.getTask(taskVF.getName());
  }

  @Nullable
  public static VirtualFile getTaskDir(@NotNull VirtualFile taskFile) {
    VirtualFile parent = taskFile.getParent();

    while (parent != null) {
      String name = parent.getName();

      if (name.contains(EduNames.TASK) && parent.isDirectory()) {
        return parent;
      }
      if (EduNames.SRC.equals(name)) {
        return parent.getParent();
      }

      parent = parent.getParent();
    }
    return null;
  }

  @Nullable
  public static Task getTaskForFile(@NotNull Project project, @NotNull VirtualFile taskFile) {
    Course course = StudyTaskManager.getInstance(project).getCourse();
    if (course == null) {
      return null;
    }
    if (course.isTutorial()) {
      List<Task> taskList = course.getLessons().get(0).getTaskList();
      if (taskList.size() == 1) {
        return taskList.get(0);
      }
    }
    VirtualFile taskDir = getTaskDir(taskFile);
    if (taskDir == null) {
      return null;
    }
    return getTask(project, taskDir);
  }

  // supposed to be called under progress
  @Nullable
  public static <T> T execCancelable(@NotNull final Callable<T> callable) {
    final Future<T> future = ApplicationManager.getApplication().executeOnPooledThread(callable);

    while (!future.isCancelled() && !future.isDone()) {
      ProgressManager.checkCanceled();
      TimeoutUtil.sleep(500);
    }
    T result = null;
    try {
      result = future.get();
    }
    catch (InterruptedException | ExecutionException e) {
      LOG.warn(e.getMessage());
    }
    return result;
  }

  @Nullable
  public static Task getTaskFromSelectedEditor(Project project) {
    final StudyEditor editor = getSelectedStudyEditor(project);
    Task task = null;
    if (editor != null) {
      final TaskFile file = editor.getTaskFile();
      task = file.getTask();
    }
    return task;
  }

  @Nullable
  private static String convertToHtml(@Nullable final String content) {
    if (content == null) return null;
    ArrayList<String> lines = ContainerUtil.newArrayList(content.split("\n|\r|\r\n"));
    MarkdownUtil.replaceHeaders(lines);
    MarkdownUtil.replaceCodeBlock(lines);

    return new MarkdownProcessor().markdown(StringUtil.join(lines, "\n"));
  }

  public static boolean isTaskDescriptionFile(@NotNull final String fileName) {
    if (EduNames.TASK_HTML.equals(fileName) || EduNames.TASK_MD.equals(fileName)) {
      return true;
    }
    String extension = FileUtilRt.getExtension(fileName);
    if (!extension.equals(FileUtilRt.getExtension(EduNames.TASK_HTML)) && !extension.equals(FileUtilRt.getExtension(EduNames.TASK_MD))) {
      return false;
    }
    return fileName.contains(EduNames.TASK) && fileName.contains(EduNames.SUBTASK_MARKER);
  }

  @Nullable
  public static VirtualFile findTaskDescriptionVirtualFile(@NotNull Project project, @NotNull VirtualFile taskDir) {
    Task task = getTask(project, taskDir.getName().contains(EduNames.TASK) ? taskDir: taskDir.getParent());
    if (task == null) {
      return null;
    }

    return ObjectUtils.chooseNotNull(taskDir.findChild(constructTaskTextFilename(task, EduNames.TASK_HTML)),
                                     taskDir.findChild(constructTaskTextFilename(task, EduNames.TASK_MD)));
  }

  @NotNull
  public static String getTaskDescriptionFileName(final boolean useHtml) {
    return useHtml ? EduNames.TASK_HTML : EduNames.TASK_MD;
  }

  @Nullable
  public static Document getDocument(String basePath, int lessonIndex, int taskIndex, String fileName) {
    String taskPath = FileUtil.join(basePath, EduNames.LESSON + lessonIndex, EduNames.TASK + taskIndex);
    VirtualFile taskFile = LocalFileSystem.getInstance().findFileByPath(FileUtil.join(taskPath, fileName));
    if (taskFile == null) {
      taskFile = LocalFileSystem.getInstance().findFileByPath(FileUtil.join(taskPath, EduNames.SRC, fileName));
    }
    if (taskFile == null) {
      return null;
    }
    return FileDocumentManager.getInstance().getDocument(taskFile);
  }

  public static void showErrorPopupOnToolbar(@NotNull Project project, String content) {
    final Balloon balloon =
      JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(content, MessageType.ERROR, null).createBalloon();
    showCheckPopUp(project, balloon);
  }

  public static void selectFirstAnswerPlaceholder(@Nullable final StudyEditor studyEditor, @NotNull final Project project) {
    if (studyEditor == null) return;
    final Editor editor = studyEditor.getEditor();
    IdeFocusManager.getInstance(project).requestFocus(editor.getContentComponent(), true);
    final List<AnswerPlaceholder> placeholders = studyEditor.getTaskFile().getActivePlaceholders();
    if (placeholders.isEmpty()) return;
    final AnswerPlaceholder placeholder = placeholders.get(0);
    Pair<Integer, Integer> offsets = getPlaceholderOffsets(placeholder, editor.getDocument());
    editor.getSelectionModel().setSelection(offsets.first, offsets.second);
    editor.getCaretModel().moveToOffset(offsets.first);
    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
  }

  public static void registerStudyToolWindow(@Nullable final Course course, Project project) {
    if (course != null && EduNames.PYCHARM.equals(course.getCourseType())) {
      final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
      registerToolWindows(toolWindowManager, project);
      final ToolWindow studyToolWindow = toolWindowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
      if (studyToolWindow != null) {
        studyToolWindow.show(null);
        initToolWindows(project);
      }
    }
  }

  private static void registerToolWindows(@NotNull final ToolWindowManager toolWindowManager, Project project) {
    final ToolWindow toolWindow = toolWindowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
    if (toolWindow == null) {
      toolWindowManager.registerToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW, true, ToolWindowAnchor.RIGHT, project, true);
    }
  }

  @Nullable public static AnswerPlaceholder getAnswerPlaceholder(int offset, List<AnswerPlaceholder> placeholders) {
    for (AnswerPlaceholder placeholder : placeholders) {
      int placeholderStart = placeholder.getOffset();
      int placeholderEnd = placeholderStart + placeholder.getRealLength();
      if (placeholderStart <= offset && offset <= placeholderEnd) {
        return placeholder;
      }
    }
    return null;
  }

  public static String pathRelativeToTask(VirtualFile file) {
    VirtualFile taskDir = getTaskDir(file);
    if (taskDir == null) return file.getName();
    VirtualFile srcDir = taskDir.findChild(EduNames.SRC);
    if (srcDir != null) {
      taskDir = srcDir;
    }
    return FileUtil.getRelativePath(taskDir.getPath(), file.getPath(), '/');
  }

  public static Pair<Integer, Integer> getPlaceholderOffsets(@NotNull final AnswerPlaceholder answerPlaceholder,
                                                             @NotNull final Document document) {
    int startOffset = answerPlaceholder.getOffset();
    int delta = 0;
    final int length = answerPlaceholder.getRealLength();
    int nonSpaceCharOffset = DocumentUtil.getFirstNonSpaceCharOffset(document, startOffset, startOffset + length);
    if (nonSpaceCharOffset != startOffset) {
      delta = startOffset - nonSpaceCharOffset;
      startOffset = nonSpaceCharOffset;
    }
    final int endOffset = startOffset + length + delta;
    return Pair.create(startOffset, endOffset);
  }

  public static boolean isCourseValid(@Nullable Course course) {
    if (course == null) return false;
    if (course.isAdaptive()) {
      final List<Lesson> lessons = course.getLessons();
      if (lessons.size() == 1) {
        return !lessons.get(0).getTaskList().isEmpty();
      }
    }
    return true;
  }

  public static void createFromTemplate(@NotNull Project project,
                                        @NotNull PsiDirectory taskDirectory,
                                        @NotNull String name,
                                        @Nullable IdeView view,
                                        boolean open) {
    FileTemplate template = FileTemplateManager.getInstance(project).getInternalTemplate(name);
    if (template == null) {
      LOG.info("Template " + name + " wasn't found");
      return;
    }
    try {
      final PsiElement file = FileTemplateUtil.createFromTemplate(template, name, null, taskDirectory);
      if (view != null && open) {
        EditorHelper.openInEditor(file, false);
        view.selectElement(file);
      }
    }
    catch (Exception e) {
      LOG.error(e);
    }
  }
  public static void openFirstTask(@NotNull final Course course, @NotNull final Project project) {
    LocalFileSystem.getInstance().refresh(false);
    final Lesson firstLesson = getFirst(course.getLessons());
    if (firstLesson == null) return;
    final Task firstTask = getFirst(firstLesson.getTaskList());
    if (firstTask == null) return;
    final VirtualFile taskDir = firstTask.getTaskDir(project);
    if (taskDir == null) return;
    final Map<String, TaskFile> taskFiles = firstTask.getTaskFiles();
    VirtualFile activeVirtualFile = null;
    for (Map.Entry<String, TaskFile> entry : taskFiles.entrySet()) {
      final String relativePath = entry.getKey();
      final TaskFile taskFile = entry.getValue();
      taskDir.refresh(false, true);
      final VirtualFile virtualFile = taskDir.findFileByRelativePath(relativePath);
      if (virtualFile != null) {
        if (!taskFile.getActivePlaceholders().isEmpty()) {
          activeVirtualFile = virtualFile;
        }
      }
    }
    if (activeVirtualFile != null) {
      final PsiFile file = PsiManager.getInstance(project).findFile(activeVirtualFile);
      ProjectView.getInstance(project).select(file, activeVirtualFile, false);
      final FileEditor[] editors = FileEditorManager.getInstance(project).openFile(activeVirtualFile, true);
      if (editors.length == 0) {
        return;
      }
      final FileEditor studyEditor = editors[0];
      if (studyEditor instanceof StudyEditor) {
        selectFirstAnswerPlaceholder((StudyEditor)studyEditor, project);
      }
      FileEditorManager.getInstance(project).openFile(activeVirtualFile, true);
    }
    else {
      String first = getFirst(taskFiles.keySet());
      if (first != null) {
        NewVirtualFile firstFile = ((VirtualDirectoryImpl)taskDir).refreshAndFindChild(first);
        if (firstFile != null) {
          FileEditorManager.getInstance(project).openFile(firstFile, true);
        }
      }
    }
  }

  public static void navigateToStep(@NotNull Project project, @NotNull Course course, int stepId) {
    if (stepId == 0 || course.isAdaptive()) {
      return;
    }
    Task task = getTask(course, stepId);
    if (task != null) {
      navigateToTask(project, task);
    }
  }

  @Nullable
  private static Task getTask(@NotNull Course course, int stepId) {
    for (Lesson lesson : course.getLessons()) {
      Task task = lesson.getTask(stepId);
      if (task != null) {
        return task;
      }
    }
    return null;
  }

  @Nullable
  static StudyStepicUserWidget getStepicWidget() {
    JFrame frame = WindowManager.getInstance().findVisibleFrame();
    if (frame instanceof IdeFrameImpl) {
      return (StudyStepicUserWidget)((IdeFrameImpl)frame).getStatusBar().getWidget(StudyStepicUserWidget.ID);
    }
    return null;
  }

  public static void showOAuthDialog() {
    OAuthDialog dialog = new OAuthDialog();
    if (dialog.showAndGet()) {
      StepicUser user = dialog.getStepicUser();
      StudySettings.getInstance().setUser(user);
    }
  }
}
