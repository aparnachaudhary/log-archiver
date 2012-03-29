/*******************************************************************************
 * Copyright 2011 Krzysztof Otrebski
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.arunoday.logview;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

public class LogRecordBuilder {

  private LogRecord ld;

  public LogRecordBuilder() {
    ld = new LogRecord();
  }

  public LogRecord build() {
    return ld;
  }

  public LogRecordBuilder withId(int id) {
    ld.setId(id);
    return this;
  }

  public LogRecordBuilder withMessage(String message) {
    ld.setMessage(message);
    return this;
  }

  public LogRecordBuilder withClass(String clazz) {
    ld.setClazz(clazz);
    return this;
  }

  public LogRecordBuilder withDate(Date date) {
    ld.setDate(date);
    return this;
  }

  public LogRecordBuilder withLevel(Level level) {
    ld.setLevel(level);
    return this;
  }

  public LogRecordBuilder withLoggerName(String loggerName) {
    ld.setLoggerName(loggerName);
    return this;
  }

  public LogRecordBuilder withMarked(boolean marked) {
    ld.setMarked(marked);
    return this;
  }

  public LogRecordBuilder withMessageId(String messageId) {
    ld.setMessageId(messageId);
    return this;
  }

  public LogRecordBuilder withMethod(String method) {
    ld.setMethod(method);
    return this;
  }

  public LogRecordBuilder withNote(Note note) {
    ld.setNote(note);
    return this;
  }

  public LogRecordBuilder withProperties(Map<String, String> properties) {
    ld.setProperties(properties);
    return this;
  }

  public LogRecordBuilder withThread(String thread) {
    ld.setThread(thread);
    return this;
  }

  public LogRecordBuilder withLogSource(String logSource) {
    ld.setLogSource(logSource);
    return this;
  }

}
