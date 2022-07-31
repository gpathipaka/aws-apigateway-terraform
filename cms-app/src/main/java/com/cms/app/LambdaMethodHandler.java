package com.cms.app;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

import java.awt.geom.RectangularShape;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author Gangadhar Pathipaka
 */

public class LambdaMethodHandler implements RequestHandler<Map<String, Object>, JSONObject> {

    private DynamoDB dynamoDb;

    private String DYNAMODB_TABLE_NAME = "cms-data-table";
    private Regions REGION = Regions.US_EAST_1;


    @Override
    public JSONObject handleRequest(Map<String, Object> inputStream, Context context) {
        ObjectMapper mapper = new ObjectMapper();
        LambdaLogger logger = context.getLogger();
        try {
            this.initDynamoDbClient();
            String httpMethod = (String) inputStream.get("httpMethod");
            String path = (String) inputStream.get("path");
            LinkedHashMap<String, String> queryStringParameters = (LinkedHashMap<String, String>) inputStream.get("queryStringParameters");
            logger.log(inputStream.toString());
            JSONObject responseObject = new JSONObject();
            if ("GET".equals(httpMethod)) {
                //perform GET operation
                String recordId = queryStringParameters.get("recordId");
                logger.log("recordId = " + recordId);
                responseObject = doGet(logger, recordId);
            } else if ("POST".equals(httpMethod)) {
                //perform GET operation
                CMSData body = mapper.readValue((String) inputStream.get("body"), CMSData.class);
                responseObject = doPost(logger, body);
            } else if ("DELETE".equals(httpMethod)) {
                //perform GET operation

            } else if ("PUT".equals(httpMethod)) {
                //perform GET operation

            }
            logger.log("sending response");
            logger.log(responseObject.toString());
            return responseObject;
        } catch (Exception e) {
            logger.log("Unknow Error: " +e.getMessage());
            e.printStackTrace();
            return createResponseObject("", 500);
        }
    }

    private JSONObject createResponseObject(String response, int statuCode) {
        JSONObject responseJson = new JSONObject();
        responseJson.put("body", response);
        responseJson.put("statusCode", statuCode);
        return responseJson;
    }

    private JSONObject doGet(LambdaLogger logger, String recordId) {
        logger.log("**********************Dynamo DB insert");
        JSONObject res =  new JSONObject();
        try {
            Item item = getAllData(recordId);
            logger.log("**********************Insert is done******");
            if (item == null) {
                res.put("Message", "Item Not found");
                return createResponseObject(res.toString(), 404);
            } else {
                res.put("Item", item);
            }
            return createResponseObject(res.toString(), 200);
        } catch (Exception e) {
            logger.log("**********************Creating failed******");
            res.put("Message", "Failed to Create Recourd");
            return createResponseObject(res.toString(), 500);
        }
    }

    private JSONObject doPost(LambdaLogger logger, CMSData body) {
        logger.log("**********************Dynamo DB insert");
        JSONObject res = new JSONObject();
        try {
            body.setRecordId(UUID.randomUUID().toString());
            persistData(body);
            logger.log("**********************Insert is done******");
            res.put("recordId", body.getRecordId());
            return createResponseObject(res.toString(), 201);
        } catch (Exception e) {
            logger.log("**********************Creating failed******");
            res.put("Message", "Failed to Create Recourd");
            return createResponseObject(res.toString(), 500);
        }
    }

    private PutItemOutcome persistData(CMSData cmsData) throws ConditionalCheckFailedException {
        return this.dynamoDb.getTable(DYNAMODB_TABLE_NAME)
                .putItem(
                        new PutItemSpec().withItem(new Item()
                                .withString("recordId", cmsData.getRecordId())
                                .withString("firstName", cmsData.getFirstName())
                                .withString("lastName", cmsData.getLastName())));
    }

    private Item  getAllData(String recordId) {
        return this.dynamoDb.getTable(DYNAMODB_TABLE_NAME).getItem("recordId", recordId);
    }
    private void initDynamoDbClient() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(REGION));
        this.dynamoDb = new DynamoDB(client);
    }
}
