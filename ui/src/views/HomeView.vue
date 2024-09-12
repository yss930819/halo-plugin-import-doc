<script setup lang="ts">
import confetti from "canvas-confetti";
import { onMounted } from "vue";
import { VPageHeader } from "@halo-dev/components";
import { axiosInstance } from "@halo-dev/api-client";

onMounted(() => {
  confetti({
    particleCount: 100,
    spread: 70,
    origin: { y: 0.6, x: 0.58 },
  });
});

const onClick = function () {
  console.log("ok");

  axiosInstance
    .post("/apis/run.halo.yss/v1/import-hw-html/upload", { name: "res" })
    .then((response) => {
      console.log(response);
    });
};
</script>

<template>
  <VPageHeader title="导入文档"></VPageHeader>
  <div class="m-0 md:m-4">
    <UppyUpload
      :restrictions="{
        allowedFileTypes: ['.html'],
      }"
      note="仅支持.html文件，可批量上传"
      endpoint="/apis/run.halo.yss/v1/import-hw-html/upload"
      width="100%"
    />
  </div>
</template>

<style lang="scss" scoped></style>
