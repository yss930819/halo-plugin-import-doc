<script setup lang="ts">
import confetti from "canvas-confetti";
import { onMounted } from "vue";
import { VButton } from "@halo-dev/components";
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
    .post("/apis/run.halo.yss/v1/import-hw-html/upload", {name:"res"})
    .then((response) => {
      console.log(response);
    });
};
</script>

<template>
  <div>
    <VButton @click="onClick"> test </VButton>
    
  </div>
</template>

<style lang="scss" scoped>
</style>
