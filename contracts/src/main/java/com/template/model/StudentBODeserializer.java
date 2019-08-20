package com.template.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class StudentBODeserializer extends JsonDeserializer<StudentBO> {
    @Override
    public StudentBO deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Object oc = jp.getCodec();
        JsonNode node = ((ObjectCodec) oc).readTree(jp);



    return new StudentBO(
            node.get("name").asText(),
            node.get("address").asText(),
            node.get("gender").asText(),
            node.get("hobby").asText(),
            node.get("s1Party").asText(),
            node.get("s2Party").asText()
    ) ;
    }
}
