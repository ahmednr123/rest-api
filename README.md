# Rest API

Rest API to store/get users, their applications and the application activities.
The users can have multiple applications under them and each application can have multiple activities.

# Table of Contents
1. [Introduction](#introduction)
2. [Authentication](#authentication)
    1. [JWT Token Usage](#jwt-token-usage)
3. [Data schema](#data-schema)
4. [Get list of data](#get-list-of-data)
    1. [Pagination](#pagination)
    1. [Example](#example)
5. [Get data](#get-data)
6. [Add data](#add-data)
7. [Update data](#update-data)
8. [Delete data](#delete-data)

## Introduction

The response data is always going to be of type `application/json`
While sending data in the body section, the `Content-Type` must be set to `application/json`.

Base URL: `https://localhost:8080/RestAPI`

## Authentication
Method|API|Body
---|---|---
`POST`|`/auth`|`{username, password}`

Success
```json
{
    "success": {
        "message": "Authorization successful",
        "token": "[jwt-token]"
    }
}
```

Error
```json
{
    "error": {
        "message": "Authorization failed",
        "code": 401
    }
}
```

### JWT Token Usage

The JWT token must be added to the header with header name `authorization` and `token` as the value of the header.

## Data schema
Type|Required fields|Schema
---|---|---
**User**|-|{**name** *[string]*, **age** *[int]*, **about** *[string]*}
**App**|{user-id}|{**name** *[string]*, **description** *[string]*}
**Activities**|{user-id}, {app-id}|{**name** *[string]*, **description** *[string]*}

## Get list of data
Method|API|Response
---|---|---
`GET`|`/users`|Get list of all users
`GET`|`/users/{user-id}/apps`|Get list of all apps
`GET`|`/users/{user-id}/apps/{app-id}/activities`|Get list of all activities

Response 
```json
{
    "list": [
        {
            "name": "Activity 1",
            "description": "This activity has been updated",
            "id": 2
        },
        {
            "name": "Activity 23",
            "description": "Do something 23 times",
            "id": 3
        },
        {
            "name": "Dummy activity",
            "description": "Dummy activity about to be deleted",
            "id": 5
        }
    ]
}
```
### Pagination
The api accepts two query parameters for collections.

Query Parameter | Description | Default value
---|---|---
offset|Offset to document record for pagination|0
limit|Limit the number of documents to show per query|10

If there are more records in the list the reply json is going to contain a `next_offset` key which can be used to access the next document records.

### Example 

Method|API|Response
---|---|---
`GET`|`/users?offset=3&limit=3`|Get list of all users

Response 
```json
{
    "next_offset": 6,
    "list": [
        {
            "name": "Chandan Rana",
            "about": "From Jharkand, Software Developer",
            "id": 4,
            "age": 21
        },
        {
            "name": "Ravi Shankar",
            "about": "From Chennai, Software Developer",
            "id": 5,
            "age": 22
        },
        {
            "name": "Dania Fiaz",
            "about": "From Bangalore, Software Developer",
            "id": 6,
            "age": 22
        }
    ]
}
```

## Get data
Method|API|Response
---|---|---
`GET`|`/users/{user-id}`|Get user data
`GET`|`/users/{user-id}/apps/{app-id}`|Get app data
`GET`|`/users/{user-id}/apps/{app-id}/activities/{activity-id}`|Get activity data

The `doc` key contains the document requested.

Response 
```json
{
    "doc" : {
        "name": "Chandan Rana",
        "about": "From Jharkand, Software Developer",
        "id": 4,
        "age": 21
    }
}
```

If the document is not found it responds with an error object that contains a message and a error code, which in this case will be `404`.

Error
```json
{
    "error": {
        "message": "User not found",
        "code": 404
    }
}
```

## Add data
Requires `authorization` header with `token` as the value.

Method|API|Body
---|---|---
`POST`|`/users`|`{name, age, about}`
`POST`|`/users/{user-id}/apps`|`{name, description}` 
`POST`|`/users/{user-id}/apps/{app-id}/activities`|`{name, description}`

Once the data is added it returns with a success object which contains the `id` of the data document that got added.

Success
```json
{
    "success": {
        "id": 14,
        "message": "User added successfully"
    }
}
```

If the data schema sent in the body is wrong. The api will respond with a error object with error code `400` and the valid schema.

Error
```json
{
    "error": {
        "message": "Wrong user data schema",
        "code": 400,
        "schema": "{name[STRING], age[INT], about[STRING]}"
    }
}
```

## Update data
Requires `authorization` header with `token` as the value.

Method|API|Body
---|---|---
`PUT`|`/users/{user-id}`|`{name, age, about}`
`PUT`|`/users/{user-id}/apps/{app-id}`|`{name, description}` 
`PUT`|`/users/{user-id}/apps/{app-id}/activities/{activity-id}`|`{name, description}`

The body must contain at least one field of the document to update.

Success
```json
{
    "success": {
        "message": "User updated successfully"
    }
}
```

If the fields passed in the body are wrong or the body is left empty, the api responds with a error object, with error code `400` and valid schema.

Error
```json
{
    "error": {
        "message": "Wrong user update schema. At least one field must be present.",
        "code": 400,
        "schema": "{name[STRING], age[INT], about[STRING]}"
    }
}
```

## Delete data
Requires `authorization` header with `token` as the value.

Method|API
---|---
`DELETE`|`/users/{user-id}`
`DELTE`|`/users/{user-id}/apps/{app-id}`
`DELETE`|`/users/{user-id}/apps/{app-id}/activities/{activity-id}`

Success
```json
{
    "success": {
        "message": "User deleted successfully"
    }
}
```

If the document is not found, it sends an error object with `404`.

Error
```json
{
    "error": {
        "message": "User not found",
        "code": 404
    }
}
```