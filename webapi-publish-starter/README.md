#webapi接口文档

##查询热部署的api列表
地址后缀：/webapi/api

动作：[GET] 

参数：
accessUrl  String 非必填
method   String 非必填
responseClass String 非必填

请求示例：/webapi/api?method=get

返回示例：
```json
[
    {
        "id": "1409320942835912705",
        "returnType": "list",
        "customResponse": [
            {
                "fieldName": "age",
                "fieldType": "java.lang.Integer",
                "column": "age"
            },
            {
                "fieldName": "name",
                "fieldType": "java.lang.String",
                "column": "name"
            },
            {
                "fieldName": "sex",
                "fieldType": "java.lang.String",
                "column": "sex"
            }
        ],
        "requestArgs": [
            {
                "fieldName": "userAge",
                "fieldType": "java.lang.Integer",
                "column": null
            }
        ],
        "method": "get",
        "responseClass": "com.hnup.common.webapi.DTO5e5d4f7bae6047f0a70d4d2ac8d895de",
        "sqlStr": "select sex ,name,age from users where age =  #{userAge}",
        "accessUrl": "/webapi/users"
    }
]

```
##查询注册是javaBean列表
地址后缀：/webapi/javaBean

动作：[GET] 

参数：
beanName  String 非必填

请求示例：/webapi/javaBean

返回示例：
```json
[
    {
        "id": "1408347128190738434",
        "beanName": "com.hnup.common.webapi.DTO70363f45386f42d69927d3dd0c06198b",
        "appKey": "webapi",
        "classBytes": "yv66vgAAADQAIQEAOmNvbS9obnVwL2NvbW1vbi93ZWJhcGkvRFRPNzAzNjNmNDUzODZmNDJkNjk5MjdkM2RkMGMwNjE5OGIHAAEBABBqYXZhL2xhbmcvT2JqZWN0BwADAQAKc2V0VXNlckFnZQEAUShMamF2YS9sYW5nL0ludGVnZXI7KUxjb20vaG51cC9jb21tb24vd2ViYXBpL0RUTzcwMzYzZjQ1Mzg2ZjQyZDY5OTI3ZDNkZDBjMDYxOThiOwEABENvZGUBAAd1c2VyQWdlAQATTGphdmEvbGFuZy9JbnRlZ2VyOwwACAAJCQACAAoBAApnZXRVc2VyQWdlAQAVKClMamF2YS9sYW5nL0ludGVnZXI7AQALc2V0VXNlck5hbWUBAFAoTGphdmEvbGFuZy9TdHJpbmc7KUxjb20vaG51cC9jb21tb24vd2ViYXBpL0RUTzcwMzYzZjQ1Mzg2ZjQyZDY5OTI3ZDNkZDBjMDYxOThiOwEACHVzZXJOYW1lAQASTGphdmEvbGFuZy9TdHJpbmc7DAAQABEJAAIAEgEAC2dldFVzZXJOYW1lAQAUKClMamF2YS9sYW5nL1N0cmluZzsBAApzZXRVc2VyU2V4AQAHdXNlclNleAwAFwARCQACABgBAApnZXRVc2VyU2V4AQAGPGluaXQ+AQADKClWDAAbABwKAAQAHQEAClNvdXJjZUZpbGUBAChEVE83MDM2M2Y0NTM4NmY0MmQ2OTkyN2QzZGQwYzA2MTk4Yi5qYXZhACEAAgAEAAAAAwACAAgACQAAAAIAEAARAAAAAgAXABEAAAAHAAEABQAGAAEABwAAABMAAgACAAAAByortQALKrAAAAAAAAEADAANAAEABwAAABEAAQABAAAABSq0AAuwAAAAAAABAA4ADwABAAcAAAATAAIAAgAAAAcqK7UAEyqwAAAAAAABABQAFQABAAcAAAARAAEAAQAAAAUqtAATsAAAAAAAAQAWAA8AAQAHAAAAEwACAAIAAAAHKiu1ABkqsAAAAAAAAQAaABUAAQAHAAAAEQABAAEAAAAFKrQAGbAAAAAAAAEAGwAcAAEABwAAABEAAQABAAAABSq3AB6xAAAAAAABAB8AAAACACA=",
        "fields": [
            {
                "fieldName": "userAge",
                "fieldType": "java.lang.Integer",
                "column": "age"
            },
            {
                "fieldName": "userName",
                "fieldType": "java.lang.String",
                "column": "name"
            },
            {
                "fieldName": "userSex",
                "fieldType": "java.lang.String",
                "column": "sex"
            }
        ]
    }
]
```

