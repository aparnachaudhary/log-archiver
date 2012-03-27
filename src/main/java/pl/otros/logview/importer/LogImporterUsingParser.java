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
package pl.otros.logview.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.Icon;

import pl.otros.logview.LogData;
import pl.otros.logview.LogDataCollector;
import pl.otros.logview.parser.LogParser;
import pl.otros.logview.parser.MultiLineLogParser;
import pl.otros.logview.parser.ParserDescription;
import pl.otros.logview.parser.ParsingContext;

public class LogImporterUsingParser implements LogImporter {

  private static final Logger LOGGER = Logger.getLogger(LogImporterUsingParser.class.getName());
  private LogParser parser = null;

  private ParserDescription pd;

  public LogImporterUsingParser(LogParser parser) {
    super();
    this.parser = parser;
    pd = parser.getParserDescription();
  }

  public void init(Properties properties) throws Exception {
    parser.init(properties);
  }

  @Override
  public void importLogs(InputStream in, final LogDataCollector dataCollector, ParsingContext parsingContext) {
    LOGGER.finest("Log import started ");
    String line = null;
    LogData logData = null;
    String charset = parser.getParserDescription().getCharset();

    BufferedReader logReader = null;
    if (charset == null) {
      logReader = new BufferedReader(new InputStreamReader(in));
    } else {
      try {
        logReader = new BufferedReader(new InputStreamReader(in, charset));
      } catch (UnsupportedEncodingException e1) {
        LOGGER.severe(String.format("Requiered charset [%s] is not supported: %s", charset, e1.getMessage()));
        LOGGER.info(String.format("Using default charset: %s", Charset.defaultCharset().displayName()));
        logReader = new BufferedReader(new InputStreamReader(in));
      }

    }
    while (true) {
      try {
        line = logReader.readLine();
        if (line == null) {
          break;
        }

        if (parser instanceof MultiLineLogParser) {
          synchronized (parsingContext) {
            logData = parser.parse(line, parsingContext);
          }
        } else {
          logData = parser.parse(line, parsingContext);
        }

        if (logData != null) {
          logData.setId(parsingContext.getGeneratedIdAndIncrease());
          logData.setLogSource(parsingContext.getLogSource());
          dataCollector.add(logData);
          parsingContext.setLastParsed(System.currentTimeMillis());
        }

      } catch (IOException e) {
        e.printStackTrace();
        LOGGER.severe(String.format("IOException during log import (file %s): %s", parsingContext.getLogSource(), e.getMessage()));
        break;
      } catch (ParseException e) {
        LOGGER.severe(String.format("ParseEception during log import (file %s): %s", parsingContext.getLogSource(), e.getMessage()));
        e.printStackTrace();
        break;
      }
    }

    try {
      if (parser instanceof MultiLineLogParser) {
        MultiLineLogParser multiLineLogParser = (MultiLineLogParser) parser;
        logData = multiLineLogParser.parseBuffer(parsingContext);
        if (logData != null) {
          logData.setId(parsingContext.getGeneratedIdAndIncrease());
          logData.setLogSource(parsingContext.getLogSource());
          synchronized (parsingContext) {
            dataCollector.add(logData);
          }
          parsingContext.setLastParsed(System.currentTimeMillis());
        }
      }
    } catch (Exception e) {
      LOGGER.info("Cannot parser rest of buffer, probablly stopped importing");
    }

    LOGGER.finest("Log import finished!");
  }


  @Override
  public String getKeyStrokeAccelelator() {
    return pd.getKeyStrokeAccelelator();
  }

  @Override
  public int getMnemonic() {
    return pd.getMenmonic();
  }

  @Override
  public Icon getIcon() {
    return pd.getIcon();
  }

  @Override
  public boolean equals(Object obj) {
    return parser.equals(obj);
  }

  @Override
  public int hashCode() {
    return parser.hashCode();
  }

  public LogParser getParser() {
    return parser;
  }


  @Override
  public void initParsingContext(ParsingContext parsingContext) {
    parser.initParsingContext(parsingContext);
  }


}
