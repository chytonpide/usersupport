package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.infrastructure.CustomerData;
import com.nconnect.usersupport.infrastructure.SupportCaseView;
import com.nconnect.usersupport.infrastructure.SupporterData;
import com.nconnect.usersupport.infrastructure.TenantData;
import com.nconnect.usersupport.model.supportcase.SupportCaseStatus;

import java.time.LocalDateTime;
import java.util.Collections;

public class SupportCaseViewStub {

    public static SupportCaseView firstInstance() {
        SupportCaseView instance = SupportCaseView.from(
                "1",
                TenantData.from("second-supportcase-tenant-id"),
                CustomerData.from("second-supportcase-customer-identity", "second-supportcase-customer-name"),
                "second-supportcase-categoryId",
                "second-supportcase-subject",
                "second-supportcase-description",
                SupportCaseStatus.OPENED,
                SupporterData.from("second-supportcase-supporter-identity", "second-supportcase-supporter-name"),
                Collections.emptyList(),
                Collections.emptyList(),
                LocalDateTime.now(),
                null,
                null,
                null);
        return instance;
    }

    public static SupportCaseView secondInstance() {
        SupportCaseView instance = SupportCaseView.from(
                "2",
                TenantData.from("second-supportcase-tenant-id"),
                CustomerData.from("second-supportcase-customer-identity", "second-supportcase-customer-name"),
                "second-supportcase-categoryId",
                "second-supportcase-subject",
                "second-supportcase-description",
                SupportCaseStatus.OPENED,
                SupporterData.from("second-supportcase-supporter-identity", "second-supportcase-supporter-name"),
                Collections.emptyList(),
                Collections.emptyList(),
                LocalDateTime.now(),
                null,
                null,
                null);
        return instance;
    }

}
