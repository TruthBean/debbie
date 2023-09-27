package com.truthbean.debbie.file.test;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockUserPrincipalLookupService extends UserPrincipalLookupService {
    @Override
    public UserPrincipal lookupPrincipalByName(String name) throws IOException {
        return null;
    }

    @Override
    public GroupPrincipal lookupPrincipalByGroupName(String group) throws IOException {
        return null;
    }
}
