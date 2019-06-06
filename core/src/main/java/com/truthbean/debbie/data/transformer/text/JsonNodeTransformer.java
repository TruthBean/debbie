package com.truthbean.debbie.data.transformer.text;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 16:19.
 */
public class JsonNodeTransformer extends JsonTransformer<JsonNode> {
    public JsonNodeTransformer() {
        super(JsonNode.class);
    }
}
