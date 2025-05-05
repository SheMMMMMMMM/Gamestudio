package sk.tuke.kpi.kp.slitherlink.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import sk.tuke.kpi.kp.slitherlink.Service.*;

@SpringBootApplication
@Configuration
@EntityScan("sk.tuke.kpi.kp.slitherlink.entity")
@EnableJpaRepositories(basePackages = "sk.tuke.kpi.kp.slitherlink.Service")
public class GameStudioServer {
    public static void main(String[] args) { SpringApplication.run(GameStudioServer.class, args);}
    @Bean
    public ScoreService scoreService() {return new ScoreServiceJPA();}

    @Bean
    public CommentService commentService() {return new CommentServiceJPA();}

    @Bean
    public RatingService ratingService() {return new RatingServiceJPA();}
    @Bean
    public UserService userService() {
        return new UserService();
    }
    // Добавлено: RestTemplate bean для виклику REST-сервісів
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
