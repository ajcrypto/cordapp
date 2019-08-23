package com.template.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.contracts.StudentContract;
import com.template.flows.StudentInitiator;
import com.template.model.StudentBO;
import com.template.states.StudentState;
import com.template.workflow.StudentFlowData;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller extends CommonController {
    private final CordaRPCOps proxy;
    private final String secretKey = "venu";
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(RPConnector rpc) {
        this.proxy = rpc.proxy;
    }

    @Autowired
    protected RPConnector connector;

    @Autowired
    protected ObjectMapper mapper;

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here.";
    }

    @GetMapping("/hi")
    public String helloStudent(){
        return "Hello Student";
    }

    @PostMapping("/student")
    public ResponseEntity enterStudentDetails(@RequestBody StudentBO studentData){

        StudentContract.Commands command;
        SignedTransaction signedTransaction = null;
        StudentState studentState;
        StateAndRef<StudentState> studentStateStateAndRef = null;
        AbstractParty s1Party = null;

        command = new StudentContract.Commands.CREATE();

        if (studentData == null)
            throw new IllegalArgumentException("Invalid Request!");

        AbstractParty S2 = getPartyFromFullName("O=School2,L=New York,C=US");

        Set<Party> partyFrom = connector.getRPCops().partiesFromName(studentData.getS1Party(),false);
        Iterator<Party> parties = partyFrom.iterator();

        while(parties.hasNext()){
            s1Party = parties.next();
        }

        logger.info(" ********************************************** ");
        logger.info(" PartyA :",s1Party);
        logger.info(" PartyA :",S2);
        logger.info(" ********************************************** ");


        // Creating state and passing the key for encryption. Encryption is done in convertToStudentState function.

        studentState = convertToStudentState(studentData,secretKey,s1Party,S2);


        try{
            FlowHandle<SignedTransaction> flowHandle = connector.getRPCops().startFlowDynamic(StudentInitiator.class,new StudentFlowData(studentState,studentStateStateAndRef,command));
            signedTransaction = flowHandle.getReturnValue().get();
            logger.info(String.format("signed Tx id: %s", signedTransaction.getId().toString()));

        }catch (InterruptedException | ExecutionException e){
            logger.error(e.getMessage(), e.getCause());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok("Student data transferred Txn Id: " + signedTransaction);
    }

    @GetMapping("/student/results")
    public ResponseEntity getStudentDetails() throws Exception{

        List<StateAndRef<StudentState>> states = connector.getRPCops().vaultQuery(StudentState.class).getStates();

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(states));

    }


    // this api will return the decrypted result

    @GetMapping("/student/decryptresults")
    public ResponseEntity getStudentDetails1() throws Exception{

        List<StateAndRef<StudentState>> states = connector.getRPCops().vaultQuery(StudentState.class).getStates();

        // decryption function
        decryptToStudentState(states,secretKey);


        //System.out.println("mapper: "+mapper.writeValueAsString(states));
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(states));
    }


    @GetMapping("/student")
    public ResponseEntity getStudentDetailsById(@RequestParam String id) throws Exception{


        UniqueIdentifier uniqueIdentifier = UniqueIdentifier.Companion.fromString(id);
        Set<Class<StudentState>> contractStateTypes
                = new HashSet(Collections.singletonList(StudentState.class));

        QueryCriteria linearCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Arrays.asList(uniqueIdentifier),
                Vault.StateStatus.UNCONSUMED, contractStateTypes);

        Vault.Page<StudentState> results = connector.getRPCops().vaultQueryByCriteria(linearCriteria, StudentState.class);


        if (results.getStates().size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(results.getStates()));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("No Records found");
        }

    }

    private AbstractParty getPartyFromFullName(String partyName) {
        CordaX500Name x500Name =CordaX500Name.parse(partyName);
        return connector.getRPCops().wellKnownPartyFromX500Name(x500Name);
    }
}