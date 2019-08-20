package com.template.states;

import com.template.contracts.StudentContract;
import com.template.schema.StudentSchemaV1;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(StudentContract.class)
public class StudentState implements LinearState, QueryableState {

    private UniqueIdentifier id;
    private String name;
    private String address;
    private String gender;
    private String hobby;
    private AbstractParty s1Party;
    private AbstractParty s2Party;



    public UniqueIdentifier getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getHobby() {
        return hobby;
    }


    public AbstractParty getS1Party() {
        return s1Party;
    }

    public AbstractParty getS2Party() {
        return s2Party;
    }

    @ConstructorForDeserialization
    public StudentState(UniqueIdentifier id,String name, String address, String gender, String hobby, AbstractParty s1Party, AbstractParty s2Party) {

        this.id = id;
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.hobby = hobby;
        this.s1Party = s1Party;
        this.s2Party = s2Party;
    }


    public StudentState(String name, String address, String gender, String hobby, AbstractParty s1Party, AbstractParty s2Party) {

        this.id = new UniqueIdentifier();
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.hobby = hobby;
        this.s1Party = s1Party;
        this.s2Party = s2Party;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return getId();
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {

        if (schema instanceof StudentSchemaV1){
            return new StudentSchemaV1.PersistentStudent(this);
        }
        else{
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }

    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new StudentSchemaV1());
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(s1Party,s2Party);
    }
}