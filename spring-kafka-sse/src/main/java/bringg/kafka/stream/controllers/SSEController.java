package bringg.kafka.stream.controllers;

import bringg.kafka.stream.domain.CalcResult;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.thymeleaf.spring5.context.webflux.IReactiveSSEDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("sse")
public class SSEController {

    @Autowired
    KafkaReceiver kafkaReceiver;


    @GetMapping({"/index", "/"})
    public String getDynamicIndexPage(Model model) throws InterruptedException {
        return "index";
    }

    //@GetMapping("/events")
    @GetMapping(value = "stream/{cmd}", produces = "application/json")
    public SseEmitter getFluxData(@PathVariable String cmd){
        if (cmd == null || cmd.equals("")) return null;
        Flux<CalcResult> kafkaFlux = kafkaReceiver.receive();
//        kafkaReceiver.
        SseEmitter emitter = new SseEmitter();

        kafkaFlux.doOnNext( r ->{
            try {
                emitter.send(">>> " + r.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
            emitter.complete();
        });

        return emitter;
    }


//    @GetMapping(path = {"/events"}, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
//    public String getDynamicPage(Model model) throws InterruptedException {
//        Flux<CalcResult> kafkaFlux = kafkaReceiver.receive();
//        kafkaFlux.log().doOnNext( calcResult ->  model.addAttribute("data",calcResult) );
////        IReactiveSSEDataDriverContextVariable dataDriver =
////                new ReactiveDataDriverContextVariable(kafkaFlux, 1, 1);
////        model.addAttribute("data", dataDriver);
//        return "events1";
//    }

//    @GetMapping(path = {"/events"},produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
//    public Flux getSimpleFlux(){
//        Flux<CalcResult> kafkaFlux = kafkaReceiver.receive();
//        return kafkaFlux.log().doOnNext( r -> System.out.println("!!!!!!!!" +r)).map( CalcResult::getKey);
//    }

    private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    //@GetMapping("/events")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter();
        nonBlockingService.execute(() -> {
            try {
                emitter.send("/sse" + " @ " + new Date());
                // we could send more events
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @Autowired
    StreamsBuilder streamsBuilder;
    //
    @Autowired
    StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    // @GetMapping("/events")
    public SseEmitter handleKafkaStreamSSE() {
        KStream<String, Long> stream = streamsBuilder.stream("qstore2");
        SseEmitter emitter = new SseEmitter();
        nonBlockingService.execute(() -> {
            try {
                //  KStream<String, Long> stream =

                stream.peek((k, v) -> {
                    try {
                        emitter.send("k: " + k + "v: " + v);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @Autowired
    QueryableStoreRegistry queryableStoreRegistry;

//    @GetMapping("/events")
    public SseEmitter handleKafkaInteractiveQuerySSE() {
        KStream<String, Long> stream = streamsBuilder.stream("qstore2");
        SseEmitter emitter = new SseEmitter();
        ReadOnlyKeyValueStore view;
        view = queryableStoreRegistry.getQueryableStoreType("qstore2", QueryableStoreTypes.keyValueStore());

        nonBlockingService.execute(() -> {
            List<CalcResult> results = new ArrayList<CalcResult>();
            System.out.println("view= " + view);
            view.all().forEachRemaining((entry) -> {
                CalcResult calcResult = new CalcResult((String) ((KeyValue) entry).key, (Long) ((KeyValue) entry).value);
                try {
                    emitter.send(" >>>> " + calcResult.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });


            emitter.complete();
        });
        return emitter;
    }

}


