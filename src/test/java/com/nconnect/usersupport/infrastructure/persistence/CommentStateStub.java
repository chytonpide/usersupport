package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.model.Commenter;
import com.nconnect.usersupport.model.Customer;
import com.nconnect.usersupport.model.Supporter;
import com.nconnect.usersupport.model.Tenant;
import com.nconnect.usersupport.model.comment.CommentState;

public class CommentStateStub {

    public static CommentState firstInstance(String id, String supportCaseId) {
        final CommentState instance = new CommentState(
                "first-comment-id",
                Tenant.from("first-comment-tenant-id"),
                supportCaseId,
                Commenter.from("first-comment-commenter-name", "first-comment-commenter-id"),
                "first-comment-body",
                false);

        return instance;
    }

    public static CommentState secondInstance(String id, String supportCaseId) {
        final CommentState instance = new CommentState(
                "second-comment-id",
                Tenant.from("second-comment-tenant-id"),
                supportCaseId,
                Commenter.from("second-comment-commenter-name", "second-comment-commenter-id"),
                "second-comment-body",
                false);

        return instance;
    }

}
