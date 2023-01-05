import RoundedBlock from "../../common/components/RoundedBlock";
import RoundedBlockHeader from "../../common/components/RoundedBlockHeader";
import { format } from "../../../utils/TimeUtils";
import React, { useState } from "react";
import CommentBoxEditMode from "./CommentBoxEditMode";
import YesOrNoDialog from "../../common/components/YesOrNoDialog";
import {deleteComment} from "../api/commentApi";
import * as E from "fp-ts/Either";
import {Tenant} from "../../authentication/data/Tenant";
import {User} from "../../authentication/data/User";
import {CommentListItem} from "../../supportcase/data/CommentListItem";
import {Comment} from "../data/Comment";

type Props = {
  tenant: Tenant;
  user: User;
  supportCaseId: string;
  commentListItem: CommentListItem;
};

const CommentBox: React.FC<Props> = (props) => {
  const [visible, setVisible] = useState(true);
  const [editMode, setEditMode] = useState(false);
  const [openYesOrNoDialog, setOpenYesOrNoDialog] = useState(false);
  const [deletionInProcess, setDeletionInProcess] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [commentListItem, setCommentListItem] = useState(props.commentListItem);

  const editOrDeleteAccess = props.user.id == props.commentListItem.commenter.id ? true : false;

  const handleDeleteClick = () => {
    setOpenYesOrNoDialog(true);
  };

  const handleDeleteYesClick = async () => {
    setDeletionInProcess(true);

    const result = await deleteComment(
      props.tenant.id,
      props.commentListItem.id,
      props.user,
    );

    if (E.isRight(result)) {
      setOpenYesOrNoDialog(false);
      setVisible(false);
    } else {
      setDeletionInProcess(false);
      setOpenYesOrNoDialog(false);
      setErrorMessage(result.left);
    }
  };

  const handleDeleteNoClick = () => {
    setOpenYesOrNoDialog(false);
  };

  const handleEditClick = () => {
    setEditMode(true);
  };

  const handleEditCanceled = () => {
    setEditMode(false);
  };

  const handleCommentEdited = (editedComment: Comment) => {
    let newCommentListItem = commentListItem;
    newCommentListItem.body = editedComment.body
    newCommentListItem.lastEditedAt = editedComment.lastEditedAt

    setCommentListItem(newCommentListItem);
    setEditMode(false);
  };

  return (
    <>
      {visible && editMode && (
        <CommentBoxEditMode
          tenant={props.tenant}
          user={props.user}
          supportCaseId={props.supportCaseId}
          commentListItem={commentListItem}
          onCommentEdited={handleCommentEdited}
          onEditCanceled={handleEditCanceled}
        />
      )}
      {visible && !editMode && (
        <RoundedBlock>
          <RoundedBlockHeader>
            <div>
              <div className="d-flex justify-content-between p-2">
                <div className="d-flex flex-column">
                  <div className="align-self-center">
                    <span className="fs-6 fw-bold me-1">
                      {commentListItem.commenter.name}
                    </span>
                    <span className="fs-6">
                      commented at {format(commentListItem.commentedAt)}
                    </span>
                  </div>
                </div>
                {editOrDeleteAccess && (
                  <div className="d-flex justify-content-end">
                    <button
                      onClick={handleEditClick}
                      type="button"
                      className="btn btn-sm btn-primary me-2"
                    >
                      Edit
                    </button>
                    <button
                      onClick={handleDeleteClick}
                      type="button"
                      className="btn btn-sm btn-secondary"
                    >
                      Delete
                    </button>
                  </div>
                )}
              </div>
              {errorMessage && (
                <div className="d-flex justify-content-end">
                  <span className="fs-7 text-warning p-2">{errorMessage}</span>
                </div>
              )}
            </div>
          </RoundedBlockHeader>
          <div className="p-3">
            <div className="form-control disabled-input">
              <pre>{commentListItem.body}</pre>
            </div>
          </div>
          <style jsx>{`
            pre {
              margin: 0px;
            }
          `}</style>
        </RoundedBlock>
      )}
      <YesOrNoDialog
        open={openYesOrNoDialog}
        onYesClick={handleDeleteYesClick}
        onNoClick={handleDeleteNoClick}
        messages={["Are you sure to delete it?"]}
        loading={deletionInProcess}
      />
    </>
  );
};
export default CommentBox;
