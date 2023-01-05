import React, { useEffect, useState } from "react";
import { getSupportCase } from "../../features/supportcase/api/supportCaseApi";
import { useRouter } from "next/router";
import PageMeta from "../../features/common/components/PageMeta";
import CommentBox from "../../features/comment/components/CommentBox";
import CommentAddBox from "../../features/comment/components/CommentAddBox";

import SupportCaseBox from "../../features/supportcase/components/SupportCaseBox";
import Loading from "../../features/common/components/Loading";
import * as E from "fp-ts/Either";
import { useAuthUser } from "../../features/authentication/hooks/useAuthUser";
import { useAuthTenant } from "../../features/authentication/hooks/useAuthTenant";
import {SupportCase} from "../../features/supportcase/data/SupportCase";
import {Comment} from "../../features/comment/data/Comment";
import {CommentListItem} from "../../features/supportcase/data/CommentListItem";

export default function DetailPage() {
  const router = useRouter();
  const authTenant = useAuthTenant();
  const authUser = useAuthUser();
  const [errorMessage, setErrorMessage] = useState("");
  const [supportCase, setSupportCase] = useState<SupportCase | null>(
    null
  );
  const [commentListItems, setCommentListItems] = useState<CommentListItem[]>(
    []
  );

  const supportCaseId = (router.query?.id as string) ?? null;
  const authTenantId = authTenant?.id;

  useEffect(() => {
    (async () => {
      if (!authTenantId || !supportCaseId) return;
      const eitherData = await getSupportCase(authTenantId, supportCaseId);
      if (E.isRight(eitherData)) {
        setSupportCase(eitherData.right);
        setCommentListItems(eitherData.right.comments);
      } else {
        setErrorMessage(eitherData.left);
      }
    })();
  }, [supportCaseId, authTenantId]);

  const commentAddAccess =
    supportCase?.customer.id == authUser?.id ||
    supportCase?.supporter?.id == authUser?.id
      ? true
      : false;

  const handleBackToListClick = () => {
    router.push(`/support-cases`);
  };

  const handleCommentAdded = (addedComment: Comment) => {
    const commentListItem = {
      id: addedComment.id,
      commenter: addedComment.commenter,
      body: addedComment.body,
      commentedAt: addedComment.commentedAt,
      lastEditedAt: addedComment.lastEditedAt,
    };
    const newCommentListItems = [...commentListItems, commentListItem];
    setCommentListItems(newCommentListItems);
  };

  return (
    <>
      {!authTenant || !authUser ? (
        <Loading />
      ) : (
        <div className="container-fluid">
          <PageMeta title="support cases" />
          <div className="row">
            <div className="col-lg-3"></div>
            <div className="col-lg-6">
              <div className="d-flex justify-content-start mb-2">
                <button
                  type="button"
                  className="btn btn-primary"
                  onClick={() => handleBackToListClick()}
                >
                  Back to List
                </button>
              </div>

              {errorMessage && (
                <div className="text-warning text-center">{errorMessage}</div>
              )}
              {!supportCase && !commentListItems && !errorMessage && (
                <h4>Loading...</h4>
              )}
              {supportCase && commentListItems && !errorMessage && (
                <>
                  <SupportCaseBox
                    tenant={authTenant}
                    user={authUser}
                    supportCase={supportCase}
                  />
                  <div className="mt-4"></div>
                  {commentListItems.map((commentListItem, index) => (
                    <div key={index} className="mb-3">
                      <CommentBox
                        tenant={authTenant}
                        user={authUser}
                        supportCaseId={supportCaseId}
                        commentListItem={commentListItem}
                      />
                    </div>
                  ))}
                  {commentAddAccess && (
                    <CommentAddBox
                      tenant={authTenant}
                      user={authUser}
                      onCommentAdded={handleCommentAdded}
                      supportCaseId={supportCase.id}
                    />
                  )}

                  <div className="mb-5"></div>
                </>
              )}
            </div>
            <div className="col-lg-3"></div>
          </div>
          <style jsx>{``}</style>
        </div>
      )}
    </>
  );
}
