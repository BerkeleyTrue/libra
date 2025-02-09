import nodeResolve from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";

export default {
  input: "public/js/libra-chart.mjs",
  output: {
    file: "public/js/libra-chart.bundle.mjs",
    format: "esm",
    sourcemap: true,
  },
  plugins: [
    nodeResolve(),
    commonjs({
      defaultIsModuleExports: true,
    }),
  ],
  external: ["squint-cljs", "squint-cljs/core.js"],
};
