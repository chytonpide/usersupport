import RoundedBlock from "../../common/components/RoundedBlock";
import React, { useState } from "react";


import {
  patchSupportCaseContent,
  postSupportCase,
  getSupportCase,
  getEditedSupportCaseAfterTime
} from "../api/supportCaseApi";
import { CATEGORY } from "../category";
import * as E from "fp-ts/Either";
import {Tenant} from "../../authentication/data/Tenant";
import {User} from "../../authentication/data/User";
import {SupportCase} from "../data/SupportCase";

type Props = {
  tenant: Tenant;
  user: User;
  supportCase: SupportCase;
  onCancelClick: () => void;
  onSupportCaseModified: (editedSupportCase: SupportCase) => void;
};

const SupportCaseBoxEditMode: React.FC<Props> = ({
  tenant,
  user,
  supportCase,
  onCancelClick,
  onSupportCaseModified,
}) => {
  const [subject, setSubject] = useState(supportCase.subject);
  const [categoryId, setCategoryId] = useState(
    supportCase.categoryId
  );
  const [description, setDescription] = useState(supportCase.description);
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubjectChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSubject(e.currentTarget.value);
  };

  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setCategoryId(e.currentTarget.value);
  };

  const handleDescriptionChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setDescription(e.currentTarget.value);
  };

  const categoryOptions = [
    <option key="none" value="">
      -
    </option>,
  ].concat(
    CATEGORY.map((categoryEntry, index) => {
      return (
        <option key={index} value={categoryEntry.id}>
          {categoryEntry.name}
        </option>
      );
    })
  );

  const handleSubmitClick = async () => {
    setErrorMessage("");
    setLoading(true);

    const requestPayload = {
      categoryId: categoryId,
      subject: subject,
      description: description,
    };


    const result = await patchSupportCaseContent(
      tenant.id,
      supportCase.id,
      user,
      requestPayload
    );
    if (E.isRight(result)) {

      const eitherSupportCase = await getEditedSupportCaseAfterTime(tenant.id, supportCase.id, supportCase.lastEditedAt);

      if (E.isRight(eitherSupportCase)) {
        onSupportCaseModified(eitherSupportCase.right);
      } else {
        setErrorMessage("Succeeded in saving comment but fail to load the comment");
        setLoading(false);
      }

    } else {
      setErrorMessage(result.left);
      setLoading(false);
    }
  };

  return (
    <RoundedBlock>
      <div className="p-3">
        <div className="mb-3">
          <label>Subject</label>
          <input
            type="text"
            className={loading ? "form-control disabled-input" : "form-control"}
            value={subject}
            onChange={(e) => handleSubjectChange(e)}
          />
        </div>
        <div className="mb-3">
          <label>Category</label>
          <select
            className={loading ? "form-control disabled-input" : "form-control"}
            onChange={(e) => handleCategoryChange(e)}
            value={categoryId}
          >
            {categoryOptions}
          </select>
        </div>
        <div className="mb-3">
          <label>Description</label>
          <textarea
            className={loading ? "form-control disabled-input" : "form-control"}
            disabled={loading}
            rows={4}
            onChange={(e) => handleDescriptionChange(e)}
            value={description}
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
            onClick={onCancelClick}
          >
            Cancel
          </button>
          <button
            disabled={loading}
            type="button"
            className="btn btn-primary"
            onClick={() => handleSubmitClick()}
          >
            Edit{" "}
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
export default SupportCaseBoxEditMode;