##sql类型的api发布

地址后缀：/webapi/sqlApi

动作：[PUT] 

参数：WebApiVO

字段说明：

WebApiVO
|   字段   |   类型   |  是否必填 | 描述 |
|  ----    | ----   |  -----  |  ----|
|  apiPath   | String  |  是 |     |
|  sqlStr  | String | 是 |     |
| requestArgs  | CustomFieldVO |  是 | 定义入参的类型，与sqlStr中的占位符变量名一致|
| customResponse | CustomFieldVO| 否 | 定义返回时候的DTO，与sqlStr中select字段名称一致，默认返回类型为Map|
| returnType | String| 否 |定义返回的对象是long、object、list，默认list|
| method|String|否|动作get、post、put，默认是get|

CustomFieldVO
|   字段   |   类型   |  是否必填 | 描述 |
|  ----    | ----   |  -----  |  ----|
|  fieldName   | String  |  是 |     |
|  fieldType  | String | 是 |  类型的全限定名称 例如java.lang.Integer|
|  column    | String  |  否 |  主要是指数据库中的字段名称  例如select的字段 |

请求示例：
```json
{
    "apiPath": "users",    
    "sqlStr": "select sex ,name,age from users where age in(#{userAge})",
    "requestArgs": [
        {
            "fieldName": "userAge",
            "fieldType": "java.lang.Integer"
        }
    ],
    "customResponse": [
        {
            "fieldName": "age",
            "fieldType": "java.lang.Integer",
            "column": "age"
        },
        {
            "fieldName": "name",
            "fieldType": "java.lang.String",
            "column": "name"
        },
        {
            "fieldName": "sex",
            "fieldType": "java.lang.String",
            "column": "sex"
        }
    ],
    "returnType":"list",
    "method": "post"
}
```
返回示例：
accessUrl：表示热部署的api程序请求后缀
```json
{
    "id": null,
    "returnType": "list",
    "customResponse": [
        {
            "fieldName": "age",
            "fieldType": "java.lang.Integer",
            "column": "age"
        },
        {
            "fieldName": "name",
            "fieldType": "java.lang.String",
            "column": "name"
        },
        {
            "fieldName": "sex",
            "fieldType": "java.lang.String",
            "column": "sex"
        }
    ],
    "requestArgs": [
        {
            "fieldName": "userAge",
            "fieldType": "java.lang.Integer",
            "column": null
        }
    ],
    "method": "get",
    "responseClass": "com.hnup.common.webapi.DTO6b4a93375f714162857525e0f2a1f4d6",
    "sqlStr": "select sex ,name,age from users where age =  #{userAge}",
    "accessUrl": "/webapi/users"
}
```

##热部署的api，例如上述示例发布的api

地址后缀：/webapi/users?userAge=23

动作：[GET] 

参数：userAge

返回示例：
```json
[
    {
        "age": 24,
        "name": "pandau",
        "sex": "m"
    }
]
```

##注册javaBean
地址后缀：/webapi/javaBean

动作：[PUT] 

参数：RegisterVO

字段说明：

WebApiVO
|   字段   |   类型   |  是否必填 | 描述 |
|  ----    | ----   |  -----  |  ----|
|  beanName   | String  |  是 |  bean名称推荐首字母大写 |
| fields  | CustomFieldVO |  是 | 定义类属性|
| methods | CustomMethodVO| 否 | 定义额外的类方法，默认已有getter，setter|

