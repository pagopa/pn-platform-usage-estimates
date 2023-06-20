echo " - Create EstimateDynamoTable"

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

echo " - Create ProfilationDynamoTable"

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name ProfilationDynamoTable \
    --attribute-definitions \
        AttributeName=paId,AttributeType=S \
        AttributeName=referenceYear,AttributeType=S \
    --key-schema \
        AttributeName=paId,KeyType=HASH \
        AttributeName=referenceYear,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

echo " - Inserting element on db"

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2023-02-15T10:15:30Z"}, "referenceMonth": {"S": "FEB-2023"}, "totalDigitalNotif": {"N": "141"}, "total890Notif": {"N": "200"}, "totalAnalogNotif": {"N": "242"}, "lastModifiedDate": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2023-01-15T10:15:30Z"}, "referenceMonth": {"S": "GEN-2023"}, "totalDigitalNotif": {"N": "114"}, "total890Notif": {"N": "10"}, "totalAnalogNotif": {"N": "20"}, "lastModifiedDate": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2022-10-15T10:15:30Z"}, "referenceMonth": {"S": "DIC-2022"}, "totalDigitalNotif": {"N": "114"}, "total890Notif": {"N": "10"}, "totalAnalogNotif": {"N": "20"}, "lastModifiedDate": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'

echo "Initialization terminated"