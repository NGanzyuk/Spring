package repository;

import model.Post;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

// Stub
public class PostRepository {
    private final ConcurrentMap<Long, Post> repository;

    private static AtomicLong nextId = new AtomicLong();

    public PostRepository() {
        this.repository = new ConcurrentHashMap<>();
    }

    private long getNextId() {
        return nextId.incrementAndGet();
    }

    public ConcurrentMap<Long, Post> all() {
        return repository;
    }

    public Optional<Post> getById(Long id) {
        return Optional.ofNullable(repository.get(id));
    }

    public Post save(Post post) {
        long id = post.getId();
        if (id == 0) {
            id = getNextId();
            post.setId(id);
          repository.put(id, post);
        } else {
          repository.put(id, post);
        }
        return post;
    }

    public void removeById(long id) {
      repository.remove(id);
    }
}
