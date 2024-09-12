package run.halo.yss.service;

import java.io.File;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.yss.halo.ContentWrapper;
import run.halo.yss.halo.PostRequest;

public interface PostService  {
    Mono<Post> draftPost(PostRequest postRequest);

    Mono<ContentWrapper> getHeadContent(Post post);

    Mono<ContentWrapper> getReleaseContent(Post post);

    PostRequest formatPost(File file);
}
