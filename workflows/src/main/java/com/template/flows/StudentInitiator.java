package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.StudentContract;
import com.template.states.StudentState;
import com.template.workflow.FlowHelper;
import com.template.workflow.StudentFlowData;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndContract;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.CordaSerializable;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class StudentInitiator extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(StudentInitiator.class);
    private StudentState studentState;
    private StateAndRef<StudentState> studentStateStateAndRef;
    private StudentContract.Commands command;


    public StudentInitiator(StudentState studentState,
                            StateAndRef<StudentState> studentStateStateAndRef,
                            StudentContract.Commands command) {
        this.studentState = studentState;
        this.studentStateStateAndRef = studentStateStateAndRef;
        this.command = command;

    }

    public StudentInitiator(StudentFlowData studentFlowData) throws FlowException{
        this(studentFlowData.getStudentState(),
        studentFlowData.getStudentStateStateAndRef(),
        studentFlowData.getCommand());
    }

    private final Step INITIALISING = new Step("Performing initial steps.");
    private final Step BUILDING = new Step("Building the Transaction.");
    private final Step SIGNING = new Step("Signing transaction.");
    private final Step COLLECTING = new Step("Collecting counterparty signature.") {

        @Override
        public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.Companion.tracker();
        }
    };

    private final Step FINALISING = new Step("Finalising transaction.") {

        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    private final ProgressTracker progressTracker = new ProgressTracker(
            INITIALISING, BUILDING, SIGNING, COLLECTING, FINALISING
    );


    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // Initiator flow logic goes here.
        progressTracker.setCurrentStep(INITIALISING);
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        Command<StudentContract.Commands> txCommand = new Command<>(command,
                studentState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));

        progressTracker.setCurrentStep(BUILDING);
         validateTxCommand(command);
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .withItems(new StateAndContract(studentState, StudentContract.Student_CONTRACT_ID),txCommand);

        txBuilder.verify(getServiceHub());

        progressTracker.setCurrentStep(SIGNING);
        final SignedTransaction signTransaction = getServiceHub().signInitialTransaction(txBuilder);

        progressTracker.setCurrentStep(COLLECTING);
        Set<FlowSession> flowSessions = new HashSet<>();
         Party party = getOurIdentity();

        List<Party> parties = FlowHelper.getAllCounterParties(studentState.getParticipants(),party, getServiceHub());

        for (Party couterParty: parties){
            flowSessions.add(initiateFlow(couterParty));
        }

        final SignedTransaction signedTransaction = subFlow(new CollectSignaturesFlow(signTransaction,flowSessions,COLLECTING.childProgressTracker()));

        progressTracker.setCurrentStep(FINALISING);
        SignedTransaction fullySignedTxFinal = subFlow(new FinalityFlow(signedTransaction,flowSessions, FINALISING.childProgressTracker()));

        return fullySignedTxFinal;
    }

    @NotNull
    private boolean validateTxCommand(StudentContract.Commands command) {

        if (command instanceof StudentContract.Commands.CREATE)
            return true;
        else
            throw new IllegalArgumentException("Unidentifiable command!");
    }
}
