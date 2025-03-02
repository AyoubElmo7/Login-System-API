# **Login System API**

Built a back-end service for a login system using Java in Spring Boot

## __Summary of APIs__

**Endpoint: User Login**

* Path: /user/login
* Method: POST
* Payload: UserDTO JSON
* Response: JSON containing a JWT

Description:

Takes in User login creadentials as a Json and returns a Json containing a Jave Web Token that can be sent back for authenticating the user when making certain API requests.

Payload:

```json
{
    "username": "Testing",
    "password": "!Tt123456789"
}
```

Response:

```json
{
    "token": "JavaWebToken",
}
```

**Endpoint: User Registration**

* Path: /user/register
* Method: PUT
* Payload: UserDTO JSON
* Response: String

Description:

Takes in User registration info as a Json and returns a String denoting a successful account creation. Encodes the user's password before saving it to the database.

Payload:

```json
{
    "username": "Testing",
    "password": "!Tt123456789",
    "email": "testing1237@gmail.com",
    "securityQuestion": "Testing Security Question",
    "securityAnswer": "Testing Security Answer"
}
```

**Endpoint: Initializing Password Reset**

* Path: /user/forgotpassword
* Method: POST
* Payload: ForgotPasswordDTO JSON
* Response: String

Description:

Takes in the details of the User to validate their ownership of the account, in the form of Json. Sends an email to the user containing a Java Web Token generated specifically for this case. 
This token will be needed for when the user actually updates their password. The response is a String stating that the email was successfully sent to the user. 

Payload:

```json
{
    "email": "ayoub.elmou7@gmail.com",
    "securityQuestion": "Testing Security Question",
    "securityAnswer": "Testing Security Answer"
}
```

**Endpoint: Resetting User Password**

* Path: /user/resetPassword
* Method: POST
* Payload: String password
           String Token 
* Response: String

Description:

Takes in the User's new password as a String, as well as a Token as a String. This token is the Java Web Token we sent to the user via email, and will be used to ensure the owner is making this request. 
The token is valid for only a few minutes, and different secret keys are used for each different use case of the Java Web Token. Encodes the user's new password before updating the User credentials within the database.

## __Configurations to Set Up__

You must add configurations to the application.properties file for this service to work. 

Adding the relevant information for the local MySQL database. The url might be different depending on the naming of the local database.

```json
spring.datasource.url=jdbc:mysql://localhost:3306/Login_System
spring.datasource.username=
spring.datasource.password=
```

Add a client id which is given by google upon request, varies by gmail account

```json
spring.security.oauth2.client.registration.google.client-id=
```


Add an email and password for the email credentials which will be used to send emails to user's upon requesting to reset their password.

```json
emailsender.email=
emailsender.password=
```

## __Miscellaneous Points__  
## ** __Read Before Running__ **

Some of the configurations were set up in this way for the sake of convenience. For instance, in a production environment the Java Web Tokens would not be included within the application.properties file and would be stored
elsewhere (Set as an environment variable, placed in a Secure Vault or a .env file). A local database was used instead of a cloud environment for the sake of simplicity (I have plans to transition to a free cloud environment 
later on).

Make sure to add the appropriate link in the sendEmail method in EmailTokenService to redirect the user to the page to update their password.

Added code coverage across the board, to ensure that any breaking changes would be caught.


  
