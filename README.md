# roboshop-shared-library

This repository contains all the common patterns of the CI and CD pipelines of the RoboSHop e-commerce project and it helps in avoiding the repetivite code and make us close to DRY approach.

```
As Pipeline is adopted for more and more projects in an organization, common patterns are likely to emerge. Oftentimes it is useful to share parts of Pipelines between various projects to reduce redundancies and keep code "DRY" [1].

Pipeline has support for creating "Shared Libraries" which can be defined in external source control repositories and loaded into existing Pipelines.

``` 
### Directory structure

The directory structure of a Shared Library repository is as follows:

```
(root)
+- src                     # Groovy source files
|   +- org
|       +- foo
|           +- Bar.groovy  # for org.foo.Bar class
+- vars
|   +- foo.groovy          # for global 'foo' variable
|   +- foo.txt             # help for 'foo' variable
+- resources               # resource files (external libraries only)
|   +- org
|       +- foo
|           +- bar.json    # static helper data for org.foo.Bar

```

## What srs has ?

```
    The src directory should look like standard Java source directory structure. This directory is added to the classpath when executing Pipelines.

```

## What vars has ?

``` 
    The vars directory hosts script files that are exposed as a variable in Pipelines. The name of the file is the name of the variable in the Pipeline. So if you had a file called vars/log.groovy with a function like def info(message)…​ in it, you can access this function like log.info "hello world" in the Pipeline. You can put as many functions as you like inside this file. Read on below for more examples and options.
```

Reference : `https://www.jenkins.io/doc/book/pipeline/shared-libraries/`


### When to call a function along with file name and when can we access it directly.

*** When you can to access a function in a file sample, and if you're referring from somewhere, then you call it as `sample.functionName()`
*** When you can to access a function in a file sample, and if you're referring from the same file, then you call it as `functionName()`


### Outstanding Iteam
** On Scripted Pipeline, we need to ensure if the artifact is there, then I don't want to prepare the artifact.
We achieved this through declarative pipeline, while scripted pipeline turned out to be a challenge.