package run.halo.yss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ApiVersion;

@ApiVersion("run.halo.yss/v1/")
@RestController
@RequestMapping("/import-hw-html")
public class ImportHWHtmlController {
    // apis/


    @PostMapping("/upload")
    public Mono<Void> import1(@RequestBody final ImportSchema importSchema) {


        System.out.println("输入参数为:" + importSchema.getName());

        return Mono.empty();
    }

}
