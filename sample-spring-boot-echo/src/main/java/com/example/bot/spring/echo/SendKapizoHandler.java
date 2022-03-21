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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@LineMessageHandler
public class SendKapizoHandler {
    private final Logger log = LoggerFactory.getLogger(SendKapizoHandler.class);

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        log.info("event: " + event);
        final String originalMessageText = event.getMessage().getText();

        final Instant timestamp = event.getTimestamp();
        final Date date = Date.from(timestamp);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String dtStr = df.format(date);

        if ("おはよう".equals(originalMessageText)) {
            return new TextMessage("カピ子(蔵)だよ！\nおはよう\n" + dtStr);
        }
        if (Objects.equals(originalMessageText, "こんばんは")) {
            return new TextMessage("カピ子(蔵)だよ！\nこんばんは");
        }
        return new TextMessage("また遊んでね！！");
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}
