package com.intellij.updater;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class CreateAction extends PatchAction {
  public CreateAction(String path) {
    super(path, -1);
  }

  public CreateAction(DataInputStream in) throws IOException {
    super(in);
  }

  protected void doBuildPatchFile(File olderFile, File newerFile, ZipOutputStream patchOutput) throws IOException {
    patchOutput.putNextEntry(new ZipEntry(myPath));

    writeExecutableFlag(patchOutput, newerFile);
    Utils.copyFileToStream(newerFile, patchOutput);

    patchOutput.closeEntry();
  }

  @Override
  protected ValidationResult doValidate(File toFile) {
    ValidationResult result = doValidateAccess(toFile, ValidationResult.Action.CREATE);
    if (result != null) return result;

    if (toFile.exists()) {
      return new ValidationResult(ValidationResult.Kind.CONFLICT,
                                  myPath,
                                  ValidationResult.Action.CREATE,
                                  ValidationResult.ALREADY_EXISTS_MESSAGE,
                                  ValidationResult.Option.REPLACE, ValidationResult.Option.KEEP);
    }
    return null;
  }

  @Override
  protected void doApply(ZipFile patchFile, File toFile) throws IOException {
    prepareToWriteFile(toFile);

    InputStream in = Utils.getEntryInputStream(patchFile, myPath);
    try {
      boolean executable = readExecutableFlag(in);
      Utils.copyStreamToFile(in, toFile);
      logger.info("### copyStreamToFile. to File: " + toFile.getCanonicalPath());
      Utils.setExecutable(toFile, executable);
      logger.info("### setExecutable for File: " + toFile.getCanonicalPath());
    }
    finally {
      in.close();
    }
  }

  private static void prepareToWriteFile(File file) throws IOException {
    if (file.exists()) {
      Utils.delete(file);
      logger.info("### Deleting File: " + file.getCanonicalPath());
      return;
    }

    while (file != null && !file.exists()) {
      file = file.getParentFile();
    }
    if (file != null && !file.isDirectory()) {
      Utils.delete(file);
      logger.info("### Deleting File: " + file.getCanonicalPath());
    }
  }

  protected void doBackup(File toFile, File backupFile) {
    // do nothing
  }

  protected void doRevert(File toFile, File backupFile) throws IOException {
    Utils.delete(toFile);
    logger.info("### Deleting File: " + toFile.getCanonicalPath());
  }
}
