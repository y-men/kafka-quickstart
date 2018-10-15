package bringg.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * Represents the shift:
 * <code>
 * "shift":{"id":11,
 * "user_id":4,
 * "start_shift":"2018-09-26T11:18:59.175Z",
 * "end_shift":null,
 * "created_at":"2018-09-26T11:18:59.176Z",
 * "updated_at":"2018-09-26T11:18:59.176Z",
 * "merchant_id":1,
 * "device_id":null,
 * "start_shift_lat":null,
 * "start_shift_lng":null,
 * "end_shift_lat":null,
 * "end_shift_lng":null
 * },
 * </code>
 */

@Data
public class ShiftDetails {

    @JsonCreator
    public ShiftDetails(
            @JsonProperty("id") long id,
            @JsonProperty("user_id") String userId,
            @JsonProperty("start_shift") Date startShift,
            @JsonProperty("end_shift") Date endShift,
            @JsonProperty("created_at") Date createdAt,
            @JsonProperty("updated_at") Date updatedAt,
            @JsonProperty("merchant_id") long merchantId,
            @JsonProperty("device_id") long deviceId,
            @JsonProperty("start_shift_lat") String startShiftLat,
            @JsonProperty("start_shift_lng") String startShiftLng,
            @JsonProperty("end_shift_lat") String endShiftLat,
            @JsonProperty("end_shift_lng") String endShiftLng
    ) {
        this.id = id;
        this.userId = userId;
        this.startShift = startShift;
        this.endShift = endShift;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.merchantId = merchantId;
        this.deviceId = deviceId;
        this.startShiftLat = startShiftLat;
        this.startShiftLng = startShiftLng;
        this.endShiftLat = endShiftLat;
        this.endShiftLng = endShiftLng;
    }

    long id;
    String userId;
    Date startShift;
    Date endShift;
    Date createdAt;
    Date updatedAt;
    long merchantId;
    long deviceId;
    String startShiftLat;
    String startShiftLng;
    String endShiftLat;
    String endShiftLng;


    //----

    public long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Date getStartShift() {
        return startShift;
    }

    public Date getEndShift() {
        return endShift;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public String getStartShiftLat() {
        return startShiftLat;
    }

    public String getStartShiftLng() {
        return startShiftLng;
    }

    public String getEndShiftLat() {
        return endShiftLat;
    }

    public String getEndShiftLng() {
        return endShiftLng;
    }
}
