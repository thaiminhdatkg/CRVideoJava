package vn.crln.video.crvideo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import vn.crln.video.crvideo.page.MainFrame;

import java.awt.*;

@SpringBootApplication
public class MainApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(MainApp.class)
                .headless(false).run(args);

        EventQueue.invokeLater(() -> {
            MainFrame jf = ctx.getBean(MainFrame.class);
            jf.setVisible(true);
        });
    }
}
