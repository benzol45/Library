# Book service  
Swagger: http://localhost:8080/swagger-ui/index.html 

Supported operations with books:
* Add, edit, store and delete book info
* Book information import from openlibrary.org
* Control availability of the books
* Book checkout and returning

Supported operations with members:
* Register
* Block & unblock

To start:
1. Start PostgreSql in docker with 
```docker run --env=PGDATA=/var/lib/postgresql/data --env=POSTGRES_USER=postgres --env=POSTGRES_PASSWORD=postgres --env=POSTGRES_DB=library --volume=postgres_data:/var/lib/postgresql/data --volume=/var/lib/postgresql/data -p 5432:5432 -d postgres:15```
2. Build & start this project
3. Open http://localhost:8080/swagger-ui/index.html to interact with API

TODO:  
-[ ] DB diagram

Plans for the next release:
* Book ordering (in the next version)
* Members notification (in the next version)
* Produced events:
  * Book is checked out by the member
  * Book is returned to the library