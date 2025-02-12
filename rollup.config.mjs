import nodeResolve from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import replace from "@rollup/plugin-replace";

export default {
  input: "public/js/libra_chart.mjs",
  output: {
    file: "public/js/libra-chart.bundle.mjs",
    format: "esm",
    sourcemap: true,
  },
  plugins: [
    replace({
      preventAssignment: true,
      values: {
        'process.env.NODE_ENV': JSON.stringify('production')
      }
    }),
    nodeResolve(),
    commonjs({
      defaultIsModuleExports: true,
    }),
  ],
  external: ["squint-cljs", "squint-cljs/core.js"],
};
