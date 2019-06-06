package com.truthbean.debbie.bean;

import com.truthbean.debbie.data.transformer.DataTransformer;

@BeanConfiguration
public class BeanConfigurationTest {

    @DebbieBean(name = "dataTransformer")
    public DataTransformer<Integer, Character> integerCharacterDataTransformer() {
        return new DataTransformer<>() {
            @Override
            public Character transform(Integer integer) {
                return (char) integer.byteValue();
            }

            @Override
            public Integer reverse(Character character) {
                return (int) character;
            }
        };
    }

}
