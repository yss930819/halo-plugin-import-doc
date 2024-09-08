package run.halo.yss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ApiVersion;

@ApiVersion("io.sakurasou.halo.typecho/v1")
@RestController
@RequestMapping("/typecho")
public class ImportController {


    @PostMapping("/upload")
    public Mono<Void> import1(@RequestBody final ImportSchema inportSchema) {


        System.out.println("输入参数为:" +inportSchema.getName());

        return Mono.empty();
    }

}
