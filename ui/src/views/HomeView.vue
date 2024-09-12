<script setup lang="ts">
import { onMounted } from "vue";
import { VPageHeader } from "@halo-dev/components";

onMounted(() => {
  // confetti({
  //   particleCount: 100,
  //   spread: 70,
  //   origin: { y: 0.6, x: 0.58 },
  // });
});

</script>

<template>
  <VPageHeader title="导入华为备忘录"></VPageHeader>
  <div class="m-0 md:m-4">
    <UppyUpload
      :restrictions="{
        allowedFileTypes: ['.html'],
      }"
      note="从华为备忘录中导出 html 可通过此处导入（其他的 html 文件没有测试过），仅支持.html文件，可批量上传。"
      endpoint="/apis/run.halo.yss/v1/import-hw-html/upload"
      width="100%"
    />
  </div>
</template>

<style lang="scss" scoped></style>
