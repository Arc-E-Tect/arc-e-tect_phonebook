If you want to add a local file called mockserverinitializer.json to the docker image, you can use the `COPY` command in a custom Dockerfile that builds from the mockserver/mockserver:latest image. You can then specify the path to your Dockerfile in the `build` option of your docker-compose.yml file. For example, you could have a directory structure like this:

```
my-app/
├── docker-compose.yml
├── Dockerfile
└── src/
    └── proto/
        └── resources/
            └── config/
                ├── mockserver.properties
                ├── initializerJson.json
                └── mockserverinitializer.json
```

And your docker-compose.yml file could look like this:

```
services:
  mockserver_container:
    build: .
    image: my-mockserver
    container_name: arc_e_tect_phonebook_mockserver
    command: -logLevel DEBUG
    ports:
      - 9091:1080
    environment:
      MOCKSERVER_PROPERTY_FILE: /config/mockserver.properties
      MOCKSERVER_INITIALIZATION_JSON_PATH: /config/initializerJson.json
```

And your Dockerfile could look like this:

```
FROM mockserver/mockserver:latest
COPY src/proto/resources/config/mockserverinitializer.json /config/mockserverinitializer.json
```

This way, you can build and run your image with the mockserverinitializer.json file included using the `docker compose up` command⁵. I hope this helps! 😊

Source: Conversation with Bing, 2/8/2024
(1) Try Docker Compose | Docker Docs. https://docs.docker.com/compose/gettingstarted/.
(2) Adding files to standard images using docker-compose. https://stackoverflow.com/questions/39388877/adding-files-to-standard-images-using-docker-compose.
(3) How to use locally built image in docker-compose file correctly?. https://stackoverflow.com/questions/70473147/how-to-use-locally-built-image-in-docker-compose-file-correctly.
(4) Add a file in a docker image - Stack Overflow. https://stackoverflow.com/questions/56670437/add-a-file-in-a-docker-image.
(5) How to use local Docker image with docker-compose?. https://stackoverflow.com/questions/49800750/how-to-use-local-docker-image-with-docker-compose.