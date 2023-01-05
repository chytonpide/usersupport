package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.infrastructure.CommentView;
import com.nconnect.usersupport.infrastructure.SupportCaseView;
import com.nconnect.usersupport.infrastructure.SupportCasesView;
import io.vlingo.xoom.common.Completes;

@SuppressWarnings("all")
public interface CommentQueries {
    Completes<CommentView> commentOf(String tenantId, String id);

}