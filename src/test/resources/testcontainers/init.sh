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

echo "### CREATE BUCKETS ###"
buckets="local-doc-bucket local-legal-bucket"
for buck in  $( echo $buckets | tr " " "\n" ) ; do
  echo creating bucket $buck ...
  aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
      s3 mb s3://$buck
  aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
      s3api put-bucket-versioning \
      --bucket $buck \
      --versioning-configuration Status=Enabled
done

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

echo " - Create ActivityReportDynamoTable"

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name ActivityReportDynamoTable \
    --attribute-definitions \
        AttributeName=paId,AttributeType=S \
        AttributeName=referenceMonth,AttributeType=S \
		    AttributeName=reportKey,AttributeType=S \
    --key-schema \
        AttributeName=paId,KeyType=HASH \
        AttributeName=reportKey,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5 \
	--global-secondary-indexes \
    "[
        {
            \"IndexName\": \"INDEX_PA_REF_MONTH\",
            \"KeySchema\": [{\"AttributeName\":\"paId\",\"KeyType\":\"HASH\"}, {\"AttributeName\":\"referenceMonth\",\"KeyType\":\"RANGE\"}],
            \"Projection\":{
                \"ProjectionType\":\"ALL\"
            },
            \"ProvisionedThroughput\": {
                \"ReadCapacityUnits\": 5,
                \"WriteCapacityUnits\": 5
            }
        }
	]"

echo " - Inserting element on db"

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name ActivityReportDynamoTable  \
    --item '{"paId": {"S": "cc1c6a8e-5967-42c6-9d83-bfb12ba1665a"}, "referenceMonth": {"S": "DIC-2022"}, "reportKey": {"S": "abc12345"}, "statusReport": {"S": "READY"}, "bucketName": {"S": "bucketName"}, "reportZipKey": {"S": "fileZipKey"}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name ActivityReportDynamoTable  \
    --item '{"paId": {"S": "cc1c6a8e-5967-42c6-9d83-bfb12ba1665a"}, "referenceMonth": {"S": "GEN-2023"}, "reportKey": {"S": "abc1234"}, "statusReport": {"S": "RAW"}, "bucketName": {"S": "bucketName"}, "reportZipKey": {"S": "fileZipKey"}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name ActivityReportDynamoTable  \
    --item '{"paId": {"S": "cc1c6a8e-5967-42c6-9d83-bfb12ba1665a"}, "referenceMonth": {"S": "FEB-2023"}, "reportKey": {"S": "abc123456"}, "statusReport": {"S": "RAW"}, "bucketName": {"S": "bucketName"}, "reportZipKey": {"S": "fileZipKey"}}'


echo "Initialization terminated"