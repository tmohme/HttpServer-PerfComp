package org.mohme.perfcompare.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@RestController
public class HttpSnoopServer {

  public static void main(String[] args) {
    SpringApplication.run(HttpSnoopServer.class, args);
  }

  @RequestMapping("/")
  public String home(@RequestHeader Map<String, String> headers) {
    StringBuilder buf = new StringBuilder("<html><header></header><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">");

    buf.append("WELCOME TO THE SPRING BOOT SERVER\r\n");
    buf.append("=================================\r\n");

    buf.append("HOSTNAME: ").append(headers.get("host")).append("\r\n");
    buf.append("\r\n");

    if (!headers.isEmpty()) {
      for (Map.Entry<String, String> h: headers.entrySet()) {
        CharSequence key = h.getKey();
        CharSequence value = h.getValue();
        buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
      }
      buf.append("\r\n");
    }

    buf.append("</pre></body></html>");
    return buf.toString();
  }

}
