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

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name PublicAdministrationDynamoTable \
    --attribute-definitions \
        AttributeName=paId,AttributeType=S \
        AttributeName=referenceMonth,AttributeType=S \
        AttributeName=paName,AttributeType=S \
    --key-schema \
        AttributeName=paId,KeyType=HASH \
        AttributeName=referenceMonth,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
	--global-secondary-indexes \
    "[
        {
            \"IndexName\": \"pa-name-index\",
            \"KeySchema\": [{\"AttributeName\":\"paName\",\"KeyType\":\"HASH\"}],
            \"Projection\":{
                \"ProjectionType\":\"ALL\"
            },
            \"ProvisionedThroughput\": {
                \"ReadCapacityUnits\": 5,
                \"WriteCapacityUnits\": 5
            }
        }
	]"
aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name BillingDynamoTable \
    --attribute-definitions \
        AttributeName=paId,AttributeType=S \
		AttributeName=referenceYear,AttributeType=S \
    --key-schema \
        AttributeName=paId,KeyType=HASH \
		AttributeName=referenceYear,KeyType=SORT \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5


aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "203945"}, "status": {"S": "ENDED"}, "deadlineDate": {"S": "2023-01-15T10:15:30Z"}, "referenceMonth": {"S": "Febbraio 2023"}, "totalDigitalNotif": {"N": "40"}, "totalPaper890Notif": {"N": "20"}, "totalPaperNationalNotif": {"N": "10"}, "totalPaperInternationalNotif": {"N": "50"}, "lastModifiedTimestamp": {"S": "2023-04-13T10:15:30Z"}, "sdiCode": {"S": "12849aedc"}, "description": {"S": "descrizione"}, "mailAddress": {"S": "mail@address1.com"}, "splitPayment": {"BOOL": true}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "139765"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2023-02-15T10:15:30Z"}, "referenceMonth": {"S": "Marzo 2023"}, "totalDigitalNotif": {"N": "50"}, "totalPaper890Notif": {"N": "10"}, "totalPaperNationalNotif": {"N": "20"}, "totalPaperInternationalNotif": {"N": "40"}, "lastModifiedTimestamp": {"S": "2023-03-31T10:15:30Z"}, "sdiCode": {"S": "12849abdc"}, "description": {"S": "descrizione"}, "mailAddress": {"S": "mail@address.com"}, "splitPayment": {"BOOL": true}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "299765"}, "status": {"S": "VALIDATED"}, "deadlineDate": {"S": "2023-05-01T10:15:30Z"}, "referenceMonth": {"S": "Maggio 2023"}, "totalDigitalNotif": {"N": "50"}, "totalPaper890Notif": {"N": "10"}, "totalPaperNationalNotif": {"N": "20"}, "totalPaperInternationalNotif": {"N": "40"}, "lastModifiedTimestamp": {"S": "2023-03-31T10:15:30Z"}, "sdiCode": {"S": "12849abdc"}, "description": {"S": "descrizione"}, "mailAddress": {"S": "mail@address.com"}, "splitPayment": {"BOOL": true}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "389765"}, "status": {"S": "IN_PROGRESS"}, "deadlineDate": {"S": "2023-06-01T10:15:30Z"}, "referenceMonth": {"S": "Giugno 2023"}, "totalDigitalNotif": {"N": "50"}, "totalPaper890Notif": {"N": "10"}, "totalPaperNationalNotif": {"N": "20"}, "totalPaperInternationalNotif": {"N": "40"}, "lastModifiedTimestamp": {"S": "2023-03-31T10:15:30Z"}, "sdiCode": {"S": "12849abdc"}, "description": {"S": "descrizione"}, "mailAddress": {"S": "mail@address.com"}, "splitPayment": {"BOOL": true}}'

aws  --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb put-item \
    --table-name EstimateDynamoTable  \
    --item '{"paId": {"S": "489765"}, "status": {"S": "IN_PROGRESS"}, "deadlineDate": {"S": "2023-07-01T10:15:30Z"}, "referenceMonth": {"S": "Luglio 2023"}, "totalDigitalNotif": {"N": null}, "totalPaper890Notif": {"N": null}, "totalPaperNationalNotif": {"N": null}, "totalPaperInternationalNotif": {"N": null}, "lastModifiedTimestamp": {"S": "2023-03-31T10:15:30Z"}, "sdiCode": {"S": null}, "description": {"S": "descrizione"}, "mailAddress": {"S": null}, "splitPayment": {"BOOL": null}}'




echo "Initialization terminated"