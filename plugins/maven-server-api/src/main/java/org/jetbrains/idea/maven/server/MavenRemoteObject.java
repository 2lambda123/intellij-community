/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.idea.maven.server;

import com.intellij.execution.rmi.RemoteObject;

public class MavenRemoteObject extends RemoteObject {
  @Override
  protected ExceptionProcessor createExceptionProcessor() {
    return new ExceptionProcessor() {
      @Override
      protected boolean isKnownException(Throwable ex) {
        return ex.getClass().getName().startsWith(MavenRemoteObject.class.getPackage().getName());
      }
    };
  }

  public RuntimeException wrapToSerializableRuntimeException(Throwable e) {
    Throwable wrap = wrapException(e);
    return wrap instanceof RuntimeException ? (RuntimeException)wrap : new RuntimeException(wrap);
  }
}
