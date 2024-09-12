package run.halo.yss.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.yss.halo.PostRequest;
import run.halo.yss.service.ImportService;
import run.halo.yss.service.PostService;

import java.io.File;
import java.security.Principal;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/12/5
 */
@Service
@RequiredArgsConstructor
public class ImportServiceV2 implements ImportService {
    private final PostService postService;
    private final ReactiveExtensionClient client;

    /**
     * 运行导出任务
     *
     * @param file
     * @return
     */
    @Override
    public Mono<Post> runTask(File file) {
        long old = System.currentTimeMillis();
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Principal::getName)
            .flatMap(owner -> {
                PostRequest postRequest = postService.formatPost(file);
                Post post = postRequest.getPost();
                return postService.draftPost(postRequest).doOnSuccess(re->{
                    // 删除临时目录
                    file.delete();
                });

            });


    }
}
