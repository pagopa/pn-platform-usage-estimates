echo "### CREATE QUEUES ###"
queues="pn-new-raw-activity-report pn-safe-storage"
for qn in  $( echo $queues | tr " " "\n" ) ; do
    echo creating queue $qn ...
    aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
        sqs create-queue \
        --attributes '{"DelaySeconds":"2"}' \
        --queue-name $qn
    echo ending create queue
done

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
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2023-02-15T10:15:30Z"}, "referenceMonth": {"S": "FEB-2023"}, "totalDigitalNotif": {"N": "141"}, "total890Notif": {"N": "200"}, "totalAnalogNotif": {"N": "242"}, "lastModifiedDate": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2023-01-15T10:15:30Z"}, "referenceMonth": {"S": "GEN-2023"}, "totalDigitalNotif": {"N": "114"}, "total890Notif": {"N": "10"}, "totalAnalogNotif": {"N": "20"}, "lastModifiedDate": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "12345"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2022-10-15T10:15:30Z"}, "referenceMonth": {"S": "DIC-2022"}, "totalDigitalNotif": {"N": "114"}, "total890Notif": {"N": "10"}, "totalAnalogNotif": {"N": "20"}, "lastModifiedDate": {"S": "2023-04-19T10:15:30Z"}, "description": {"S": "description"}, "mailAddress": {"S": "mailAddress"}}'

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name ActivityReportDynamoTable \
    --attribute-definitions \
        AttributeName=paId,AttributeType=S \
        AttributeName=fileKey,AttributeType=S \
    --key-schema \
        AttributeName=paId,KeyType=HASH \
        AttributeName=fileKey,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5


echo "Initialization terminated"