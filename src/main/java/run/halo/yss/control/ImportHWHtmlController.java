package run.halo.yss.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.plugin.ApiVersion;

import run.halo.yss.service.impl.ImportServiceV2;


@ApiVersion("run.halo.yss/v1/")
@RequestMapping("/import-hw-html")
@RestController
@Slf4j
public class ImportHWHtmlController {
    // apis/
    @Autowired
    private ImportServiceV2 importService;


    @PostMapping(value="/upload",consumes = {
        MediaType.TEXT_MARKDOWN_VALUE,
        MediaType.TEXT_EVENT_STREAM_VALUE,
        "text/*",
        MediaType.APPLICATION_PROBLEM_JSON_VALUE,
        MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<Post> upload(@RequestPart("file") Mono<FilePart> file) {
        // Path _tmp = FileUtil.getPluginTemp();
        //
        //
        // return file.flatMap(filePart -> {
        //     System.out.println(filePart.filename());
        //
        //     File __save_file = new File(_tmp.toString() + "/" + filePart.filename());
        //
        //
        //     return filePart.transferTo(__save_file).flatMap(
        //         f -> {
        //             return importService.runTask(__save_file);
        //         }
        //     );
        // });

        return Mono.just(new Post());

    }
}
