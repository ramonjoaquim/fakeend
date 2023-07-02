# Fake-end :hotsprings:

Fake-end is a application made in Spring Boot + mongoDB that's provide a fake end server with verbs 
- POST
- GET
- DELETE
- PUT
- PATCH

It's a good choice for test front-end applications with real response of a API.
## Installation

```bash
mvn install
mvn spring-boot:run

by default application is running in port :8080
```

## MongoDB (Required) :warning:
for run application is need a mongo connection with values set in [application.properties](src/main/resources/application.yml)
> Example using mongoDB Atlas
````
mongodb.uri=mongodb+srv://<USER>:<PASSWORD>@cluster5.t4r5.mongodb.net/<DATABASE>?retryWrites=true&w=majority
mongodb.database=<DATABASE>
````

## Swagger
API docs is available on http://localhost:8080/swagger-ui.html

## Usage
1 - Create a endpoint
> POST http://localhost:8080/fakeend/api/endpoint/create

```json
{
    "name":"person", //to identify collection in mongoDB
    "path": "person" //call in URL
}
```

2 - Use your endpoint
> POST http://localhost:8080/fakeend/person 

- send your JSON data
+ body example below 

```json
{
    "name": "Ramon Joaquim",
     "age": 24,
     "address": {
        "city": "crici city",
        "zip": 88815270
      } 
}
```
> returns status code 201, body empty.

:large_orange_diamond: GET ALL
> GET http://localhost:8080/fakeend/person

return below
```json
{
    "results": 1,
    "content": [
        {
            "name": "Ramon Joaquim",
            "age": 24,
            "address": {
                "city": "crici city",
                "zip": 88815270
            },
            "id": 1
        }
    ]
}
```

:large_orange_diamond: GET BY ID
> GET http://localhost:8080/fakeend/person/1

return below
```json
{
    "name": "Ramon Joaquim",
    "age": 24,
    "address": {
        "city": "crici city",
        "zip": 88815270
    },
    "id": 1
}
```

:large_orange_diamond: UPDATE 
> UPDATE http://localhost:8080/fakeend/person/1
note: the update is update all your data by ID

body example below
```json
{
    "name": "Ramon Joaquim",
     "age": 24
}
```

returns status code 204, body empty.

:large_orange_diamond: PATCH 
> PATCH http://localhost:8080/fakeend/person/1
note: the pacth is update specifc propertie by ID

body example below

```json
{
     "age": 30
}
```

returns 

```json
{
    "name": "Ramon Joaquim",
    "age": 30,
    "address": {
        "city": "crici city",
        "zip": 88815270
    },
    "id": 1
}

```

:large_orange_diamond: DELETE BY ID
> DELETE http://localhost:8080/fakeend/person/1
returns status code 204, body empty.

note: for DELETE verb, is available purge-all to clear all data in your endpoint fake
> DELETE http://localhost:8080/fakeend/person/purge-all
return status code 204, body empty.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.
 
## What's next :soon:
+ personalized timeout for response
+ a main page for endpoints registred

## License
[MIT](https://choosealicense.com/licenses/mit/)
