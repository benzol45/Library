# Book service  
Swagger: http://localhost:8080/swagger-ui/index.html 

Supported operations with books:
* Add, edit, store and delete book info
* Control availability of the books
* Book checkout and returning

Supported operations with members:
* Register, block, unblock

TODO:  
-[ ] Build tutorial
-[ ] DB diagram

Plans for the next release:
* Book information import from openlibrary.org
* Book ordering (in the next version)
* Members notification (in the next version)
* Produced events:
  * Book is checked out by the member
  * Book is returned to the library