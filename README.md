# Information-service

## Fronted configuration for local developement

First you need to install [Node.js](https://nodejs.org/en)

Once Node is ready you can now install project dependencies and run the server. Open website directory in terminal and then follow these steps:  
1. npm install - needed to install dependencies locally, you need to do this step only when it's your first time running frontend on your machine or if something changed in file website/package.json
2. npm run dev - this command spins up server hosting website, output of the command will have the address and port that you can use access the website

To shut down the server simply use CTRL+C hotkey or close terminal.

## Backend endpoints
### Login:
<ol>
    <li><b>GET /client/login (/editorial/login)</b></li>
        This endpoint allows the retrieval of a login page or a logout confirmation message depending on the presence of a query parameter named "logout".
    <br><br>
    <b>Query Parameters:</b>
    <br>
    <ul>
         <li>logout (optional): a string parameter used to indicate if the user has logged out.</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    The response of this endpoint is a ResponseEntity object that wraps the response message and the HTTP status code. 
    If the "logout" parameter is present, the message "You have been successfully logged out." will be returned with a 200 OK status code.
    If the "logout" parameter is not present, the message "Login page." will be returned with a 200 OK status code.
    <br><br>
    <li><b>POST /client/login/v2 (/editorial/login/v2)</b></li>
        This endpoint allows a user to log in to the system. The login credentials must be provided in the request body in JSON format.
        If the login is successful, the client will be authorized.
    <br><br>
    <b>Request Body:</b>
    <br>
    The request body must contain a JSON object representing the user's login information. The object should contain the following fields:
    <ul>
        <li>username (required): a string representing the user's username.</li>
        <li>password (required): a string representing the user's password.</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    The response is a status code indicating the outcome of the login operation. If the login was successful, a 200 OK status code is returned. 
    If the login credentials are incorrect, a 401 Unauthorized status code is returned. 
    If the user is trying to sign in with an account created by another provider, a 401 Unauthorized status code is returned with information "You cannot log in this way. You must log in via provider_name" (Only for /client/login/v2 endpoint).
    If the server encountered an error while processing the login request, a 500 Internal Server Error status code is returned.
    <br><br>
    <li><b>GET /client/login/google</b></li>
    This endpoint redirects the user to Google's authorization endpoint, allowing them to authenticate with their Google account. 
    The endpoint constructs an authorization URL with the required parameters, including the client ID and redirect URI.
    The user is redirected to this URL, where they can authenticate and grant access to the requested scopes.
    After successful authentication, the user is redirected back to the client application.
    <br><br>
    <li><b>GET /client/login/oauth2/code/google</b></li>
    This endpoint is used to authenticate users via Google OAuth 2.0 protocol. 
    The endpoint receives an authorization code from the Google authorization server and returns a response containing the user's information. It <b>SHOULD NOT BE DIRECTLY ACCESSED!!!</b>
    <br><br>
    <b>Query Parameters:</b>
    <br>
    <ul>
         <li>code (required): a string representing the authorization code returned by the Google authorization server.</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    The endpoint returns a ResponseEntity object that contains a string representing the status of the authentication process.
    If the authentication is successful, the response body contains a message "Successful login via google account." indicating that the user has been logged in successfully.
    If the authentication fails, the response body contains a message "Unsuccessful authentication via google account." indicating that the user was not authenticated.
    Additionally, the endpoint uses a RestTemplate to communicate with the Google API and retrieve the user's information. 
    If the user does not exist in the system, the endpoint also registers the user with the system by creating a UserRegistrationDto object and calling the registerService.registerUser() method. 
    If the user is successfully registered, the endpoint also registers the user with the editorial service by calling the registerService.registerUserClientToEditorial() method.
</ol>

### Registration:
<ol>
    <li><b>POST /client/registration (/editorial/registration)</b></li>
        This endpoint allows users to register an account in the system. 
        The endpoint expects a POST request with a JSON object representing the user registration data in the request body.
    <br><br>
    <b>Request Body:</b>
    <br>
    The request body must contain a JSON object with the following fields:
    <ul>
        <li>username (required): a string representing the user's username.</li>
        <li>password (required): a string representing the user's password.</li>
        <li>email (required): a string representing the user's email address.</li>
        <li>name (required): a string representing the user's first name.</li>
        <li>surname (required): a string representing the user's last name.</li>
        <li>authorityName (required): a string representing the user's authority name (Only for /editorial/registration endpoint).</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    This endpoint returns an HTTP response with a status code and an optional message. 
    If the registration process is successful, it returns a response with a 200 OK status code and the message "Correct registration process." 
    If the email or username already exists in the database, it returns a response with a 409 CONFLICT status code and an error message indicating that the email or username is already taken. 
    If there is any other error during the registration process, such as an error while trying to register the user with an editorial service, it returns a response with the appropriate HTTP status code and an error message explaining the issue.
    <br><br>
    <li><b>POST /client/registration/fe (/editorial/registration/fc)</b></li>
        This endpoint handles user registration from an editorial microservice. It receives a POST request with a JSON payload containing user information. 
        It checks if the email or username already exists in the database, and if not, it registers the user. 
        It also checks if the request header "X-Caller" contains the value "REGISTRATION_FROM_EDITORIAL". 
        If it does not contain this value, the endpoint returns a bad request response. If the value is present, the endpoint returns a success response.
        It can only be accessed by the caller with X-Caller header set to "REGISTRATION_FROM_EDITORIAL" ("REGISTRATION_FROM_CLIENT"). So it <b>SHOULD NOT BE DIRECTLY ACCESSED!!!</b>
    <br><br>
    <b>Request Body:</b>
    <br>
    The request body must contain a JSON object representing the user information. The object should contain the following fields:
    <ul>
        <li>username (required): a string representing the user's username.</li>
        <li>password (required): a string representing the user's password.</li>
        <li>email (required): a string representing the user's email address.</li>
        <li>name (required): a string representing the user's first name.</li>
        <li>surname (required): a string representing the user's last name.</li>
    </ul>
    <br>
    <b>Headers:</b>
    <br>
    <ul>
        <li>X-Caller (required): a string representing the caller's identity. Must be set to "REGISTRATION_FROM_EDITORIAL" ("REGISTRATION_FROM_CLIENT").</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    If the email or username already exists in the database, the endpoint returns a conflict response with an error message. 
    If the "X-Caller" header does not contain the expected value, the endpoint returns a bad request response with an error message. 
    If the registration process is successful, the endpoint returns a success response with a message indicating the success of the registration process.
</ol>

### Articles:
<ol>
    <li><b>GET /client/articles</b></li>
        This endpoint retrieves a list of articles. You can optionally filter the articles by category by providing a <b>category</b> query parameter.
    <br><br>
    <b>Query Parameters:</b>
    <br>
    <ul>
        <li>category (optional): a string representing the category to filter by.</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    The response is a list of article objects represented in JSON format.
    If there are no articles found, <b>a 204 No Content</b> status code is returned.
    <br><br>
    <li><b>GET /client/articles/pages</b></li>
        This endpoint retrieves a paginated list of articles. You can optionally filter the articles by category by providing a category query
        parameter. You can also specify the <b>page</b> and <b>size</b> query parameters
        to control the pagination.
    <br><br>
    <b>Query Parameters:</b>
    <br>
    <ul>
        <li>page (optional): an integer representing the page number (starting from 0).</li>
        <li>size (optional): an integer representing the page size.</li>
        <li>category (optional): a string representing the category to filter by.</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    The response is a paginated list of article objects represented in JSON format.
    If there are no articles found, <b>a 204 No Content</b> status code is returned.
    If there's an error with the query parameters, <b>a 400 Bad Request</b> status code is returned.
    <br><br>
</ol>

### Users:
<ol>
    <li><b>DELETE /client/delete (/editorial/delete)</b></li>
    This endpoint deletes a user from the database. Only users with ADMIN privileges or the owner of the account can delete a user.
    <br><br>
    <b>Query Parameters:</b>
    <br>
    <ul>
    <li>id (required): an integer representing the ID of the user to be deleted.</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    The response is a status code indicating the outcome of the deletion operation. If the deletion was successful, <b>a 200 OK</b> status code is returned. If the user is not authorized to perform the deletion operation, <b>a 403 Forbidden</b> status code is returned. If there's an error with the query parameters, <b>a 400 Bad Request</b> status code is returned. If the server encountered an error while performing the deletion operation, <b>a 500 Internal Server Error</b> status code is returned.
    <br><br>
    <li><b>DELETE /client/delete/fe (/editorial/delete/fc)</b></li>
    This endpoint deletes a user from the database. It can only be accessed by the caller with X-Caller header set to "DELETE_FROM_EDITORIAL". So it <b>SHOULD NOT BE DIRECTLY ACCESSED!!!</b>
    <br><br>
    <b>Query Parameters:</b>
    <br>
    <ul>
        <li>id (required): an integer representing the ID of the user to be deleted.</li>
    </ul>
    <br>
    <b>Headers:</b>
    <br>
    <ul>
        <li>X-Caller (required): a string representing the caller's identity. Must be set to "DELETE_FROM_EDITORIAL" ("DELETE_FROM_CLIENT").</li>
    </ul>
    <br>
    <b>Response:</b>
    <br>
    The response is a status code indicating the outcome of the deletion operation. If the deletion was successful, <b>a 200 OK</b> status code is returned. If there's an error with the query parameters or the caller is not authorized to perform the deletion operation, <b>a 400 Bad Request</b> status code is returned. If the server encountered an error while performing the deletion operation, <b>a 500 Internal Server Error</b> status code is returned.
    <br><br>
    <li><b>PUT /client/edit (/editorial/edit)</b></li>
    This endpoint edits an existing user in the database. Only users with ADMIN privileges or the owner of the account can edit a user.
    <br><br>
    <b>Query Parameters:</b>
    <br>
    <ul>
        <li>id (required): an integer representing the ID of the user to be edited.</li>
    </ul>
    <br>
    <b>Request Body:</b>
    <br>
    The request body must contain a JSON object representing the updated user information. The object should contain the following fields:
    <ul>
        <li>username (required): a string representing the user's username</li>
        <li>name (required): a string representing the user's first name.</li>
        <li>surname (required): a string representing the user's last name.</li>
        <li>email (required): a string representing the user's email address.</li>
        <li>password (required): a string representing the user's new password.</li>
    </ul>
        <br>
        <b>Response:</b>
        <br>
        The response is a status code indicating the outcome of the update operation. If the update was successful, <b>a 200 OK</b> status code is returned. If the user is not authorized to perform the update operation, <b>a 403 Forbidden</b> status code is returned. If there's an error with the query parameters or the request body
    </ol>