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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@LineMessageHandler
public class SendKapizoHandler {
  private final Logger log = LoggerFactory.getLogger(SendKapizoHandler.class);
  private final MessageFactory messageFactory = new MessageFactory();

  // db
  //    String url =
  // "jdbc:postgres://dvqlfcdcfxxlkm:41b99962bbbb3278fc6ccddfbe2f1ef7c0c6ab21224d19369535cc17e1da3817@ec2-54-227-248-71.compute-1.amazonaws.com:5432/d416bt68e3p6ii";
  //    String user = "dvqlfcdcfxxlkm";
  //    String password = "41b99962bbbb3278fc6ccddfbe2f1ef7c0c6ab21224d19369535cc17e1da3817";

  @EventMapping
  public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    log.info("event: " + event);

    // access to db
    //        accessToDatabase();
    final Connection conn = getConnection();
    //     final String result= conn.createStatement("select * from message;");

    final String originalMessageText = event.getMessage().getText();

    final Instant timestamp = event.getTimestamp();

    //        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    //        final String dtStr = df.format(date);

    final String message = messageFactory.makeMessage(originalMessageText, timestamp);
    final int hour = timestamp.atZone(ZoneOffset.UTC).getHour();

    if ("おはよう".equals(originalMessageText) && hour >= 9 && hour <= 17) {
      return new TextMessage("カピ子(蔵)だよ！\nおはよう\n");
    }
    if (Objects.equals(originalMessageText, "こんばんは") && hour < 9 || hour > 17) {
      return new TextMessage("カピ子(蔵)だよ！\nこんばんは");
    }
    return new TextMessage("また遊んでね！！!");
  }

  //    private void accessToDatabase() {
  //        Connection conn = null;
  //        Statement stmt = null;
  //        ResultSet rset = null;
  //
  //        //PostgreSQLへ接続
  //        try {
  //            conn = DriverManager.getConnection(url, user, password);
  //
  //            //SELECT文の実行
  //            stmt = conn.createStatement();
  //            String sql = "SELECT 1";
  //            rset = stmt.executeQuery(sql);
  //
  //            //SELECT結果の受け取り
  //            while(rset.next()){
  //                String col = rset.getString(1);
  //                System.out.println(col);
  //            }
  //
  //        } catch (SQLException e) {
  //            e.printStackTrace();
  //        }
  //
  //
  //    }

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
//    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
      String dbUrl = "jdbc:postgresql://ec2-54-227-248-71.compute-1.amazonaws.com:5432/d416bt68e3p6ii?password=41b99962bbbb3278fc6ccddfbe2f1ef7c0c6ab21224d19369535cc17e1da3817&sslmode=require&user=dvqlfcdcfxxlkm";
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
