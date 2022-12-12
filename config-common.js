import {spawnSync} from "child_process";

export function isDev() {
    return process.env.NODE_ENV !== "production";
}

export function printSbtTask(task, devMode) {
    const buildModeArg = !!devMode ? "-Dbuild-mode=dev" : "-Dbuild-mode=prod";
    const args = [buildModeArg, "--error", "--batch", `print ${task}`];
    const options = {
        stdio: [
            "pipe", // StdIn.
            "pipe", // StdOut.
            "inherit", // StdErr.
        ],
    };
    const result = process.platform === 'win32'
        ? spawnSync("sbt.bat", args.map(x => `"${x}"`), {shell: true, ...options})
        : spawnSync("sbt", args, options);

    if (result.error)
        throw result.error;
    if (result.status !== 0)
        throw new Error(`sbt process failed with exit code ${result.status}`);
    return result.stdout.toString('utf8').trim();
}
