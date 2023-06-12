# ms-evidence-handler

A Java Springboot service within Access to Work (AtW) that will facilitate and manage the conversion and upload of
various evidence types to PDF

## Service Endpoints

### POST /convert

Endpoint to convert a file for a user. It can be a mixture of accepted image types, document types or PDFs

Body:

- `file` - Multipart file
- `userId` - User ID (UUID format)

200:

```json
{
  "uploadedFileKeys": [
    "759b92b0-60a8-43ff-acd5-deb58077d097/5ca72c67-9e62-4311-9a49-3fbe85d80f28"
  ]
}
```

406 - Virus: "VIRUS DETECTED"
406 - Password Protected: "PASSWORD PROTECTED"
415 - Unsupported media type:
If any one of the files are not accepted, no files will be converted or upload they will all be rejected

```txt
Unsupported file type
```

### POST /delete

This endpoint will delete the file based on the provided key Body:

Body:

- `key` - Format is `UUID/UUID` eg. `2e775591-adb5-4fa8-b0cc-1cc467f867bb/20323cb9-be68-49d1-9f91-28cb1c9a12fb`

200 - (no body):
AWS processed request. If there was no file it will return 200 as it was able to process request (as per S3 design)

## Test only

### POST /file-meta

This endpoint will return the meta data for a file in S3

Body:

- `key` - Format is `UUID/UUID` eg. `2e775591-adb5-4fa8-b0cc-1cc467f867bb/20323cb9-be68-49d1-9f91-28cb1c9a12fb`

200:

```json
{
  "userMetadata": {},
  "httpExpiresDate": null,
  "expirationTime": null,
  "expirationTimeRuleId": null,
  "ongoingRestore": null,
  "restoreExpirationTime": null,
  "bucketKeyEnabled": null,
  "versionId": null,
  "etag": "48c1fbc9c50db4c909ac95d92cd3da01",
  "storageClass": null,
  "objectLockMode": null,
  "objectLockRetainUntilDate": null,
  "objectLockLegalHoldStatus": null,
  "contentMD5": null,
  "ssealgorithm": null,
  "ssecustomerAlgorithm": null,
  "ssecustomerKeyMd5": null,
  "requesterCharged": false,
  "rawMetadata": {
    "access-control-allow-headers": "authorization,content-type,content-length,content-md5,cache-control,x-amz-content-sha256,x-amz-date,x-amz-security-token,x-amz-user-agent,x-amz-target,x-amz-acl,x-amz-version-id,x-localstack-target,x-amz-tagging,amz-sdk-invocation-id,amz-sdk-request",
    "access-control-allow-methods": "HEAD,GET,PUT,POST,DELETE,OPTIONS,PATCH",
    "access-control-allow-origin": "*",
    "access-control-expose-headers": "x-amz-version-id",
    "connection": "close",
    "content-length": 1409541,
    "content-type": "application/pdf",
    "date": "Thu, 09 Sep 2021 12:13:45 GMT",
    "etag": "48c1fbc9c50db4c909ac95d92cd3da01",
    "last-modified": "2021-09-08T13:07:59.000+00:00",
    "server": "hypercorn-h11"
  },
  "cacheControl": null,
  "contentDisposition": null,
  "contentLanguage": null,
  "lastModified": "2021-09-08T13:07:59.000+00:00",
  "contentType": "application/pdf",
  "contentLength": 1409541,
  "contentEncoding": null,
  "instanceLength": 1409541,
  "serverSideEncryption": null,
  "archiveStatus": null,
  "sseawsKmsKeyId": null,
  "sseawsKmsEncryptionContext": null,
  "partCount": null,
  "contentRange": null,
  "replicationStatus": null
}
```

404 - Resource no found in S3

## Health check endpoints

This service utilises Spring Boot actuator.

### GET /actuator/health

200:

```json
{
  "status": "UP"
}
```

503:

```json
{
  "status": "DOWN"
}
```

## Run service

### Locally

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=local 
```


Maintainer Team: Bluejay

Contributing file: ../CONTRIBUTING.md