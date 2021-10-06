# Web Application

## Tech Stack:
```
1. Java
2. Maven
3. Springboot, Hibernate, Spring Data JPA
4. Postgresql
```

## Assignment 1: How to Demo?

1. Open terminal and go to your github folder
```
# cd /home/varad/Desktop/NSC/Github/webapp
```
2. Switch to main branch.
```
    # git checkout main
```
2. Run the web application from IntelliJ.
3. Open Postman.
4. First we will demo POST request.

4.1. URL: 
```
http://localhost:8080/v1/user
```
4.2. Request Body:
```
{
    "first_name": "Gargi",
    "last_name": "Desai",
    "password": "Gargi@123",
    "username": "gargi@gmail.com"
}
``` 
4.3. Click on send. Response to expect.
```
201 Created

{
    "firstName": "Gargi",
    "lastName": "Desai",
    "account_created": "2021-10-06T17:07:29.776Z",
    "emailId": "gargi@gmail.com",
    "id": "4fe8360c-9a31-4488-a2fa-80e18c4235be",
    "account_updated": "2021-10-06T17:07:29.776Z"
}
```
After getting the response, open the database and execute the following query. Then showcase that 
1. Password is encrypted
2. id, account_created and account_updated fields are getting populated automatically.

4.4. Click on send once more, Response to expect.
```
400 Bad Request

User Already Exists
```

4.5. Empty One of the field, then click send. Response to expect.
```
400 Bad Request

Firstname, Lastname, Username and Password cannot be empty in JSON request body.

```

5. Second we will demo GET request

URL:
```
http://localhost:8080/v1/user/self
```
Authorization:
```
Select 'Basic Auth'
Username: gargi@gmail.com
Password: Gargi@123
```
Response to expect:
```
200 OK

{
    "firstName": "Gargi",
    "lastName": "Desai",
    "account_created": "2021-10-06T17:07:29.776Z",
    "emailId": "gargi@gmail.com",
    "id": "4fe8360c-9a31-4488-a2fa-80e18c4235be",
    "account_updated": "2021-10-06T17:07:29.776Z"
}
```
5.1. Change Username and click send. Response to expect.
```
400 Bad Request

User dont Exists
```
Correct the username.

5.2. Change Password and click send. Response to expect.
```
400 Bad Request

Invalid Password
```

6. Third, we will demo PUT request.

URL:
```
http://localhost:8080/v1/user/self
```
Request Body:
```
{
    "first_name": "Gargi Ratnakar",
    "last_name": "Desai",
    "password": "Gargi@123",
    "username": "gargi@gmail.com"
}
```
Authorization:
```
Select 'Basic Auth'
Username: gargi@gmail.com
Password: Gargi@123
```

6.1. Click on send, Response to expect.
```
200 OK

{
    "firstName": "Gargi Ratnakar",
    "lastName": "Desai",
    "account_created": "2021-10-06T17:07:29.776Z",
    "emailId": "gargi@gmail.com",
    "id": "4fe8360c-9a31-4488-a2fa-80e18c4235be",
    "account_updated": "2021-10-06T17:07:29.776Z"
}
```
6.2. Change Username and click send. Response to expect.
```
400 Bad Request

User dont Exists
```
Correct the username.

6.3. Change Password and click send. Response to expect.
```
400 Bad Request

Invalid Password
``` 
6.4. Showcase handling update to fields other than firstname, lastname, password. 
```
{
    "first_name": "Varad",
    "last_name": "Desai",
    "username": "varad@gmail.com",
    "account_created": "2021-10-06T17:07:29.776Z"
}

Utility strings:  
"account_updated": "2021-10-06T17:07:29.776Z"
"id": "4fe8360c-9a31-4488-a2fa-80e18c4235be"
```
Response to expect
```
400 Bad Request

You cannot update fields other than Firstname, Lastname and Password.
``` 




















