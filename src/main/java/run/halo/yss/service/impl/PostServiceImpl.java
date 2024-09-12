package run.halo.yss.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Ref;
import run.halo.app.infra.Condition;
import run.halo.app.infra.ConditionStatus;
import run.halo.yss.halo.Content;
import run.halo.yss.halo.ContentRequest;
import run.halo.yss.halo.ContentWrapper;
import run.halo.yss.halo.PostRequest;
import run.halo.yss.halo.service.AbstractContentService;
import run.halo.yss.service.PostService;
import run.halo.yss.util.FileUtil;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A default implementation of {@link PostService}.
 *
 * @author guqing
 * @since 2.0.0
 */
@Slf4j
@Component
public class PostServiceImpl extends AbstractContentService implements PostService {
    private final ReactiveExtensionClient client;

    public PostServiceImpl(ReactiveExtensionClient client) {
        super(client);
        this.client = client;
    }


    /**
     * 保存文章
     *
     * @param postRequest
     * @return
     */
    @Override
    public Mono<Post> draftPost(PostRequest postRequest) {
        // System.out.println(JSONUtil.toJsonStr(postRequest));
        return Mono.defer(
                () -> {
                    Post post = postRequest.getPost();
                    return getContextUsername()
                        .map(username -> {
                            post.getSpec().setOwner(username);
                            return post;
                        })
                        .defaultIfEmpty(post);
                }
            )
            //保存文章
            .flatMap(client::create)
            .flatMap(post -> {
                System.out.println("保存文章" + post.toString());
                if (postRequest.getContent() == null) {
                    return Mono.just(post);
                }
                var contentRequest =
                    new ContentRequest(Ref.of(post), post.getSpec().getHeadSnapshot(),
                        postRequest.getContent().getRaw(), postRequest.getContent().getContent(),
                        postRequest.getContent().getRawType());
                //保存文章内容
                return draftContent(post.getSpec().getBaseSnapshot(), contentRequest)
                    .flatMap(contentWrapper -> waitForPostToDraftConcludingWork(
                        post.getMetadata().getName(),
                        contentWrapper)
                    );
            })
            .retryWhen(Retry.backoff(5, Duration.ofMillis(100))
                .filter(OptimisticLockingFailureException.class::isInstance));
    }

    private Mono<Post> waitForPostToDraftConcludingWork(String postName,
        ContentWrapper contentWrapper) {
        return Mono.defer(() -> client.fetch(Post.class, postName)
                .flatMap(post -> {
                    post.getSpec().setBaseSnapshot(contentWrapper.getSnapshotName());
                    post.getSpec().setHeadSnapshot(contentWrapper.getSnapshotName());
                    if (Objects.equals(true, post.getSpec().getPublish())) {
                        post.getSpec().setReleaseSnapshot(post.getSpec().getHeadSnapshot());
                    }
                    Condition condition = Condition.builder()
                        .type(Post.PostPhase.DRAFT.name())
                        .reason("DraftedSuccessfully")
                        .message("Drafted post successfully.")
                        .status(ConditionStatus.TRUE)
                        .lastTransitionTime(Instant.now())
                        .build();
                    Post.PostStatus status = post.getStatusOrDefault();
                    status.setPhase(Post.PostPhase.DRAFT.name());
                    status.getConditionsOrDefault().addAndEvictFIFO(condition);
                    return client.update(post);
                }))
            .retryWhen(Retry.backoff(5, Duration.ofMillis(100))
                .filter(OptimisticLockingFailureException.class::isInstance));
    }


    @Override
    public Mono<ContentWrapper> getHeadContent(Post post) {
        var headSnapshot = post.getSpec().getHeadSnapshot();
        return getContent(headSnapshot, post.getSpec().getBaseSnapshot());
    }


    @Override
    public Mono<ContentWrapper> getReleaseContent(Post post) {
        var releaseSnapshot = post.getSpec().getReleaseSnapshot();
        return getContent(releaseSnapshot, post.getSpec().getBaseSnapshot());
    }


