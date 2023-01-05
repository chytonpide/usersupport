import RoundedBlock from "../../common/components/RoundedBlock";
import React, { useState } from "react";
import * as E from "fp-ts/Either";
import {getComment, patchCommentBody} from "../api/commentApi";
import {sleep} from "../../../utils/SleepUtils";
import {Comment} from "../data/Comment";
import {Tenant} from "../../authentication/data/Tenant";
import {User} from "../../authentication/data/User";
import {CommentListItem} from "../../supportcase/data/CommentListItem";

type Props = {
  tenant: Tenant;
  user: User;
  supportCaseId: string;
  commentListItem: CommentListItem;
  onCommentEdited: (editedComment: Comment) => void;
  onEditCanceled: () => void;
};

const CommentBoxEditMode: React.FC<Props> = ({
  tenant,
  user,
  supportCaseId,
  commentListItem,
  onCommentEdited,
  onEditCanceled,
}) => {
  const [errorMessage, setErrorMessage] = useState("");
  const [body, setBody] = useState(commentListItem.body);
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setBody(e.currentTarget.value);
  };

  const handleCancelClick = () => {
    onEditCanceled();
  };

  const handleEditCommentClick = async () => {
    setErrorMessage("");
    setLoading(true);

    const requestPayload = {
      supportCaseId: supportCaseId,
      body: body,
    };

    const result = await patchCommentBody(
      tenant.id,
      commentListItem.id,
      user,
      requestPayload
    );
    if (E.isRight(result)) {

      const eitherPatchedComment = await getEditedCommentAfterThan(commentListItem);
      if(E.isRight(eitherPatchedComment)) {
        onCommentEdited(eitherPatchedComment.right);
      } else {
        setErrorMessage(eitherPatchedComment.left);
      }

      setLoading(false);

    } else {
      setErrorMessage(result.left);
      setLoading(false);
    }
  };

  const getEditedCommentAfterThan = async (commentListItem: CommentListItem) => {
    for (let retries = 0; retries < 3; retries++) {
      const eitherComment = await getComment(tenant.id, commentListItem.id);
      if (E.isRight(eitherComment)) {
        if(eitherComment.right.lastEditedAt != commentListItem.lastEditedAt ) {
          return eitherComment
        }
      } else {
        return eitherComment
      }
      await sleep(1000)
    }
    return E.left("Fail to load patched comment");
  }

  return (
    <RoundedBlock>
      <div className="p-3">
        <div className="mb-3">
          <textarea
            className={loading ? "form-control disabled-input" : "form-control"}
            disabled={loading}
            rows={4}
            onChange={(e) => handleChange(e)}
            value={body}
          />
        </div>
        <div className="d-flex justify-content-end">
          {errorMessage && (
            <div className="fs-7 text-warning">{errorMessage}</div>
          )}
        </div>
        <div className="d-flex justify-content-end">
          <button
            disabled={loading}
            type="button"
            className="btn btn-secondary me-2"
            onClick={() => handleCancelClick()}
          >
            Cancel
          </button>

          <button
            disabled={loading}
            type="button"
            className="btn btn-primary"
            onClick={() => handleEditCommentClick()}
          >
            Edit Comment{" "}
            {loading && (
              <div
                className="spinner-border spinner-border-sm text-light ms-1"
                role="status"
              >
                <span className="visually-hidden">Loading...</span>
              </div>
            )}
          </button>
        </div>
      </div>
      <style jsx>{`
        pre {
          margin: 0px;
        }
      `}</style>
    </RoundedBlock>
  );
};
export default CommentBoxEditMode;
