package bringg.kafka.stream.domain;

import lombok.Data;

@Data
public class CalcResult {
    public CalcResult(String key, Long value) {
        this.key = key;
        this.value = value;
    }

    String key;
    Long   value;
}
