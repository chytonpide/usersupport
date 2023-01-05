import RoundedBlock from "../../common/components/RoundedBlock";
import { useState } from "react";
import {getComment, postComment} from "../api/commentApi";
import PostCommentPayload = app.data.request.PostCommentPayload;
import * as E from "fp-ts/Either";
import {sleep} from "../../../utils/SleepUtils";
import {Tenant} from "../../authentication/data/Tenant";
import {User} from "../../authentication/data/User";
import {Comment} from "../data/Comment";


type Props = {
  tenant: Tenant
  user: User
  supportCaseId: string;
  onCommentAdded: (addedComment: Comment) => void;
};

const CommentAddBox: React.FC<Props> = ({tenant, user, supportCaseId, onCommentAdded }) => {
  const [errorMessage, setErrorMessage] = useState("")
  const [commentBody, setCommentBody] = useState("");
  const [loading, setLoading] = useState(false);


  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setCommentBody(e.currentTarget.value);
  };

  const handleAddCommentClick = async () => {
    setErrorMessage("");
    setLoading(true);

    const commenter = {
      id: user.id,
      name: user.name,
    };

    const requestPayload : PostCommentPayload = {
      supportCaseId : supportCaseId,
      body : commentBody,
    }

    const eitherCommentId = await postComment(
      tenant.id,
      user,
      requestPayload
    );

    if (E.isRight(eitherCommentId)) {
      const commentId = eitherCommentId.right

      let commentLoaded = false;
      for (let retries = 0; retries < 3; retries++) {
        const eitherComment = await getComment(tenant.id, commentId);
        if (E.isRight(eitherComment)) {
          commentLoaded = true;
          onCommentAdded(eitherComment.right);
          break;
        }
        await sleep(1000)
      }

      if(!commentLoaded) {
        setErrorMessage("Fail to load the comment. Please reload page.");
      }

      setCommentBody("")
      setLoading(false);

    } else {
      setErrorMessage(eitherCommentId.left);
      setLoading(false);
    }
  };

  return (
    <RoundedBlock>
      <div className="p-3">
        <div className="mb-3">
          <textarea
            className={loading ? "form-control disabled-input" : "form-control"}
            disabled={loading}
            rows={4}
            onChange={(e) => handleChange(e)}
            value={commentBody}
          />
          {errorMessage && <span className="fs-7 text-warning">{errorMessage}</span>}
        </div>
        <div className="d-flex justify-content-end">
          <button
            disabled={loading}
            type="button"
            className="btn btn-primary"
            onClick={() => handleAddCommentClick()}
          >
            Add Comment{" "}
            {loading && (
              <div
                className="spinner-border spinner-border-sm text-light"
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
export default CommentAddBox;
