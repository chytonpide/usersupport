import RoundedBlock from "../../common/components/RoundedBlock";
import React, { useState } from "react";
import { postSupportCase } from "../api/supportCaseApi";
import { useRouter } from "next/router";
import Loading from "../../common/components/Loading";
import * as E from 'fp-ts/Either'
import {useAuthUser} from "../../authentication/hooks/useAuthUser";
import {useAuthTenant} from "../../authentication/hooks/useAuthTenant";
import {CATEGORY} from "../category";


const SupportCaseOpenBox: React.FC = () => {
  const router = useRouter();
  const tenant = useAuthTenant();
  const user = useAuthUser();

  const [subject, setSubject] = useState("");
  const [selectedCategoryId, setSelectedCategoryId] = useState("");
  const [description, setDescription] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubjectChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSubject(e.currentTarget.value);
  };

  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedCategoryId(e.currentTarget.value);
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
    if(!user || !tenant)
      return

    setErrorMessage("");
    setLoading(true);

    const requestPayload = {
      categoryId: selectedCategoryId,
      subject: subject,
      description: description,
    };

    const eitherSupportCaseId = await postSupportCase(tenant.id, user, requestPayload);
    if (E.isRight(eitherSupportCaseId)) {
      router.push(`/support-cases/`);
    } else {
      setErrorMessage(eitherSupportCaseId.left);
      setLoading(false);
    }
  };

  return (
    <>
      {!(tenant && user) && <Loading />}
      {tenant && user && (
        <RoundedBlock>
          <div className="p-3">
            <div className="mb-3">
              <label>Subject</label>
              <input
                type="text"
                className={
                  loading ? "form-control disabled-input" : "form-control"
                }
                value={subject}
                onChange={(e) => handleSubjectChange(e)}
              />
            </div>
            <div className="mb-3">
              <label>Category</label>
              <select
                className={
                  loading ? "form-control disabled-input" : "form-control"
                }
                onChange={(e) => handleCategoryChange(e)}
                value={selectedCategoryId}
              >
                {categoryOptions}
              </select>
            </div>
            <div className="mb-3">
              <label>Description</label>
              <textarea
                className={
                  loading ? "form-control disabled-input" : "form-control"
                }
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
                className="btn btn-primary"
                onClick={() => handleSubmitClick()}
              >
                Open{" "}
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
      )}
    </>
  );
};
export default SupportCaseOpenBox;
