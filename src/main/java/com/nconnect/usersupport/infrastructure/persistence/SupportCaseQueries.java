package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.infrastructure.SupportCaseView;
import com.nconnect.usersupport.infrastructure.SupportCasesView;
import io.vlingo.xoom.common.Completes;

import com.nconnect.usersupport.infrastructure.SupportCaseData;

@SuppressWarnings("all")
public interface SupportCaseQueries {
    Completes<SupportCaseView> supportCaseOf(String tenantId, String id);

    Completes<SupportCasesView> supportCases(String tenantId, int offset, int limit);

    Completes<SupportCasesView> supportCases(String tenantId, int offset, int limit, long total);

    Completes<Long> total(String tenantId);
}