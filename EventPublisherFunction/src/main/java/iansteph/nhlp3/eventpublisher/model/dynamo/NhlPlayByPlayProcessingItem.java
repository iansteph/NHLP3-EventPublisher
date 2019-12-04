package iansteph.nhlp3.eventpublisher.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;

import java.util.Objects;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;
import static java.lang.String.format;

@DynamoDBTable(tableName="NhlPlayByPlayProcessingAggregate")
public class NhlPlayByPlayProcessingItem {

    private String compositeGameId;
    private String lastProcessedTimeStamp;
    private int lastProcessedEventIndex;
    private boolean inIntermission;

    public NhlPlayByPlayProcessingItem() {}

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
    @DynamoDBTyped(DynamoDBAttributeType.BOOL)
    public Boolean inIntermission() { return inIntermission; }
    public void setInIntermission(final boolean isIntermission) { this.inIntermission = isIntermission; }

    public String toString() {
        return format("NhlPlayByPlayProcessingItem(compositeGameId=%s,lastProcessedTimeStamp=%s,lastProcessedEventIndex=%s," +
                "inIntermission=%s)", compositeGameId, lastProcessedTimeStamp, lastProcessedEventIndex, inIntermission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhlPlayByPlayProcessingItem that = (NhlPlayByPlayProcessingItem) o;
        return lastProcessedEventIndex == that.lastProcessedEventIndex &&
                inIntermission == that.inIntermission &&
                Objects.equals(compositeGameId, that.compositeGameId) &&
                Objects.equals(lastProcessedTimeStamp, that.lastProcessedTimeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeGameId, lastProcessedTimeStamp, lastProcessedEventIndex, inIntermission);
    }
}