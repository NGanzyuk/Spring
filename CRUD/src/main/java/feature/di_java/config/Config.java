package feature.di_java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feature.di_java.controller.PostController;
import feature.di_java.repository.PostRepository;
import feature.di_java.service.PostService;

@Configuration
public class Config {
    @Bean
    public PostController postController(PostService service) {
        return new PostController(service);
    }

    @Bean
    public PostService postService(PostRepository repository) {
        return new PostService(repository);
    }
}
