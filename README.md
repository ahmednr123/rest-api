# Rest API

The response data is always going to be of type `application/json`
While sending data in the body section, the `Content-Type` must be set to `application/json`.

Base URL: `https://localhost:8080/RestAPI`

# Table of Contents
1. [Authentication](#authentication)
2. [Data schema](#data-schema)
3. [Get list of data](#get-list-of-data)
    1. [Pagination](#pagination)
    1. [Example](#example)
4. [Get data](#get-data)

## Authentication
Method|API|Body
---|---|---
`POST`|`/auth`|`{username, password}`

On Success
```json
{
    "error": {
        "message": "Authorization failed",
        "code": 401
    }
}
```

On Error
```json
{
    "error": {
        "message": "Authorization failed",
        "code": 401
    }
}
```

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

If there are more records in the list the reply json is going to contain a `next_offset` key which can be used to access the next document records

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