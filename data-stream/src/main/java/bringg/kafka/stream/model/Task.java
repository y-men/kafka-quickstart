package bringg.kafka.stream.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@Slf4j
public class Task {

    /**
     * Create the object from following JSON:
     * {"task_id":4,"merchant_id":1,"user_id":null,"status":0,"team_ids":[],"request_id":"9764a47e-540f-4294-97dd-4b5fec771c43"}
     *
     * @param taskId
     * @param merchantId
     * @param userId
     * @param status
     * @param teamIds
     * @param requestId
     */
    @JsonCreator
    public Task(
            @JsonProperty("task_id") Long taskId,
            @JsonProperty("merchant_id") Long merchantId,
            @JsonProperty("user_id") String userId,
            @JsonProperty("status") Long status,
            @JsonProperty("team_ids") String[] teamIds,
            @JsonProperty("request_id") String requestId,
            @JsonProperty("last_assigned_time") Date lastAssignedTime) {
        this.taskId = taskId;
        this.merchantId = merchantId;
        this.userId = userId;
        this.status = status;
        this.teamIds = teamIds;
        this.requestId = requestId;
        this.lastAssignedTime = lastAssignedTime;
    }

    Long taskId;
    Long merchantId;
    String userId;
    Long status;
    String[] teamIds;
    String requestId;


    Date lastAssignedTime;

// ----------- TODO : Remove, not nedded, 'only for intellisence'


    public Date getLastAssignedTime() {
        return lastAssignedTime;
    }


    public Long getTaskId() {
        return taskId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public String getUserId() {
        return userId;
    }

    public Long getStatus() {
        return status;
    }

    public String[] getTeamIds() {
        return teamIds;
    }

    public String getRequestId() {
        return requestId;
    }
}
