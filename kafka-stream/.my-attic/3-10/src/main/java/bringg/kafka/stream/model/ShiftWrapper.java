package bringg.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the shift with additional properties
 * <code>
 *     {"merchant_id":1,
 *     "shift_id":11,
 *     "shift":{"id":11,
 *              "user_id":4,
 *              "start_shift":"2018-09-26T11:18:59.175Z",
 *              "end_shift":null,
 *              "created_at":"2018-09-26T11:18:59.176Z",
 *              "updated_at":"2018-09-26T11:18:59.176Z",
 *              "merchant_id":1,
 *              "device_id":null,
 *              "start_shift_lat":null,
 *              "start_shift_lng":null,
 *              "end_shift_lat":null,
 *              "end_shift_lng":null
 *              },
 *      "lat":null,
 *      "lng":null,
 *      "request_id":"302fe4ef-ec83-47fa-a33f-323207ffecb4"
 *      }
 *
 * </code>
 */
@Data
public class ShiftWrapper {

    @JsonCreator
    public ShiftWrapper(
            @JsonProperty("merchant_id") long merchantId,
            @JsonProperty("shift_id") long shiftId,
            @JsonProperty("shift") Shift shift,
            @JsonProperty("lat") String lat,
            @JsonProperty("lng") String lng,
            @JsonProperty("request_id") String requestId) {
        this.merchantId = merchantId;
        this.shiftId = shiftId;
        this.shift = shift;
        this.lat = lat;
        this.lng = lng;
        this.requestId = requestId;
    }

    long merchantId;
    long shiftId;
    Shift shift;
    String lat;
    String lng;
    String requestId;

// ----------- TODO : Remove, not nedded, 'only for intellisence'

    public long getMerchantId() {
        return merchantId;
    }

    public long getShiftId() {
        return shiftId;
    }

    public Shift getShift() {
        return shift;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getRequestId() {
        return requestId;
    }
}
