import { definePlugin } from "@halo-dev/console-shared";
import HomeView from "./views/HomeView.vue";
import { IconMagic } from "@halo-dev/components";
import { markRaw } from "vue";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "ToolsRoot",
      route: {
        path: "/import-doc",
        name: "import-doc",
        component: HomeView,
        meta: {
          title: "文档导入",
          searchable: true,
          menu: {
            name: "文档导入",
            group: "tool",
            icon: markRaw(IconMagic),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
});
