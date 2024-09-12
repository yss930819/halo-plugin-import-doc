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
          title: "导入华为备忘录",
          searchable: true,
          menu: {
            name: "导入华为备忘录",
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
