package com.example.Beans;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableListDeserializer extends JsonDeserializer<ObservableList<String>> {
    @Override
    public ObservableList<String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);

        ObservableList<String> list = FXCollections.observableArrayList();
        for (JsonNode elementNode : node) {
            String element = elementNode.asText();
            list.add(element);
        }

        return list;
    }
}
