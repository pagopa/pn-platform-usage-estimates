echo " - Create pn-platform TABLES"
aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name EstimateDynamoTable \
    --attribute-definitions \
        AttributeName=paId,AttributeType=S \
        AttributeName=referenceMonth,AttributeType=S \
    --key-schema \
        AttributeName=paId,KeyType=HASH \
        AttributeName=referenceMonth,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2023-02-15T10:15:30Z"}, "referenceMonth": {"S": "MAR-2023"}, "totalDigitalNotif": {"N": "141"}, "total890Notif": {"N": "200"}, "totalAnalogNotif": {"N": "242"}, "lastModifiedTimestamp": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2023-01-15T10:15:30Z"}, "referenceMonth": {"S": "FEBB-2023"}, "totalDigitalNotif": {"N": "114"}, "total890Notif": {"N": "10"}, "totalAnalogNotif": {"N": "20"}, "lastModifiedTimestamp": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2022-10-15T10:15:30Z"}, "referenceMonth": {"S": "NOV-2022"}, "totalDigitalNotif": {"N": "114"}, "total890Notif": {"N": "10"}, "totalAnalogNotif": {"N": "20"}, "lastModifiedTimestamp": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'



echo "Initialization terminated"