package run.halo.yss.halo;

import lombok.Data;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.Ref;

/**
 * Post and content data for creating and updating post.
 *
 * @author guqing
 * @since 2.0.0
 */
@Data
public class PostRequest {


    private Post post;
    private Content content;

    public PostRequest(Post post, Content content) {
        this.post = post;
        this.content = content;
    }
}
