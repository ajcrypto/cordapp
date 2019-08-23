package com.template.webserver;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.template.encryption.SymmetricCrypto;
import com.template.model.StudentBO;
import com.template.states.StudentState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.NodeInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class CommonController {

    @Autowired
    protected RPConnector connector;

    @Autowired
    protected ObjectMapper mapper;

    private AbstractParty getPartyFromFullName(String partyName){
        CordaX500Name x500Name = CordaX500Name.parse(partyName);
        return connector.getRPCops().wellKnownPartyFromX500Name(x500Name);
    }


    @GetMapping("sayHello")
    public String sayHello() {
        return "Hello! from " + connector.getHost();
    }

    @GetMapping("/allNodes")
    public Map<String, List<CordaX500Name>> getAllNodes() {
        List<NodeInfo> nodeInfoSnapshot = connector.getRPCops().networkMapSnapshot();
        return ImmutableMap.of("allnodes",
                nodeInfoSnapshot.stream().map(node -> node.getLegalIdentities().get(0).getName()).filter(
                        name -> !name.equals(connector.getRPCops().nodeInfo().getLegalIdentities().get(0).getName()))
                        .collect(toList()));
    }

    @GetMapping("me")
    public Map<String, CordaX500Name> getMyIdentity() {
        return ImmutableMap.of("ME", connector.getRPCops().nodeInfo().getLegalIdentities().get(0).getName());
    }


    public StudentState convertToStudentState(StudentBO studentBO, String secretKey, AbstractParty S1Party,
                                              AbstractParty S2Party) {
        return new StudentState(
                SymmetricCrypto.encrypt(studentBO.getName(),secretKey),
                SymmetricCrypto.encrypt(studentBO.getAddress(),secretKey),
                SymmetricCrypto.encrypt(studentBO.getGender(),secretKey),
                SymmetricCrypto.encrypt(studentBO.getHobby(), secretKey),
                S1Party,S2Party);
    }

    public List<StateAndRef<StudentState>> decryptToStudentState(List<StateAndRef<StudentState>> states, String secretKey){

        for(int i=0;i<states.size();i++){
            System.out.println(SymmetricCrypto.decrypt(states.get(i).getState().getData().getName(),secretKey));
            System.out.println(SymmetricCrypto.decrypt(states.get(i).getState().getData().getAddress(),secretKey));
            System.out.println(SymmetricCrypto.decrypt(states.get(i).getState().getData().getGender(),secretKey));
            System.out.println(SymmetricCrypto.decrypt(states.get(i).getState().getData().getHobby(),secretKey));
        }

//        JSONArray jsonarray = null;
//        try {
//            jsonarray = new JSONArray(mapper.writeValueAsString(states));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        for (int i = 0; i < jsonarray.length(); i++) {
//            JSONObject jsonobject = jsonarray.getJSONObject(i);
//            String name = jsonobject.getString("name");
//            String url = jsonobject.getString("url");
//        }
//////        try {
////            System.out.println("Start:");
////            for(int i=0;i<states.size();i++){
////                System.out.println(" i = "+i);
////                System.out.println(mapper.writeValueAsString(states.get(i)));
////            }
////        } catch (JsonProcessingException e) {
////            e.printStackTrace();
////        }
//
//
//        try {
//            List<StateAndRef> jsonNode = mapper.readValue(mapper.writeValueAsString(states), new StateAndRef<List<StateAndRef>>(){});
//
////            JsonNode jsonNode = mapper.readValue(mapper.writeValueAsString(states), JsonNode.class);
//
//
//            JsonNode brandNode = jsonNode.get(0).get("state").get("data").get("name");
//            String brand = brandNode.asText();
//            System.out.println("brand = " + brand);
//
//            JsonNode doorsNode = jsonNode.get(0).get("state").get("data").get("address");
//            String doors = doorsNode.asText();
//            System.out.println("doors = " + doors);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return states;
    }

    }
