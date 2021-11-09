# Web Application

## Tech Stack:
```
1. Java
2. Maven
3. Springboot, Hibernate, Spring Data JPA
4. Postgresql
```

## Assignment 1: How to Demo?...

### 1. Git Demo:

1. Create pull requests between
```
1. Main branch of organization and assignment branch of fork.
2. Main branch of organization and main branch of fork.
3. Main branch of fork and assignment branch of fork.
```
There should be nothing to compare.

2.  TAs and instructors are collaborators to the GitHub repository.
```
https://github.com/orgs/csye6225org/people
```
3. Show case README.md file
```
https://github.com/csye6225org/webapp
```
4. Show case that repository is cloned correctly.
   Execute the following commands in terminal.
```
# cd /home/varad/Desktop/NSC
# cd Github/webapp/
# git remote -v
```

### 2. Web Application Demo


1. Open terminal and go to your github folder
```
# cd /home/varad/Desktop/NSC/Github/webapp
```
2. Switch to main branch.
```
    # git checkout main
```
3. Open IntelliJ, Open project in following location.
   Select 'webapp' folder in this location. And then open project.
```
/home/varad/Desktop/NSC/Github/
```
4. Run the Web application
   4.1. Run 'ProjectApplicationTests.java' from IntelliJ
   4.2. Run 'ProjectApplication.java' from IntelliJ.
5. Open Postman.
   5.1. Workspace 'Csye6225'
   5.2. Folder 'webapp'
6. First we will demo POST request.

6.1. URL: 
```
http://localhost:8080/v1/user
```
6.2. Request Body:
```
{
    "first_name": "Gargi",
    "last_name": "Desai",
    "password": "Gargi@123",
    "username": "gargi@gmail.com"
}
``` 
6.3. Click on send. Response to expect.
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

```
SELECT * FROM "user";
```

6.4. Click on send once more, Response to expect.
```
400 Bad Request

User Already Exists
```

6.6. Show that Non email username does not get created.
Request Body:
```
{
    "first_name": "Gargi",
    "last_name": "Desai",
    "password": "Gargi@123",
    "username": "gargigmailcom"
}
```
Response to expect:
```
400 Bad Request

Username is not a valid Email
```

7. Second we will demo GET request

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
7.1. Change Username and click send. Response to expect.
```
400 Bad Request

User dont Exists
```
Correct the username.

7.2. Change Password and click send. Response to expect.
```
400 Bad Request

Invalid Password
```

8. Third, we will demo PUT request.

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

8.1. Click on send, Response to expect.
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
8.2. Change Username and click send. Response to expect.
```
400 Bad Request

User dont Exists
```
Correct the username.

8.3. Change Password and click send. Response to expect.
```
400 Bad Request

Invalid Password
``` 
8.4. Showcase handling update to fields other than firstname, lastname, password. 
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
Response to expect:
```
400 Bad Request

You cannot update fields other than Firstname, Lastname and Password.
``` 

### 3. Git Repository Content Check

```
https://github.com/csye6225org/webapp/blob/main/.gitignore
```

### 4. AWS Check

1. Login to your root aws account.
2. Go to IAM.
3. Show Groups and their roles.
4. Show Users and their groups.
5. Go to organization and show hierarchy.
6. Show that MFA is enabled on every account.
## Thank you.

## Generate executable Jar....

### 1. Correct path for Java jdk and jre

```
# export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
# export PATH=$JAVA_HOME/bin:$PATH
```

### 2. Go to project location where pom.xml is present

```
# cd /home/varad/Desktop/NSC/Github/webapp/project/
```
### 3. Execute maven build

```
# mvn clean install
```
...
