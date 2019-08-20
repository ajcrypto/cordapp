package com.template.schema;

import com.google.common.collect.ImmutableList;
import com.template.states.StudentState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

public class StudentSchemaV1 extends MappedSchema {

    public StudentSchemaV1() {
        super(StudentSchema.class, 1, ImmutableList.of(PersistentStudent.class));
    }


    @Entity
    @Table(name = "Student")
    public static class PersistentStudent extends PersistentState{

        @Column(name = "Id")
        private String id;


        @Column(name = "Name")
        @Convert(converter = Converter.class)
        private String name;


        @Column(name = "Address")
        @Convert(converter = Converter.class)
        private String address;

        @Column(name = "Gender")
        private String gender;

        @Column(name = "Hobby")
        private String hobby;

        @Column(name = "S1Party")
        private AbstractParty s1Party;

        @Column(name = "S2Party")
        private AbstractParty s2Party;



        @ConstructorForDeserialization
            public PersistentStudent(UniqueIdentifier id, String name, String address, String gender, String hobby, AbstractParty s1Party, AbstractParty s2Party) {
            super();
            this.id = id.toString();
            this.name = name;
            this.address = address;
            this.gender = gender;
            this.hobby = hobby;
            this.s1Party = s1Party;
            this.s2Party = s2Party;
        }


        public PersistentStudent(StudentState studentState) {
            this.id = studentState.getId().toString();
            this.name = studentState.getName();
            this.address = studentState.getAddress();
            this.gender = studentState.getGender();
            this.hobby = studentState.getHobby();
        }

    }

}
