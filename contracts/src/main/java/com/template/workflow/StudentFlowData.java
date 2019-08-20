package com.template.workflow;

import com.template.contracts.StudentContract;
import com.template.states.StudentState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class StudentFlowData {

    private StudentState studentState;
    private StateAndRef<StudentState> studentStateStateAndRef;
    private StudentContract.Commands command;


    public StudentFlowData(StudentState studentState, StateAndRef<StudentState> studentStateStateAndRef, StudentContract.Commands command) {
        this.studentState = studentState;
        this.studentStateStateAndRef = studentStateStateAndRef;
        this.command = command;
    }


    public StudentState getStudentState() {
        return studentState;
    }


    public StateAndRef<StudentState> getStudentStateStateAndRef() {
        return studentStateStateAndRef;
    }

    public StudentContract.Commands getCommand() {
        return command;
    }


}
