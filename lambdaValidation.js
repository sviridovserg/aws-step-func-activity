const AWS = require('aws-sdk');
const docClient = new AWS.DynamoDB.DocumentClient({ region: 'us-west-2'});

exports.handler = (event, context, callback) => {
    console.log("Begin task validation. Input " + JSON.stringify(event))
    docClient.scan({
        TableName: 'pocValidation'
    }, (err, data) => {
        if (err) {
            console.error("Unable to read item. Error JSON:", JSON.stringify(err, null, 2));
            callback(err);
            return;
        }
        let isValid = true;
        const items = data.Items;
        for (var k in items) {
            console.log('Validate term: ' + items[k].text);
            if (event.value.indexOf(items[k].text) >= 0) {
                isValid = false;
                break;
            }
        }
        callback(null, isValid);
        console.log("End task validation")
    })
};