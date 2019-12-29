**Invalid target while compiling**

Make sure your `JAVA_HOME` environment variable points to JDK 11. Akka cluster management is posing issues with JDK 8.
Restart your IDE after making changes.

**Address already in use**

Make sure that if you ran this project before that terminal session is closed. Make sure that no other service is running
on that port.

**Large start-up time while building**

While running/building code using `local_run.sh`/`build.sh` `ng ***` command takes so much time.
Related issue on angular-cli's gihtub: `https://github.com/angular/angular-cli/issues/3484`