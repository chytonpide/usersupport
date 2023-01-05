package com.nconnect.usersupport.model;

import io.vlingo.xoom.common.Completes;

public interface TenantOwned {
    Completes<Boolean> isOwnedBy(String tenantId);
}
