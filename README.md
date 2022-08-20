# Product Info Service
ETL-like service for extract the file
> product-info.json

from provided builds and save them do DB for future use.

the example of the target file:
```json
{
  "name": "JetBrains Gateway",
  "version": "2022.2",
  "buildNumber": "222.3345.108",
  "productCode": "GW",
  "dataDirectoryName": "JetBrainsGateway2022.2",
  "svgIconPath": "bin/gateway.svg",
  "launch": [
    {
      "os": "Linux",
      "launcherPath": "bin/gateway.sh",
      "javaExecutablePath": "jbr/bin/java",
      "vmOptionsFilePath": "bin/gateway64.vmoptions",
      "startupWmClass": "jetbrains-gateway"
    }
  ],
  "bundledPlugins": [
    "com.intellij",
    "com.intellij.dev",
    "com.jetbrains.gateway.terminal",
    "com.jetbrains.space",
    "org.jetbrains.plugins.terminal"
  ],
  "modules": [
    "com.intellij.modules.platform",
    "com.intellij.modules.ssh",
    "com.intellij.modules.ssh.ui",
    "com.jetbrains.gateway"
  ],
  "fileExtensions": [
  ]
}
```
## Main API
*  GET / - simple INFO-panel
* GET /status - service status as json
* POST /refresh - run full rescan
* POST /refresh/code/{productCode} - rescan by product
* GET /code/{productCode} - get all 'product-info.json' files by provided product
* GET /code/{productCode}/build/{buildNumber} - get uniq 'product-info.json' file for specified build

> **Swagger UI** is available by the link [/v3/api-docs](/v3/api-docs)
> 
> **api-docs** are available by the link [/v3/api-docs](/v3/api-docs)

## How to run
The most convenient way to run service as cluster just run docker-compose.yaml below:
[docker-compose.yaml](docker-compose.yaml)
> pay attention about awailable ports on your host 
> ```yaml
>    ports:
>      - "8080-8081:8080"
>```
> also you can change number of replical with
> ```yaml
>    deploy:
>      mode: replicated
>      replicas: 2
>```

or

if you want to run service locally, please but pay attention about create DB before with [docker-compose-db.yaml](docker-compose-db.yaml)

Then just build it
> mvn clean install

Then just run the project with 'local' profile:
as usual java app
> java -jar product-info-service-runner-1.0.0.jar -Dspring.profiles.active=local

**or with your favorite IDE**

First scan will run automatically after about 1 minute after bootstrap.

In case you would like to run the tests, just run
> mvn clean test

**or use your IDE** as well
