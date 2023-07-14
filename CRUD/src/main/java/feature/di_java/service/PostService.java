package feature.di_java.service;

import feature.di_java.exception.NotFoundException;
import feature.di_java.model.Post;
import feature.di_java.repository.PostRepository;

import java.util.Map;

public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Map<Long, Post> all() {
        return repository.all();
    }

    public Post getById(long id) {
        return repository.getById(id).orElseThrow(NotFoundException::new);
    }

    public Post save(Post post) {
        return repository.save(post);
    }

    public void removeById(long id) {
        repository.removeById(id);
    }
}

