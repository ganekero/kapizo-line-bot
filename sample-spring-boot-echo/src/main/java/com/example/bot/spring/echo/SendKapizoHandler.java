/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.*;

@LineMessageHandler
public class SendKapizoHandler {
  private final Logger log = LoggerFactory.getLogger(SendKapizoHandler.class);
  private final MessageFactory messageFactory = new MessageFactory();

  @EventMapping
  public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    log.info("event: " + event);

    final String originalMessageText = event.getMessage().getText();
    final Instant timestamp = event.getTimestamp();
    final String message = messageFactory.makeMessage(originalMessageText, timestamp);
    final ZonedDateTime utcOffsetDateTime = timestamp.atZone(ZoneOffset.UTC);
    ZonedDateTime jstOffsetDateTime =
        utcOffsetDateTime.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
    final int hour = jstOffsetDateTime.getHour();
    final int minutes = jstOffsetDateTime.getMinute();
    final int seconds = jstOffsetDateTime.getSecond();
    log.info("hour: " + hour + "minutes: " + minutes + "seconds: " + seconds);

    String whereSql = null;
    int dayOfWeek = jstOffsetDateTime.getDayOfWeek().getValue();
    int targetTime = hour * 3600 + minutes * 60 + seconds;
    String whereTime =
        String.format(
            "%s > from_time and %s < to_time and status = 't' and %s > from_weekday and %s < to_weekday",
            targetTime, targetTime, dayOfWeek, dayOfWeek);
    if (originalMessageText.contains("おは")) {
      whereSql = String.format("%s and keywords = 'おは'", whereTime);
    }
    if (originalMessageText.contains("起きてる")) {
      whereSql = String.format("%s and keywords = '起きてる'", whereTime);
    }
    if (originalMessageText.contains("こんばんは")) {
      whereSql = String.format("%s and keywords = 'こんばんは'", whereTime);
    }
    Statement statement;
    ResultSet resultSet;
    final String selectSql = "select reply_contents from reply_message where ";
    final String sql = selectSql + whereSql + ";";
    log.info("SQL: " + sql);
    final Connection conn = getConnection();
    try {
      statement = conn.createStatement();
      resultSet = statement.executeQuery(sql);
      if (resultSet.next()) {
        String replyMsg = resultSet.getString("reply_contents");

        // close connection
        statement.close();
        resultSet.close();
        return new TextMessage(replyMsg);
      } else {
        return new TextMessage("また遊んでね！！!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("なんか知らんけどばーか");
      return new TextMessage("ごめんね。\n予期せぬエラーだからもう一回送ってね。\nそれでもダメだったら親に相談してね");
    }
  }

  public static Connection getConnection() {
    // jdbc:postgresql://<host>:<port>/<dbname>?user=<username>&password=<password>
    URI dbUri;
    try {
      dbUri = new URI(System.getenv("DATABASE_URL"));
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    //    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() +
    // dbUri.getPath();
    String dbUrl =
        "jdbc:postgresql://ec2-54-227-248-71.compute-1.amazonaws.com:5432/d416bt68e3p6ii?password=41b99962bbbb3278fc6ccddfbe2f1ef7c0c6ab21224d19369535cc17e1da3817&sslmode=require&user=dvqlfcdcfxxlkm";
    try {
      return DriverManager.getConnection(dbUrl, username, password);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @EventMapping
  public void handleDefaultMessageEvent(Event event) {
    System.out.println("event: " + event);
  }
}
