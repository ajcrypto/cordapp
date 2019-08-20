//package com.template.webserver;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableMap;
//import net.corda.core.messaging.CordaRPCOps;
//import net.corda.webserver.services.WebServerPluginRegistry;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//
//public class WebPlugin implements WebServerPluginRegistry {
//
//    private final List<Function<CordaRPCOps, ?>> webApis = ImmutableList.of(Controller::);
//
//    private final Map<String, String> staticServeDirs = ImmutableMap.of("cordapp-template-java",getClass().getClassLoader().getResource("static").toExternalForm());
//
//    @NotNull
//    @Override
//    public Map<String, String> getStaticServeDirs() {
//        return staticServeDirs;
//    }
//
//    @NotNull
//    @Override
//    public List<Function<CordaRPCOps, ? extends Object>> getWebApis() {
//        return webApis;
//    }
//
//    @Override
//    public void customizeJSONSerialization(@NotNull ObjectMapper om) {
//
//    }
//}
