package com.example.bot.spring.echo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class MessageFactoryTest {
  @Test
  void test1() {
    final MessageFactory f = new MessageFactory();
    Assertions.assertEquals(
        "また遊んでね！！", f.makeMessage("ohayou", Instant.parse("2022-06-12T12:00:00.00Z")));
    Assertions.assertEquals(
        "カピ子(蔵)だよ！\nおはよう\n", f.makeMessage("おはよう", Instant.parse("2022-06-12T12:00:00.00Z")));
    Assertions.assertEquals(
        "カピ子(蔵)だよ！\nこんばんは", f.makeMessage("こんばんは", Instant.parse("2022-06-12T20:00:00.00Z")));
  }
}
