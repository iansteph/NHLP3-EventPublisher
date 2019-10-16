package iansteph.nhl.eventpublisher.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="NhlPlayByPlayProcessingAggregate")
public class NhlPlayByPlayProcessingItem {

    private String compositeGameId;
    private String lastProcessedTimeStamp;
    private int lastProcessedEventIndex;
    private boolean isIntermission;
    private boolean hasGameEnded;

    @DynamoDBHashKey
    public String getCompositeGameId() { return compositeGameId; }
    public void setCompositeGameId(final String compositeGameId) { this.compositeGameId = compositeGameId; }

    @DynamoDBAttribute
    public String getLastProcessedTimeStamp() { return lastProcessedTimeStamp; }
    public void setLastProcessedTimeStamp(final String lastProcessedTimeStamp) { this.lastProcessedTimeStamp = lastProcessedTimeStamp; }

    @DynamoDBAttribute
    public int getLastProcessedEventIndex() { return lastProcessedEventIndex; }
    public void setLastProcessedEventIndex(final int lastProcessedEventIndex) { this.lastProcessedEventIndex = lastProcessedEventIndex; }

    @DynamoDBAttribute
    public boolean isIntermission() { return isIntermission; }
    public void setIsIntermission(final boolean isIntermission) { this.isIntermission = isIntermission; }

    @DynamoDBAttribute
    public boolean hasGameEnded() { return hasGameEnded; }
    public void  setHasGameEnded(final boolean hasGameEnded) { this.hasGameEnded = hasGameEnded; }

    public String toString() {
        return String.format("NhlPlayByPlayProcessingItem(compositeGameId=%s,lastProcessedTimeStamp=%s,lastProcessedEventIndex=%s," +
                "isIntermission=%s,hasGameEnded=%s)", compositeGameId, lastProcessedTimeStamp, lastProcessedEventIndex, isIntermission,
                hasGameEnded);
    }
}