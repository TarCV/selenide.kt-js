package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.assertThat;

class ScreenshotTest {
  @Test
  void summary() {
    assertThat(new Screenshot("/home/user/shot.png", "/home/user/shot.html").summary())
      .isEqualTo("\nScreenshot: /home/user/shot.png" +
        "\nPage source: /home/user/shot.html");
  }

  @Test
  void summary_is_empty_if_no_files() {
    assertThat(Screenshot.none().summary()).isEqualTo("");
  }

  @Test
  void summary_with_only_image() {
    assertThat(new Screenshot("/home/user/shot.png", null).summary())
      .isEqualTo("\nScreenshot: /home/user/shot.png");
  }

  @Test
  void summary_with_only_page_source() {
    assertThat(new Screenshot(null, "/home/user/shot.html").summary())
      .isEqualTo("\nPage source: /home/user/shot.html");
  }
}
