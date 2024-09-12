package run.halo.yss.service;

import java.io.File;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.yss.halo.ContentWrapper;
import run.halo.yss.halo.PostRequest;

public interface PostService  {
    Mono<Post> draftPost(PostRequest postRequest);

    Mono<Post> updatePost(PostRequest postRequest);

    Mono<Post> updateBy(@NonNull Post post);

    Mono<ContentWrapper> getHeadContent(String postName);

    Mono<ContentWrapper> getHeadContent(Post post);

    Mono<ContentWrapper> getReleaseContent(String postName);

    Mono<ContentWrapper> getReleaseContent(Post post);

    Mono<Post> publish(Post post);

    Mono<Post> unpublish(Post post);

    Mono<Post> getByUsername(String postName, String username);

    PostRequest formatPost(File file);
}
