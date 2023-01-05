package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.model.Customer;
import com.nconnect.usersupport.model.Supporter;
import com.nconnect.usersupport.model.Tenant;
import com.nconnect.usersupport.model.supportcase.SupportCaseState;
import com.nconnect.usersupport.model.supportcase.SupportCaseStatus;

public class SupportCaseStateStub {

    public static SupportCaseState firstInstance(String id) {
        SupportCaseState instance = new SupportCaseState(id,
                Tenant.from("first-supportcase-tenant-id"),
                Customer.from("first-supportcase-customer-id", "first-supportcase-customer-name"),
                "first-supportcase-categoryId",
                "first-supportcase-subject",
                "first-supportcase-description",
                SupportCaseStatus.OPENED,
                Supporter.from("first-supportcase-supporter-id", "first-supportcase-supporter-name"));

        return instance;
    }

    public static SupportCaseState secondInstance(String id) {
        SupportCaseState instance = new SupportCaseState(id,
                Tenant.from("second-supportcase-tenant-id"),
                Customer.from("second-supportcase-customer-id", "second-supportcase-customer-name"),
                "second-supportcase-categoryId",
                "second-supportcase-subject",
                "second-supportcase-description",
                SupportCaseStatus.OPENED,
                Supporter.from("second-supportcase-supporter-id", "second-supportcase-supporter-name"));

        return instance;
    }

}