CustomFieldVO
|   字段   |   类型   |  是否必填 | 描述 |
|  ----    | ----   |  -----  |  ----|
|  fieldName   | String  |  是 |     |
|  fieldType  | String | 是 |  类型的全限定名称 例如java.lang.Integer|
|  column    | String  |  否 |  主要是指数据库中的字段名称  例如select的字段 |

CustomMethodVO
|   字段   |   类型   |  是否必填 | 描述 |
|  ----    | ----   |  -----  |  ----|
|  methodName   | String  |  是 |   方法名  |
|  returnType  | String | 是 |  返回类型，需要全限定名称例如java.lang.Integer|
|  argsType    | List<String>  |  否 | 参数列表，需要全限定名称例如java.lang.Integer|
| body |String| 否|方法体，例如“{return $1 ;}” $0代表第一个参数 this $1代表第二个....

请求示例：
```json
{
    "beanName": "UserInfo",
    "fields": [
        {
            "fieldName": "name",
            "fieldType": "java.lang.String"
        },
        {
            "fieldName": "age",
            "fieldType": "int"
        },
        {
            "fieldName": "sex",
            "fieldType": "java.lang.String"
        }
    ],
    "methods": []
}
```
返回示例：
```json
{
    "id": "1409326648565755906",
    "beanName": "com.hnup.common.webapi.UserInfo",
    "appKey": "webapi",
    "classBytes": "yv66vgAAADQAIQEAH2NvbS9obnVwL2NvbW1vbi93ZWJhcGkvVXNlckluZm8HAAEBABBqYXZhL2xhbmcvT2JqZWN0BwADAQAHc2V0TmFtZQEANShMamF2YS9sYW5nL1N0cmluZzspTGNvbS9obnVwL2NvbW1vbi93ZWJhcGkvVXNlckluZm87AQAEQ29kZQEABG5hbWUBABJMamF2YS9sYW5nL1N0cmluZzsMAAgACQkAAgAKAQAHZ2V0TmFtZQEAFCgpTGphdmEvbGFuZy9TdHJpbmc7AQAGc2V0QWdlAQAkKEkpTGNvbS9obnVwL2NvbW1vbi93ZWJhcGkvVXNlckluZm87AQADYWdlAQABSQwAEAARCQACABIBAAZnZXRBZ2UBAAMoKUkBAAZzZXRTZXgBAANzZXgMABcACQkAAgAYAQAGZ2V0U2V4AQAGPGluaXQ+AQADKClWDAAbABwKAAQAHQEAClNvdXJjZUZpbGUBAA1Vc2VySW5mby5qYXZhACEAAgAEAAAAAwACAAgACQAAAAIAEAARAAAAAgAXAAkAAAAHAAEABQAGAAEABwAAABMAAgACAAAAByortQALKrAAAAAAAAEADAANAAEABwAAABEAAQABAAAABSq0AAuwAAAAAAABAA4ADwABAAcAAAATAAIAAgAAAAcqG7UAEyqwAAAAAAABABQAFQABAAcAAAARAAEAAQAAAAUqtAATrAAAAAAAAQAWAAYAAQAHAAAAEwACAAIAAAAHKiu1ABkqsAAAAAAAAQAaAA0AAQAHAAAAEQABAAEAAAAFKrQAGbAAAAAAAAEAGwAcAAEABwAAABEAAQABAAAABSq3AB6xAAAAAAABAB8AAAACACA=",
    "fieldStr": "[{\"fieldName\":\"name\",\"fieldType\":\"java.lang.String\",\"column\":null},{\"fieldName\":\"age\",\"fieldType\":\"int\",\"column\":null},{\"fieldName\":\"sex\",\"fieldType\":\"java.lang.String\",\"column\":null}]"
}
```
