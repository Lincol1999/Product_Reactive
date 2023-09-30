# Product management service
This Microservice contains the following endpoints:

- Products

### Tools
- Java 11
- Spring Boot 2.7.16
- Mongo DB
- Spring Boot Started Validation
- Docker

### Configure env
- If you are using vscode, when you run a launch.json file it will be created in the .vscode folder, there it will create your "env" environment variables.
- Modify the values of the env. to your own configuration.

```
{
    "configurations": [
        {
            "args": "",
            "cwd": "${workspaceFolder}",
            "env": {
                "API_PRODUCT": "{YOUR_ENDPOINT}",
                "API_PRODUCT_ROUTER": "{YOUR_ENDPOINT_ROUTER}",
                "DATA_BASE": "{YOUR_DATA_BASE}",
                "DB_PASSWORD": "{YOUR_PASSWORD}",
                "DB_USER": "{YOUR_USER}",
                "UPLOADS_PATH"="{YOUR_PATH}"
            },
            .
            .
            .
        }
    ]
}

```
### Configure .env for docker-compose
- Create a .env file at the root of the project and add the following configurations. They should modify to your preference.
```
API_PRODUCT={YOUR_ENDPOINT}
API_PRODUCT_ROUTER={YOUR_ENDPOINT_ROUTER}
DATA_BASE={YOUR_DATA_BASE}
DB_PASSWORD={YOUR_PASSWORD}
DB_USER={YOUR_USER}
UPLOADS_PATH={YOUR_PATH}
```

### Prepare to Dev or Prod Env
1. package the application
```shell
mvn clean package -DskipTests
```
2. build the application image
```shell
docker build -t lmorales/api-reactivo-productos-core:1.0 .
```
3. startup the containers with docker-compose.yml file 
- You could run just this since it also creates the image.
```shell
docker-compose up