package bringg.kafka.stream.controllers;

import bringg.kafka.stream.configurations.MultiSinkProcessor;
import bringg.kafka.stream.domain.CalcResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@EnableBinding(MultiSinkProcessor.class)
@RequestMapping("calc")
public class CalcController {
    @Autowired
    StreamsBuilder streamsBuilder;
    //
    @Autowired
    StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Autowired
    QueryableStoreRegistry queryableStoreRegistry;



    @GetMapping(value = "json/{cmd}", produces = "application/json")
    public @ResponseBody
    CalcResult[] getCalcResult(@PathVariable String cmd) throws InterruptedException {
        if (cmd == null || cmd.equals("")) return null;
        List<CalcResult> results = new ArrayList<CalcResult>();
        ReadOnlyKeyValueStore view;
        view = queryableStoreRegistry.getQueryableStoreType("qstore2", QueryableStoreTypes.keyValueStore());

        // Need to understand why this does not work ...
        //view = streamsBuilderFactoryBean.getKafkaStreams().store("qstore",QueryableStoreTypes.keyValueStore() );
        System.out.println("view= " + view);
        view.all().forEachRemaining((entry) -> {
            results.add(new CalcResult((String) ((KeyValue) entry).key, (Long) ((KeyValue) entry).value));
        });
        return results.toArray(new CalcResult[0]);
    }


   // @RequestMapping("/index")
    @GetMapping({"/detail"})
    public String getIndexPage(Model model) throws InterruptedException {
        //log.debug("Getting Index page");
        CalcResult[] calcResults = getCalcResult("all");
        model.addAttribute("results", calcResults);

        return "detail";
    }



    public static <T> T waitUntilStoreIsQueryable(final String storeName,
                                                  final QueryableStoreType<T> queryableStoreType,
                                                  final KafkaStreams streams) throws InterruptedException {
        while (true) {
            try {
                return streams.store(storeName, queryableStoreType);
            } catch (InvalidStateStoreException ignored) {
                // store not yet ready for querying
                Thread.sleep(100);
            }
        }
    }

}
