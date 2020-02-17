package com.truthbean.debbie.data.transformer;

import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-23 00:09.
 */
public class MapToBeanTransformer<M extends Map<String, Object>, Bean> implements DataTransformer<M, Bean> {

    @Override
    public Bean transform(M original) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public M reverse(Bean transformer) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
