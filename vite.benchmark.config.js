// Config for benchmarks application

import {defineConfig} from "vite";
import {printSbtTask} from "./config-common.js";

const replacementForPublic = printSbtTask("benchmarks/benchmarksProd", false)

export default defineConfig({
    resolve: {
        alias: [
            {
                find: "@public",
                replacement: replacementForPublic,
            },
            {
                find: "@subproject",
                replacement: "./benchmarks"
            }
        ],
    },
});
