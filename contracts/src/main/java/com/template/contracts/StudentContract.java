package com.template.contracts;

import com.template.states.StudentState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class StudentContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String Student_CONTRACT_ID = "com.template.contracts.StudentContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(@NotNull LedgerTransaction tx) {
    final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(),Commands.class);
    final Commands commandData = command.getValue();
    final Set<PublicKey> setOfSigners = new HashSet<>(command.getSigners());

    if (commandData instanceof Commands.CREATE)
        verifyRequest(tx,setOfSigners);

    }

    private Set<PublicKey> keysFromParticipants(StudentState studentState){
        return studentState
                .getParticipants().stream()
                .map(AbstractParty::getOwningKey)
                .collect(Collectors.toSet());

    }

    private void verifyRequest(LedgerTransaction tx, Set<PublicKey> setOfSigners) {

        requireThat(req -> {
            req.using("No inputs should be consumed when requesting for an Request.",tx.getInputs().isEmpty());
            req.using("only one Request state should be created.",tx.getOutputs().size()==1);

        StudentState studentState = (StudentState) tx.getOutputStates().get(0);
        req.using("Signers must be part of participants",setOfSigners.equals(keysFromParticipants(studentState)));

            return null;
        } );
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class CREATE extends TypeOnlyCommandData implements Commands {}
    }
}