    @Override
    public PostRequest formatPost(File file) {

        String title = file.getName().split(".html")[0];
        StringBuffer content = new StringBuffer();
        String uuid = UUID.randomUUID().toString();
        Document doc;
        try {
            System.out.println("read doc");
            doc = Jsoup.parse(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("read doc over");

        doc.getElementsByTag("div").forEach(
            node -> {
                content.append(changeDivNode(node, uuid));
            }
        );


        Post post = new Post();

        Post.PostSpec postSpec = new Post.PostSpec();
        postSpec.setTitle(title);
        postSpec.setSlug(uuid);
        postSpec.setAllowComment(true);
        postSpec.setDeleted(false);
        Post.Excerpt excerpt = new Post.Excerpt();
        excerpt.setAutoGenerate(true);
        excerpt.setRaw("");
        postSpec.setExcerpt(excerpt);
        postSpec.setPriority(0);
        postSpec.setVisible(Post.VisibleEnum.PUBLIC);
        postSpec.setPublish(false);
        postSpec.setPinned(false);


        Post.PostStatus postStatus = new Post.PostStatus();
        //草稿箱，待发布状态
        postStatus.setPhase(Post.PostPhase.DRAFT.name());
        // postStatus.setContributors(List.of(owner));

        post.setSpec(postSpec);
        // post.setStatus(postStatus);
        //设置元数据才能保存
        Metadata postMeta = new Metadata();
        postMeta.setName(UUID.randomUUID().toString());
        // postMeta.setAnnotations(Map.of("content.halo.run/preferred-editor","bytemd"));
        post.setMetadata(postMeta);


        return new PostRequest(post, new Content(content.toString(),
            content.toString(),
            "html")
        );


    }

    private String changeDivNode(Element node, String postId) {

        StringBuffer out = new StringBuffer();
        // 处理不是图片
        if (node.getElementsByTag("img").isEmpty()) {

            for (int i = 0; i < node.childNodes().size(); i++) {
                out.append(changeNode(node.childNode(i), node, i));
            }

        } else {
            Element img = node.getElementsByTag("img").first();

            assert img != null;

            String src = img.attributes().get("src");

            img.attributes().put("src", saveBase64ImageToFile(src, postId));

            out.append(img);
        }

        return "<div>" + out + "</div>\n";
    }

    private String changeNode(Node node, Node parent, int index) {

        // 处理还有嵌套的情况
        if (node.childNodes().size() > 0) {
            StringBuffer out = new StringBuffer();
            for (int i = 0; i < node.childNodes().size(); i++) {
                out.append(changeNode(node.childNode(i), node, i));
            }

            // 处理 font 情况
            if (node.nodeName().equals("font")) {
                return "<span color=\"" + node.attributes().get("color") + "\" style=\"color: "
                    + node.attributes().get("color") + "\">" + out.toString() + "</span>\n";
            }

            return "<" + node.nodeName() + ">" + out.toString() + "</" + node.nodeName() + ">\n";
        }

        // 处理独立节点
        if (node.nodeName().equals("#text")) {
            return node.toString();
        } else if (node.nodeName().equals("br")) {
            // 最后一个 br删除
            if (index == parent.childNodes().size() - 1) {
                return "";
            }
            // 后面有别的标记的删除
            else if (parent.childNodes().get(index + 1).nodeName() != "#text") {
                return "";
            } else if (parent.childNodes().get(index + 1).toString().isEmpty()) {
                return "";
            } else {
                return node.toString();
            }

        } else if (node.nodeName().equals("font")) {
            return "<span color=\"" + node.attributes().get("color") + "\" style=\"color: "
                + node.attributes().get("color") + "\">" + node.toString() + "</span>\n";
        }

        return node.toString();
    }

    public String saveBase64ImageToFile(String base64Image, String postId) {
        try {
            // 检查Base64字符串是否以数据URI格式开始
            if (base64Image.startsWith("data:image")) {
                // 截取图片格式
                String[] base64Data = base64Image.split(",");
                String imageFormat = base64Data[0].split(";")[0].split(":")[1]; // 获取图片格式
                String base64ImageWithoutHeader = base64Data[1].replace("\n",""); // 获取实际的Base64编码字符串

                // 创建文件名和路径
                String fileName =
                    System.currentTimeMillis() + UUID.randomUUID().toString().replace("-","") + "." + imageFormat.split("/")[1]; // 根据格式创建文件名
                Path outputPath = FileUtil.getAttachmentsPath(postId);
                File file = new File(outputPath + File.separator + fileName);

                // 解码Base64字符串并写入文件
                byte[] imageBytes = Base64.getDecoder().decode(base64ImageWithoutHeader);
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(imageBytes);
                }

                return file.getAbsolutePath().split("attachments")[1]; // 返回文件的存储路径
            } else {
                throw new IllegalArgumentException("Invalid Base64 image data.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 发生异常时返回null
        }
    }
}
