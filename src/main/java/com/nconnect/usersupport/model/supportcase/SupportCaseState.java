package com.nconnect.usersupport.model.supportcase;

import com.nconnect.usersupport.model.*;

public final class SupportCaseState {

  public final String id;
  public final Tenant tenant;
  public final Customer customer;
  public final String categoryId;
  public final String subject;
  public final String description;
  public final SupportCaseStatus status; //oppend, assigned, closed
  public final Supporter supporter;


  public static SupportCaseState identifiedBy(final String id) {
    return new SupportCaseState(id, null, null, null, null, null, null, null);
  }

  public SupportCaseState (final String id, final Tenant tenant, final Customer customer, final String categoryId, final String subject, final String description, final SupportCaseStatus status, final Supporter supporter) {
    this.id = id;
    this.tenant = tenant;
    this.customer = customer;
    this.categoryId = categoryId;
    this.subject = subject;
    this.description = description;
    this.status = status;
    this.supporter = supporter;
  }

  public SupportCaseState open(final String id, final Tenant tenant, final Customer customer, final String categoryId, final String subject, final String description, final SupportCaseStatus status) {
    return new SupportCaseState(id, tenant, customer, categoryId, subject, description, status, this.supporter);
  }

  public SupportCaseState close(final SupportCaseStatus status) {
    return new SupportCaseState(this.id, this.tenant, this.customer, this.categoryId, this.subject, this.description, status, this.supporter);
  }

  public SupportCaseState assign(final Supporter supporter, final SupportCaseStatus status) {
    return new SupportCaseState(this.id, this.tenant, this.customer, this.categoryId, this.subject, this.description, status, supporter);
  }

  public SupportCaseState edit(final String categoryId, final String subject, final String description) {
    return new SupportCaseState(this.id, this.tenant, this.customer, categoryId, subject, description, this.status, this.supporter);
  }

}
