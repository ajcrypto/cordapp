package com.template.webserver;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.template.model.StudentBO;
import com.template.states.StudentState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.NodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

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

    public StudentState convertToStudentState(StudentBO studentBO, AbstractParty S1Party,
                                              AbstractParty S2Party) {

        return new StudentState(
                studentBO.getName(),studentBO.getAddress(),
                studentBO.getGender(),studentBO.getHobby(),
                S1Party,S2Party);
    }

}
