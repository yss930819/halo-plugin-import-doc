package run.halo.yss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "ImportSchema", group = "run.halo.yss",
    version = "v1alpha1", singular = "import", plural = "imports")
public class ImportSchema extends AbstractExtension {
    @Schema
    private String name = "";

}
