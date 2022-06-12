package com.example.bot.spring.echo;

import com.linecorp.bot.model.message.TextMessage;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;

public class MessageFactory {
  public String makeMessage(String receivedMessageText, Instant timestamp) {
    final int hour = timestamp.atZone(ZoneOffset.UTC).getHour();

    if ("おはよう".equals(receivedMessageText) && hour >= 9 && hour <= 17) {
      return "カピ子(蔵)だよ！\nおはよう\n";
    }
    if (Objects.equals(receivedMessageText, "こんばんは") && hour < 9 || hour > 17) {
      return "カピ子(蔵)だよ！\nこんばんは";
    }
    return "また遊んでね！！";
    // return message
  }
}
